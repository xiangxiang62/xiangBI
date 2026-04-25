/**
 * XiangBI File: src/pages/CampusInsight/index.tsx
 * Responsibility: Page-level UI module.
 */
import { genChatUsingPOST } from '@/services/yubi/aiController';
import { ReloadOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { useLocation } from '@umijs/max';
import {
  Button,
  Card,
  Col,
  Empty,
  Form,
  Input,
  Result,
  Row,
  Space,
  Spin,
  Tag,
  Typography,
  message,
} from 'antd';
import ReactECharts from 'echarts-for-react';
import { useEffect, useMemo, useState } from 'react';

const { Paragraph, Text } = Typography;

type InsightConfig = {
  title: string;
  description: string;
  highlight: string[];
  promptBuilder: (major: string) => string;
};

type AnalysisJson = {
  data_overview?: string;
  main_trends?: string;
  abnormal_points?: string;
  recommendations?: string;
  [key: string]: any;
};

const insightConfigMap: Record<string, InsightConfig> = {
  '/campus/employment': {
    title: '大学生就业情况',
    description: '聚焦毕业去向、行业分布、地区流向和薪资区间，生成该专业学生的就业趋势分析。',
    highlight: ['就业率', '行业分布', '城市流向'],
    promptBuilder: (major) =>
      `请生成一份关于大学生就业情况的模拟分析数据，专业限定为“${major}”，面向中国大学生群体，要求数据合理、结构清晰，适合做可视化展示。请严格按照以下格式返回数据（分隔符=>=>=>必须单独占一行）：\n=>=>=>\n{ECharts图表配置JSON}\n=>=>=>\n{数据分析结论文本}\n=>=>=>\n要求：1. 图表配置必须是合法的ECharts JSON格式；2. 图表主题要围绕该专业的就业率、就业行业分布、就业城市层级或薪资区间中的至少两个维度；3. 分析结论要结合该专业特点，包含数据概览、主要趋势、异常点或值得关注的现象、建议；4. 不要输出任何额外说明，只返回上述格式内容。`,
  },
  '/campus/dining': {
    title: '大学生吃饭情况',
    description: '围绕食堂、外卖、消费频次与月均餐饮开销，生成该专业学生的饮食行为分析。',
    highlight: ['食堂占比', '外卖频次', '月均餐饮支出'],
    promptBuilder: (major) =>
      `请生成一份关于大学生吃饭情况的模拟分析数据，专业限定为“${major}”，面向中国大学生群体，要求数据合理、结构清晰，适合做可视化展示。请严格按照以下格式返回数据（分隔符=>=>=>必须单独占一行）：\n=>=>=>\n{ECharts图表配置JSON}\n=>=>=>\n{数据分析结论文本}\n=>=>=>\n要求：1. 图表配置必须是合法的ECharts JSON格式；2. 图表主题要围绕该专业学生的食堂就餐、外卖、校外聚餐、月均餐饮花费或一周就餐频次中的至少两个维度；3. 分析结论要结合该专业学习节奏或生活习惯，包含数据概览、主要趋势、异常点或值得关注的现象、建议；4. 不要输出任何额外说明，只返回上述格式内容。`,
  },
  '/campus/love': {
    title: '大学生恋爱情况',
    description: '从恋爱状态、恋爱时长、消费投入与对学习生活影响等维度生成该专业学生的恋爱分析。',
    highlight: ['恋爱状态', '恋爱时长', '生活影响'],
    promptBuilder: (major) =>
      `请生成一份关于大学生恋爱情况的模拟分析数据，专业限定为“${major}”，面向中国大学生群体，要求数据合理、结构清晰，适合做可视化展示。请严格按照以下格式返回数据（分隔符=>=>=>必须单独占一行）：\n=>=>=>\n{ECharts图表配置JSON}\n=>=>=>\n{数据分析结论文本}\n=>=>=>\n要求：1. 图表配置必须是合法的ECharts JSON格式；2. 图表主题要围绕该专业学生的恋爱状态、恋爱时长、恋爱消费、对学习或情绪影响中的至少两个维度；3. 分析结论要结合该专业特点，包含数据概览、主要趋势、异常点或值得关注的现象、建议；4. 不要输出任何额外说明，只返回上述格式内容。`,
  },
};

const defaultMajorMap: Record<string, string> = {
  '/campus/employment': '计算机科学与技术',
  '/campus/dining': '软件工程',
  '/campus/love': '汉语言文学',
};

const analysisFieldMap: Record<string, { title: string; color: string }> = {
  data_overview: { title: '数据概览', color: '#e6f4ff' },
  main_trends: { title: '主要趋势', color: '#f6ffed' },
  abnormal_points: { title: '异常点', color: '#fff7e6' },
  recommendations: { title: '建议', color: '#f9f0ff' },
};

const parseAiResult = (content?: string) => {
  if (!content) {
    throw new Error('AI 未返回内容');
  }
  const parts = content.split('=>=>=>');
  if (parts.length < 3) {
    throw new Error('AI 返回格式不正确');
  }
  let chartText = parts[1]?.trim() || '';
  const genResult = parts[2]?.trim() || '';
  if (!chartText || !genResult) {
    throw new Error('AI 返回内容不完整');
  }
  if (!chartText.startsWith('{')) {
    const start = chartText.indexOf('{');
    const end = chartText.lastIndexOf('}');
    if (start !== -1 && end !== -1 && end > start) {
      chartText = chartText.slice(start, end + 1);
    }
  }
  return {
    option: JSON.parse(chartText),
    genResult,
  };
};

const normalizeAnalysisText = (value?: string) => {
  if (!value) {
    return '';
  }
  return value
    .replace(/^```json\s*/i, '')
    .replace(/^```\s*/i, '')
    .replace(/\s*```$/i, '')
    .trim();
};

const tryParseAnalysisJson = (value?: string): AnalysisJson | null => {
  const normalized = normalizeAnalysisText(value);
  if (!normalized) {
    return null;
  }
  try {
    const parsed = JSON.parse(normalized);
    return parsed && typeof parsed === 'object' ? parsed : null;
  } catch (error) {
    return null;
  }
};

const renderAnalysisResult = (value?: string) => {
  const parsed = tryParseAnalysisJson(value);
  if (!parsed) {
    return (
      <div
        style={{
          whiteSpace: 'pre-wrap',
          lineHeight: 1.9,
          color: '#222',
          fontSize: 15,
        }}
      >
        {normalizeAnalysisText(value)}
      </div>
    );
  }

  const orderedKeys = ['data_overview', 'main_trends', 'abnormal_points', 'recommendations'];
  const extraKeys = Object.keys(parsed).filter((key) => !orderedKeys.includes(key));
  const finalKeys = [...orderedKeys, ...extraKeys].filter((key) => parsed[key]);

  return (
    <Space direction="vertical" size={12} style={{ width: '100%' }}>
      {finalKeys.map((key) => {
        const meta = analysisFieldMap[key] || { title: key, color: '#fafafa' };
        return (
          <Card
            key={key}
            size="small"
            style={{
              borderRadius: 14,
              background: meta.color,
              border: '1px solid #f0f0f0',
              boxShadow: '0 6px 18px rgba(0, 0, 0, 0.04)',
            }}
            bodyStyle={{ padding: 16 }}
          >
            <Space direction="vertical" size={8} style={{ width: '100%' }}>
              <Text strong style={{ fontSize: 15 }}>
                {meta.title}
              </Text>
              <div
                style={{
                  whiteSpace: 'pre-wrap',
                  lineHeight: 1.85,
                  color: '#333',
                  fontSize: 14,
                }}
              >
                {typeof parsed[key] === 'string' ? parsed[key] : JSON.stringify(parsed[key], null, 2)}
              </div>
            </Space>
          </Card>
        );
      })}
    </Space>
  );
};

const CampusInsightPage: React.FC = () => {
  const location = useLocation();
  const config = useMemo(
    () => insightConfigMap[location.pathname] || insightConfigMap['/campus/employment'],
    [location.pathname],
  );
  const defaultMajor = defaultMajorMap[location.pathname] || '计算机科学与技术';
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [option, setOption] = useState<Record<string, any>>();
  const [genResult, setGenResult] = useState<string>();
  const [error, setError] = useState<string>();
  const [major, setMajor] = useState(defaultMajor);

  const loadInsight = async (targetMajor?: string) => {
    const finalMajor = (targetMajor || form.getFieldValue('major') || defaultMajor).trim();
    if (!finalMajor) {
      message.warning('请输入专业');
      return;
    }
    setMajor(finalMajor);
    setLoading(true);
    setError(undefined);
    try {
      const res = await genChatUsingPOST({
        message: config.promptBuilder(finalMajor),
      });
      if (res.code !== 0 || !res.data) {
        throw new Error(res.message || 'AI 调用失败');
      }
      const parsed = parseAiResult(res.data);
      setOption(parsed.option);
      setGenResult(parsed.genResult);
    } catch (e: any) {
      setOption(undefined);
      setGenResult(undefined);
      setError(e?.message || '加载失败');
      message.error(e?.message || '加载失败');
    }
    setLoading(false);
  };

  useEffect(() => {
    form.setFieldsValue({ major: defaultMajor });
    setMajor(defaultMajor);
    loadInsight(defaultMajor);
  }, [location.pathname]);

  return (
    <PageContainer
      title={config.title}
      extra={[
        <Button key="reload" icon={<ReloadOutlined />} onClick={() => loadInsight()} loading={loading}>
          重新生成
        </Button>,
      ]}
    >
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Card
            style={{
              borderRadius: 16,
              background:
                'linear-gradient(135deg, rgba(8,110,74,0.08) 0%, rgba(223,247,238,0.9) 100%)',
            }}
          >
            <Space direction="vertical" size={12} style={{ width: '100%' }}>
              <Text strong style={{ fontSize: 24 }}>
                {config.title}
              </Text>
              <Paragraph style={{ marginBottom: 0, maxWidth: 760 }}>{config.description}</Paragraph>
              <Space wrap>
                <Tag color="blue">当前专业：{major}</Tag>
                {config.highlight.map((item) => (
                  <Tag key={item} color="green">
                    {item}
                  </Tag>
                ))}
              </Space>
              <Form
                form={form}
                layout="inline"
                initialValues={{ major: defaultMajor }}
                onFinish={(values) => loadInsight(values.major)}
              >
                <Form.Item
                  name="major"
                  label="专业"
                  rules={[{ required: true, message: '请输入专业' }]}
                >
                  <Input placeholder="例如：计算机科学与技术" style={{ width: 260 }} />
                </Form.Item>
                <Form.Item>
                  <Button type="primary" htmlType="submit" loading={loading}>
                    生成图表与结论
                  </Button>
                </Form.Item>
              </Form>
            </Space>
          </Card>
        </Col>
        <Col xs={24} lg={15}>
          <Card title="图表展示" style={{ borderRadius: 16, minHeight: 520 }}>
            <Spin spinning={loading}>
              {error ? (
                <Result status="error" title="生成失败" subTitle={error} />
              ) : option ? (
                <ReactECharts option={option} style={{ height: 420 }} />
              ) : (
                <Empty description="暂无图表数据" />
              )}
            </Spin>
          </Card>
        </Col>
        <Col xs={24} lg={9}>
          <Card title="AI 分析结论" style={{ borderRadius: 16, minHeight: 520 }}>
            <Spin spinning={loading}>
              {error ? (
                <Result status="warning" title="暂未生成结论" subTitle={error} />
              ) : genResult ? (
                renderAnalysisResult(genResult)
              ) : (
                <Empty description="暂无分析结论" />
              )}
            </Spin>
          </Card>
        </Col>
      </Row>
    </PageContainer>
  );
};

export default CampusInsightPage;

