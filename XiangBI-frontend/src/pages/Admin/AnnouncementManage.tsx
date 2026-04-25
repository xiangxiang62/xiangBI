/**
 * XiangBI File: src/pages/Admin/AnnouncementManage.tsx
 * Responsibility: Page-level UI module.
 */
import {
  addAnnouncementUsingPOST,
  deleteAnnouncementUsingPOST,
  listAnnouncementByPageUsingPOST,
  updateAnnouncementUsingPOST,
} from '@/services/yubi/announcementController';
import {
  ModalForm,
  PageContainer,
  ProColumns,
  ProFormDateTimePicker,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { ActionType } from '@ant-design/pro-table';
import { Button, Descriptions, message, Modal, Popconfirm, Space, Tag } from 'antd';
import moment from 'moment';
import { useRef, useState } from 'react';

const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '已发布', value: 1 },
];

const statusEnum = {
  0: { text: '草稿' },
  1: { text: '已发布' },
};

const priorityOptions = [
  { label: '低', value: 0 },
  { label: '中', value: 1 },
  { label: '高', value: 2 },
];

const priorityTextMap: Record<number, string> = {
  0: '低',
  1: '中',
  2: '高',
};

const formatDateTime = (value?: string) => {
  if (!value) {
    return '-';
  }
  const date = moment(value);
  return date.isValid() ? date.format('YYYY-MM-DD HH:mm:ss') : value;
};

