/**
 * XiangBI File: src/pages/Welcome.tsx
 * Responsibility: Page-level UI module.
 */
import { listAnnouncementVOByPageUsingPOST } from '@/services/yubi/announcementController';
import { PageContainer } from '@ant-design/pro-components';
import { Link } from '@umijs/max';
import { Alert, Button, Card, Col, Empty, List, Row, Skeleton, Space, Tag, Typography, message } from 'antd';
import moment from 'moment';
import { useEffect, useState } from 'react';

const { Paragraph, Text, Title } = Typography;

const benefitList = [
  {
    title: '降低分析门槛',
    description: '不需要复杂建模和手工写图表配置，输入目标后即可得到结构化图表与分析结论。',
  },
  {
    title: '提升响应效率',
    description: '平台将数据整理、图表生成和结论总结串成一条链路，减少重复性分析工作。',
  },
  {
    title: '结果更易落地',
    description: '输出同时包含可视化结果和文字结论，方便在汇报、复盘和业务沟通中直接使用。',
  },
  {
    title: '支持持续追踪',
    description: '分析结果会沉淀在“我的图表”中，便于后续回看、比较和重新生成。',
  },
];

const formatDateTime = (value?: string) => {
  if (!value) {
    return '-';
  }
  const date = moment(value);
  return date.isValid() ? date.format('YYYY-MM-DD HH:mm:ss') : value;
};

const Welcome: React.FC = () => {
  const [announcementList, setAnnouncementList] = useState<API.Announcement[]>([]);
  const [loading, setLoading] = useState(false);

  const loadAnnouncements = async () => {
    setLoading(true);
    try {
      const res = await listAnnouncementVOByPageUsingPOST({
        current: 1,
        pageSize: 6,
        status: 1,
        sortField: 'publishTime',
        sortOrder: 'desc',
      });
      if (res.code === 0) {
        setAnnouncementList(res.data?.records || []);
      } else {
        message.error(res.message || '公告加载失败');
      }
    } catch (error: any) {
      message.error(error?.message || '公告加载失败');
    }
    setLoading(false);
  };

  useEffect(() => {
    loadAnnouncements();
  }, []);

  return (
    <PageContainer>
      <Space direction="vertical" size={16} style={{ width: '100%' }}>
        <Card
          style={{
            borderRadius: 20,
            background:
              'linear-gradient(135deg, rgba(8,82,66,0.08) 0%, rgba(233,247,242,1) 45%, rgba(248,250,252,1) 100%)',
            border: '1px solid #e8f3ef',
          }}
          bodyStyle={{ padding: 28 }}
        >
          <Row gutter={[24, 24]} align="middle">
            <Col xs={24} lg={15}>
              <Space direction="vertical" size={14} style={{ width: '100%' }}>
                <Tag color="green" style={{ width: 'fit-content', paddingInline: 10, borderRadius: 999 }}>
                  BI · Business Intelligence
                </Tag>
                <Title level={2} style={{ margin: 0 }}>
                  用更轻量的方式完成数据分析
                </Title>
                <Paragraph style={{ fontSize: 15, lineHeight: 1.9, marginBottom: 0, maxWidth: 760 }}>
                  BI 是通过收集、整理、分析和展示数据，辅助业务做出更有效决策的一种方法。
                  本平台将 AI 与图表分析结合起来，把“提出问题、生成图表、输出结论”压缩成一次可操作的流程，
                  让分析从复杂操作变成可直接使用的能力。
                </Paragraph>
                <Space wrap>
                  <Link to="/add_chart">
                    <Button type="primary" size="large">
                      立即体验智能分析
                    </Button>
                  </Link>
                  <Link to="/my_chart">
                    <Button size="large">查看我的图表</Button>
                  </Link>
                </Space>
              </Space>
            </Col>
            <Col xs={24} lg={9}>
              <Card
                bordered={false}
                style={{
                  borderRadius: 18,
                  background: '#ffffff',
                  boxShadow: '0 18px 40px rgba(8, 82, 66, 0.08)',
                }}
              >
                <Space direction="vertical" size={12} style={{ width: '100%' }}>
                  <Text strong style={{ fontSize: 16 }}>
                    适合的使用场景
                  </Text>
                  <Alert message="经营复盘" description="快速查看趋势、结构与异常波动。" type="success" showIcon />
                  <Alert message="教学展示" description="用图表和结论降低汇报表达成本。" type="info" showIcon />
                  <Alert message="数据探索" description="先形成初步分析，再继续深入拆解。" type="warning" showIcon />
                </Space>
              </Card>
            </Col>
          </Row>
        </Card>

        <Row gutter={[16, 16]}>
          <Col xs={24} xl={14}>
            <Card title="本平台的好处" style={{ borderRadius: 18 }}>
              <Row gutter={[12, 12]}>
                {benefitList.map((item) => (
                  <Col xs={24} md={12} key={item.title}>
                    <Card
                      size="small"
                      style={{
                        borderRadius: 14,
                        background: '#fafafa',
                        border: '1px solid #f0f0f0',
                        minHeight: 160,
                      }}
                    >
                      <Space direction="vertical" size={8}>
                        <Text strong style={{ fontSize: 15 }}>
                          {item.title}
                        </Text>
                        <Paragraph style={{ marginBottom: 0, color: '#666', lineHeight: 1.8 }}>
                          {item.description}
                        </Paragraph>
                      </Space>
                    </Card>
                  </Col>
                ))}
              </Row>
            </Card>
          </Col>

          <Col xs={24} xl={10}>
            <Card title="最新公告" extra={<Button type="link" onClick={loadAnnouncements}>刷新</Button>} style={{ borderRadius: 18 }}>
              {loading ? (
                <Space direction="vertical" size={16} style={{ width: '100%' }}>
                  {[1, 2, 3].map((item) => (
                    <Skeleton key={item} active paragraph={{ rows: 2 }} />
                  ))}
                </Space>
              ) : announcementList.length ? (
                <List
                  itemLayout="vertical"
                  dataSource={announcementList}
                  renderItem={(item) => (
                    <List.Item key={item.id} style={{ paddingInline: 0 }}>
                      <Space direction="vertical" size={8} style={{ width: '100%' }}>
                        <Space wrap>
                          <Text strong style={{ fontSize: 15 }}>
                            {item.title}
                          </Text>
                          <Tag color={item.status === 1 ? 'green' : 'default'}>
                            {item.status === 1 ? '已发布' : '草稿'}
                          </Tag>
                        </Space>
                        <Paragraph
                          ellipsis={{ rows: 3 }}
                          style={{ marginBottom: 0, color: '#666', lineHeight: 1.8 }}
                        >
                          {item.content}
                        </Paragraph>
                        <Space size={16}>
                          <Text type="secondary">发布人：{item.userName || '-'}</Text>
                          <Text type="secondary">发布时间：{formatDateTime(item.publishTime)}</Text>
                        </Space>
                      </Space>
                    </List.Item>
                  )}
                />
              ) : (
                <Empty description="暂无公告" />
              )}
            </Card>
          </Col>
        </Row>
      </Space>
    </PageContainer>
  );
};

export default Welcome;

