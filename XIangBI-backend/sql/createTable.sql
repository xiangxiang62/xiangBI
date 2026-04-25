-- 建库脚本
create database if not exists bi default character set utf8mb4 collate utf8mb4_unicode_ci;

use bi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment primary key comment 'id',
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    phoneNum     varchar(32)                            null comment '手机号',
    email        varchar(32)                            null comment '邮箱',
    leftCount    int                                    null comment '积分',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户表' collate = utf8mb4_unicode_ci;

-- 图表表
create table if not exists chart
(
    id           bigint auto_increment primary key comment 'id',
    goal         text                                   null comment '分析目标',
    `name`       varchar(128)                           null comment '图表名称',
    chartData    text                                   null comment '图表数据',
    chartType    varchar(128)                           null comment '图表类型',
    genChart     text                                   null comment '生成的图表数据',
    genResult    text                                   null comment '生成的分析结论',
    status       varchar(128) default 'wait'           not null comment 'wait/running/succeed/failed',
    execMessage  text                                   null comment '执行信息',
    userId       bigint                                 null comment '创建用户 id',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '图表表' collate = utf8mb4_unicode_ci;

-- 公告表
create table if not exists announcement
(
    id           bigint auto_increment primary key comment 'id',
    title        varchar(80)                            not null comment '公告标题',
    content      text                                   not null comment '公告内容',
    status       tinyint      default 0                 not null comment '状态：0-草稿，1-已发布',
    priority     int          default 0                 not null comment '优先级，值越大越靠前',
    publishTime  datetime                               null comment '发布时间',
    userId       bigint                                 not null comment '创建人 id',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_announcement_title (title),
    index idx_announcement_status_publishTime (status, publishTime),
    index idx_announcement_userId (userId)
) comment '公告表' collate = utf8mb4_unicode_ci;
