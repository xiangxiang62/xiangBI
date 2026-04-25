/**
 * XiangBI File: src/pages/Admin/UserManage.tsx
 * Responsibility: Page-level UI module.
 */
import {
  deleteUserUsingPOST,
  listUserByPageUsingPOST,
  updateUserUsingPOST,
} from '@/services/yubi/userController';
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
import { Avatar, Button, message, Popconfirm, Tag } from 'antd';
import { useRef, useState } from 'react';

const userRoleOptions = [
  { label: '普通用户', value: 'user' },
  { label: '管理员', value: 'admin' },
];

const userRoleEnum = {
  user: { text: '普通用户' },
  admin: { text: '管理员' },
};

const UserManagePage = () => {
  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.User>();

  const columns: ProColumns<API.User>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
      search: false,
    },
    {
      title: '头像',
      dataIndex: 'userAvatar',
      width: 80,
      search: false,
      render: (_, record) => <Avatar src={record.userAvatar}>{record.userName?.[0]}</Avatar>,
    },
    {
      title: '账号',
      dataIndex: 'userAccount',
      ellipsis: true,
      search: false,
    },
    {
      title: '昵称',
      dataIndex: 'userName',
      ellipsis: true,
    },
    {
      title: '角色',
      dataIndex: 'userRole',
      valueType: 'select',
      valueEnum: userRoleEnum,
      fieldProps: {
        options: userRoleOptions,
      },
      render: (_, record) => {
        const isAdmin = record.userRole === 'admin';
        return <Tag color={isAdmin ? 'gold' : 'blue'}>{isAdmin ? '管理员' : '普通用户'}</Tag>;
      },
      width: 110,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      search: false,
      ellipsis: true,
    },
    {
      title: '手机号',
      dataIndex: 'phoneNum',
      search: false,
      width: 140,
    },
    {
      title: '积分',
      dataIndex: 'leftCount',
      search: false,
      width: 90,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      search: false,
      width: 180,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 140,
      render: (_, record) => [
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
          title="确认删除这个用户吗？"
          onConfirm={async () => {
            const hide = message.loading('正在删除');
            try {
              const res = await deleteUserUsingPOST({ id: record.id });
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
      <ProTable<API.User>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        search={{
          labelWidth: 88,
        }}
        request={async (params) => {
          const res = await listUserByPageUsingPOST({
            current: params.current,
            pageSize: params.pageSize,
            userName: params.userName,
            userRole: params.userRole,
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
      <ModalForm<API.UserUpdateRequest>
        title="编辑用户"
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
          const res = await updateUserUsingPOST({
            id: currentRow.id,
            userName: values.userName,
            userAvatar: values.userAvatar,
            userProfile: values.userProfile,
            userRole: values.userRole,
            phoneNum: values.phoneNum,
            email: values.email,
            leftCount: values.leftCount,
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
        <ProFormText name="userName" label="昵称" rules={[{ required: true, message: '请输入昵称' }]} />
        <ProFormText name="userAvatar" label="头像地址" />
        <ProFormTextArea name="userProfile" label="个人简介" />
        <ProFormSelect
          name="userRole"
          label="角色"
          options={userRoleOptions}
          rules={[{ required: true, message: '请选择角色' }]}
        />
        <ProFormText name="email" label="邮箱" />
        <ProFormText name="phoneNum" label="手机号" />
        <ProFormDigit name="leftCount" label="积分" fieldProps={{ precision: 0 }} />
      </ModalForm>
    </PageContainer>
  );
};

export default UserManagePage;

