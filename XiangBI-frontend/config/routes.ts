/**
 * XiangBI File: config/routes.ts
 * Responsibility: Project configuration module.
 */
export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { path: '/user/login', component: './User/Login' },
      { path: '/user/register', component: './User/Register' },
      { path: '/user/settings', component: './User/Settings' },
    ],
  },
  { path: '/', redirect: '/welcome' },
  { path: '/welcome', name: '首页', icon: 'homeOutlined', component: './Welcome' },
  { path: '/add_chart', name: '智能分析', icon: 'barChart', component: './AddChart' },
  {
    path: '/add_chart_async',
    name: '智能分析（异步）',
    icon: 'barChart',
    component: './AddChartAsync',
  },
  {
    path: '/add_chart_mq',
    name: '智能分析（MQ）',
    icon: 'deploymentUnit',
    component: './AddChartMQ',
  },
  { path: '/my_chart', name: '我的图表', icon: 'pieChart', component: './MyChart' },
  {
    path: '/campus/employment',
    name: '大学生就业情况',
    icon: 'areaChart',
    component: './CampusInsight',
  },
  {
    path: '/campus/dining',
    name: '大学生吃饭情况',
    icon: 'fundProjectionScreen',
    component: './CampusInsight',
  },
  {
    path: '/campus/love',
    name: '大学生恋爱情况',
    icon: 'heart',
    component: './CampusInsight',
  },
  {
    path: '/admin',
    name: '管理页面',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/charts' },
      { path: '/admin/charts', name: '图表管理', component: './Admin/ChartManage' },
      { path: '/admin/users', name: '用户管理', component: './Admin/UserManage' },
      { path: '/admin/announcements', name: '公告管理', component: './Admin/AnnouncementManage' },
    ],
  },
  { path: '*', layout: false, component: './404' },
];
