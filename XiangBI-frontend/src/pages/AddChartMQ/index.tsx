/**
 * XiangBI File: src/pages/AddChartMQ/index.tsx
 * Responsibility: Page-level UI module.
 */
import { ByAiAsyncMQUsingPOST } from '@/services/yubi/chartController';
import { UploadOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, message, Select, Space, Upload } from 'antd';
import { useForm } from 'antd/es/form/Form';
import TextArea from 'antd/es/input/TextArea';
import React, { useState } from 'react';

const chartTypeOptions = [
  { value: '折线图', label: '折线图' },
  { value: '柱状图', label: '柱状图' },
  { value: '堆叠图', label: '堆叠图' },
  { value: '饼图', label: '饼图' },
  { value: '雷达图', label: '雷达图' },
  { value: '热力图', label: '热力图' },
  { value: '漏斗图', label: '漏斗图' },
  { value: '散点图', label: '散点图' },
  { value: '仪表盘', label: '仪表盘' },
  { value: 'K线图', label: 'K线图' },
  { value: '条形图', label: '条形图' },
  { value: '区域图', label: '区域图' },
  { value: '面积热力图', label: '面积热力图' },
  { value: '三维散点图', label: '三维散点图' },
];

/**
 * 基于 RabbitMQ 的图表异步生成页面。
 */
const AddChartMQPage: React.FC = () => {
  const [form] = useForm();
  const [submitting, setSubmitting] = useState(false);

  /**
   * 提交 MQ 生成任务。
   */
  const onFinish = async (values: any) => {
    if (submitting) {
      return;
    }
    if (!values?.file?.file?.originFileObj) {
      message.error('请上传 CSV 文件');
      return;
    }

    setSubmitting(true);
    const params = {
      goal: values.goal,
      name: values.name,
      chartType: values.chartType,
    };

    try {
      const res = await ByAiAsyncMQUsingPOST(params, {}, values.file.file.originFileObj);
      if (res.code === 0 && res.data?.chartId) {
        message.success(`任务提交成功，图表 ID：${res.data.chartId}，请到“我的图表”查看结果`);
        form.resetFields();
      } else {
        message.error(res.message || '任务提交失败');
      }
    } catch (error: any) {
      message.error(error?.message || '任务提交失败');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="add-chart-mq-page">
      <Card title="智能分析（MQ）">
        <Form
          form={form}
          name="addChartMQ"
          labelAlign="left"
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 16 }}
          onFinish={onFinish}
        >
          <Form.Item
            name="goal"
            label="分析目标"
            rules={[{ required: true, message: '请输入分析目标' }]}
          >
            <TextArea
              placeholder="请输入你的分析需求，比如：分析网站用户增长趋势"
              fieldNames={{}}
            />
          </Form.Item>

          <Form.Item name="name" label="图表名称">
            <Input placeholder="请输入图表名称" />
          </Form.Item>

          <Form.Item name="chartType" label="图表类型">
            <Select placeholder="请选择图表类型" options={chartTypeOptions} />
          </Form.Item>

          <Form.Item
            name="file"
            label="原始数据"
            rules={[{ required: true, message: '请上传 CSV 文件' }]}
          >
            <Upload name="file" maxCount={1} accept=".csv,.xlsx,.xls">
              <Button icon={<UploadOutlined />}>上传数据文件</Button>
            </Upload>
          </Form.Item>

          <Form.Item wrapperCol={{ span: 16, offset: 4 }}>
            <Space>
              <Button type="primary" htmlType="submit" loading={submitting} disabled={submitting}>
                提交任务
              </Button>
              <Button htmlType="reset">重置</Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default AddChartMQPage;
