/**
 * XiangBI File: src/pages/Admin/ChartManage.tsx
 * Responsibility: Page-level UI module.
 */
import { CHART_TYPE, CHART_TYPE_JSON } from '@/constants/chart/chartType';
import {
  deleteChartUsingPOST,
  listChartVOByPageUsingPOST,
  updateChartUsingPOST,
} from '@/services/yubi/chartController';
import {
  ModalForm,
  PageContainer,
  ProColumns,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { ActionType } from '@ant-design/pro-table';
import { Button, Descriptions, message, Modal, Popconfirm, Space, Tag } from 'antd';
import ReactECharts from 'echarts-for-react';
import { useRef, useState } from 'react';

const statusColorMap: Record<string, string> = {
  成功: 'success',
  失败: 'error',
  生成中: 'processing',
  等待中: 'default',
};

const getChartOption = (chart?: API.Chart) => {
  if (!chart?.genChart) {
    return undefined;
  }
  try {
    return JSON.parse(chart.genChart);
  } catch (error) {
    return undefined;
  }
};

const ChartManagePage = () => {
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.Chart>();
  const [previewRow, setPreviewRow] = useState<API.Chart>();

  const columns: ProColumns<API.Chart>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
      search: false,
    },
    {
      title: '图表名称',
      dataIndex: 'name',
      ellipsis: true,
    },
    {
      title: '分析目标',
      dataIndex: 'goal',
      ellipsis: true,
      search: false,
    },
    {
      title: '图表类型',
      dataIndex: 'chartType',
      valueType: 'select',
      valueEnum: CHART_TYPE_JSON,
      fieldProps: {
        options: CHART_TYPE,
      },
      width: 120,
    },
    {
      title: '创建人',
      dataIndex: 'userId',
      width: 90,
      search: false,
    },
    {
      title: '执行状态',
      dataIndex: 'execMessage',
      width: 110,
      search: false,
      render: (_, record) => {
        const text = record.execMessage || '未知';
        return <Tag color={statusColorMap[text] || 'default'}>{text}</Tag>;
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      width: 180,
      search: false,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      width: 180,
      search: false,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 180,
      render: (_, record) => [
        <Button
          key="preview"
          type="link"
          onClick={() => {
            setPreviewRow(record);
          }}
        >
          查看
        </Button>,
        <Button
          key="edit"
          type="link"
          onClick={() => {
            setCurrentRow(record);
          }}
        >
          编辑
        </Button>,
        <Popconfirm
          key="delete"
          title="确认删除这个图表吗？"
          onConfirm={async () => {
            const hide = message.loading('正在删除');
            try {
              const res = await deleteChartUsingPOST({ id: record.id });
              if (res.code === 0) {
                message.success('删除成功');
                actionRef.current?.reload();
              } else {
                message.error(res.message || '删除失败');
              }
            } finally {
              hide();
            }
          }}
        >
          <Button danger type="link">
            删除
          </Button>
        </Popconfirm>,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.Chart>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        search={{
          labelWidth: 88,
        }}
        request={async (params) => {
          const res = await listChartVOByPageUsingPOST({
            current: params.current,
            pageSize: params.pageSize,
            name: params.name,
            chartType: params.chartType,
            sortField: 'createTime',
            sortOrder: 'desc',
          });

          return {
            data: res.data?.records || [],
            success: res.code === 0,
            total: Number(res.data?.total) || 0,
          };
        }}
        pagination={{
          showSizeChanger: true,
          pageSize: 10,
        }}
        options={false}
        dateFormatter="string"
      />
      <ModalForm<API.ChartUpdateRequest>
        title="编辑图表"
        open={!!currentRow}
        initialValues={currentRow}
        modalProps={{
          destroyOnClose: true,
          onCancel: () => setCurrentRow(undefined),
        }}
        onFinish={async (values) => {
          if (!currentRow?.id) {
            return false;
          }
          const res = await updateChartUsingPOST({
            id: currentRow.id,
            name: values.name,
            goal: values.goal,
            chartType: values.chartType,
            userId: values.userId,
          });
          if (res.code !== 0) {
            message.error(res.message || '更新失败');
            return false;
          }
          message.success('更新成功');
          setCurrentRow(undefined);
          actionRef.current?.reload();
          return true;
        }}
      >
        <ProFormText name="name" label="图表名称" rules={[{ required: true, message: '请输入图表名称' }]} />
        <ProFormTextArea
          name="goal"
          label="分析目标"
          rules={[{ required: true, message: '请输入分析目标' }]}
        />
        <ProFormSelect
          name="chartType"
          label="图表类型"
          options={CHART_TYPE}
          rules={[{ required: true, message: '请选择图表类型' }]}
        />
        <ProFormDigit
          name="userId"
          label="创建人 ID"
          fieldProps={{ precision: 0 }}
          rules={[{ required: true, message: '请输入创建人 ID' }]}
        />
      </ModalForm>
      <Modal
        title="图表详情"
        open={!!previewRow}
        width={960}
        footer={null}
        destroyOnClose
        onCancel={() => setPreviewRow(undefined)}
      >
        {previewRow && (
          <Space direction="vertical" size={16} style={{ display: 'flex' }}>
            <Descriptions bordered column={2}>
              <Descriptions.Item label="图表名称">{previewRow.name || '-'}</Descriptions.Item>
              <Descriptions.Item label="图表类型">{previewRow.chartType || '-'}</Descriptions.Item>
              <Descriptions.Item label="创建人">{previewRow.userId || '-'}</Descriptions.Item>
              <Descriptions.Item label="执行状态">{previewRow.execMessage || '-'}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{previewRow.createTime || '-'}</Descriptions.Item>
              <Descriptions.Item label="更新时间">{previewRow.updateTime || '-'}</Descriptions.Item>
              <Descriptions.Item label="分析目标" span={2}>
                {previewRow.goal || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="分析结论" span={2}>
                {previewRow.genResult || '-'}
              </Descriptions.Item>
            </Descriptions>
            {getChartOption(previewRow) ? (
              <ReactECharts option={getChartOption(previewRow)} style={{ height: 400 }} />
            ) : null}
          </Space>
        )}
      </Modal>
    </PageContainer>
  );
};

export default ChartManagePage;