const AnnouncementManagePage = () => {
  const actionRef = useRef<ActionType>();
  const [createOpen, setCreateOpen] = useState(false);
  const [currentRow, setCurrentRow] = useState<API.Announcement>();
  const [previewRow, setPreviewRow] = useState<API.Announcement>();

  const handlePublish = async (record: API.Announcement) => {
    const hide = message.loading('正在发布');
    try {
      const res = await updateAnnouncementUsingPOST({
        id: record.id,
        title: record.title,
        content: record.content,
        status: 1,
        priority: record.priority,
        publishTime: moment().format('YYYY-MM-DD HH:mm:ss'),
        userId: record.userId,
      });
      if (res.code === 0) {
        message.success('发布成功');
        actionRef.current?.reload();
        if (previewRow?.id === record.id) {
          setPreviewRow({
            ...record,
            status: 1,
            publishTime: moment().format('YYYY-MM-DD HH:mm:ss'),
          });
        }
      } else {
        message.error(res.message || '发布失败');
      }
    } finally {
      hide();
    }
  };

  const columns: ProColumns<API.Announcement>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
      search: false,
    },
    {
      title: '公告标题',
      dataIndex: 'title',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      valueType: 'select',
      valueEnum: statusEnum,
      fieldProps: {
        options: statusOptions,
      },
      width: 100,
      render: (_, record) => (
        <Tag color={record.status === 1 ? 'green' : 'default'}>
          {record.status === 1 ? '已发布' : '草稿'}
        </Tag>
      ),
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      width: 90,
      search: false,
      render: (_, record) => priorityTextMap[record.priority ?? 0] || '-',
    },
    {
      title: '发布人',
      dataIndex: 'userName',
      width: 120,
      search: false,
    },
    {
      title: '发布时间',
      dataIndex: 'publishTime',
      width: 180,
      search: false,
      render: (_, record) => formatDateTime(record.publishTime),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      search: false,
      render: (_, record) => formatDateTime(record.createTime),
    },
    {
      title: '操作',
      valueType: 'option',
      width: 240,
      render: (_, record) => {
        const actions = [
          <Button key="preview" type="link" onClick={() => setPreviewRow(record)}>
            查看
          </Button>,
          <Button key="edit" type="link" onClick={() => setCurrentRow(record)}>
            编辑
          </Button>,
        ];

        if (record.status !== 1) {
          actions.push(
            <Popconfirm
              key="publish"
              title="确认发布这条公告吗？"
              onConfirm={() => handlePublish(record)}
            >
              <Button type="link">发布</Button>
            </Popconfirm>,
          );
        }

        actions.push(
          <Popconfirm
            key="delete"
            title="确认删除这条公告吗？"
            onConfirm={async () => {
              const hide = message.loading('正在删除');
              try {
                const res = await deleteAnnouncementUsingPOST({ id: record.id });
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
        );

        return actions;
      },
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.Announcement>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        search={{
          labelWidth: 88,
        }}
        request={async (params) => {
          const res = await listAnnouncementByPageUsingPOST({
            current: params.current,
            pageSize: params.pageSize,
            title: params.title,
            status: params.status,
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
        toolBarRender={() => [
          <Button key="create" type="primary" onClick={() => setCreateOpen(true)}>
            新建公告
          </Button>,
        ]}
      />

      <ModalForm<API.AnnouncementAddRequest>
        title="新建公告"
        open={createOpen}
        modalProps={{
          destroyOnClose: true,
          onCancel: () => setCreateOpen(false),
        }}
        initialValues={{
          status: 0,
          priority: 0,
        }}
        onFinish={async (values) => {
          const res = await addAnnouncementUsingPOST(values);
          if (res.code !== 0) {
            message.error(res.message || '创建失败');
            return false;
          }
          message.success('创建成功');
          setCreateOpen(false);
          actionRef.current?.reload();
          return true;
        }}
      >
        <ProFormText
          name="title"
          label="公告标题"
          rules={[{ required: true, message: '请输入公告标题' }]}
        />
        <ProFormTextArea
          name="content"
          label="公告内容"
          rules={[{ required: true, message: '请输入公告内容' }]}
          fieldProps={{ rows: 6 }}
        />
        <ProFormSelect
          name="status"
          label="状态"
          options={statusOptions}
          rules={[{ required: true, message: '请选择状态' }]}
        />
        <ProFormSelect
          name="priority"
          label="优先级"
          options={priorityOptions}
          rules={[{ required: true, message: '请选择优先级' }]}
        />
        <ProFormDateTimePicker
          name="publishTime"
          label="发布时间"
          fieldProps={{ format: 'YYYY-MM-DD HH:mm:ss' }}
          convertValue={(value) => (value ? moment(value) : undefined)}
          transform={(value) => ({
            publishTime: value ? moment(value).format('YYYY-MM-DD HH:mm:ss') : undefined,
          })}
        />
      </ModalForm>

      <ModalForm<API.AnnouncementUpdateRequest>
        title="编辑公告"
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
          const res = await updateAnnouncementUsingPOST({
            id: currentRow.id,
            title: values.title,
            content: values.content,
            status: values.status,
            priority: values.priority,
            publishTime: values.publishTime,
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
        <ProFormText
          name="title"
          label="公告标题"
          rules={[{ required: true, message: '请输入公告标题' }]}
        />
        <ProFormTextArea
          name="content"
          label="公告内容"
          rules={[{ required: true, message: '请输入公告内容' }]}
          fieldProps={{ rows: 6 }}
        />
        <ProFormSelect
          name="status"
          label="状态"
          options={statusOptions}
          rules={[{ required: true, message: '请选择状态' }]}
        />
        <ProFormSelect
          name="priority"
          label="优先级"
          options={priorityOptions}
          rules={[{ required: true, message: '请选择优先级' }]}
        />
        <ProFormDateTimePicker
          name="publishTime"
          label="发布时间"
          fieldProps={{ format: 'YYYY-MM-DD HH:mm:ss' }}
          convertValue={(value) => (value ? moment(value) : undefined)}
          transform={(value) => ({
            publishTime: value ? moment(value).format('YYYY-MM-DD HH:mm:ss') : undefined,
          })}
        />
      </ModalForm>

      <Modal
        title="公告详情"
        open={!!previewRow}
        width={720}
        footer={null}
        destroyOnClose
        onCancel={() => setPreviewRow(undefined)}
      >
        {previewRow && (
          <Space direction="vertical" size={16} style={{ display: 'flex' }}>
            <Descriptions bordered column={2}>
              <Descriptions.Item label="公告标题">{previewRow.title || '-'}</Descriptions.Item>
              <Descriptions.Item label="状态">
                {previewRow.status === 1 ? '已发布' : '草稿'}
              </Descriptions.Item>
              <Descriptions.Item label="优先级">
                {priorityTextMap[previewRow.priority ?? 0] || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="发布人">{previewRow.userName || '-'}</Descriptions.Item>
              <Descriptions.Item label="发布时间">
                {formatDateTime(previewRow.publishTime)}
              </Descriptions.Item>
              <Descriptions.Item label="创建时间">
                {formatDateTime(previewRow.createTime)}
              </Descriptions.Item>
              <Descriptions.Item label="公告内容" span={2}>
                <div style={{ whiteSpace: 'pre-wrap' }}>{previewRow.content || '-'}</div>
              </Descriptions.Item>
            </Descriptions>
          </Space>
        )}
      </Modal>
    </PageContainer>
  );
};

export default AnnouncementManagePage;

