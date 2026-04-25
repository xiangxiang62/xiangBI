/**
 * XiangBI File: src/access.ts
 * Responsibility: Frontend shared constants or access module.
 */
/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.LoginUserVO } | undefined) {
  const { currentUser } = initialState ?? {};
  return {
    canAdmin: currentUser?.userRole === 'admin',
  };
}

