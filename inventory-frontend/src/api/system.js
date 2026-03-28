import request from '@/utils/request'

// ==================== 用户管理 ====================

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/api/system/user/list',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/api/system/user/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/api/system/user',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: '/api/system/user',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/system/user/${id}`,
    method: 'delete'
  })
}

// 批量删除用户
export function batchDeleteUsers(ids) {
  return request({
    url: '/api/system/user/batch',
    method: 'delete',
    data: ids
  })
}

// 重置密码
export function resetPassword(id) {
  return request({
    url: `/api/system/user/resetPwd`,
    method: 'put',
    data: { userId: id }
  })
}

// 修改用户状态
export function changeUserStatus(id, status) {
  return request({
    url: `/api/system/user/changeStatus`,
    method: 'put',
    data: { userId: id, status }
  })
}

// 导出用户
export function exportUser(params) {
  return request({
    url: '/api/system/user/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// ==================== 角色管理 ====================

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/api/system/role/list',
    method: 'get',
    params
  })
}

// 获取所有角色（下拉选择用）
export function getAllRoles() {
  return request({
    url: '/api/system/role/all',
    method: 'get'
  })
}

// 获取角色详情
export function getRoleDetail(id) {
  return request({
    url: `/api/system/role/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/api/system/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(data) {
  return request({
    url: '/api/system/role',
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/api/system/role/${id}`,
    method: 'delete'
  })
}

// 修改角色状态
export function changeRoleStatus(id, status) {
  return request({
    url: `/api/system/role/status/${id}`,
    method: 'put',
    params: { status }
  })
}

// 获取角色菜单权限
export function getRoleMenus(roleId) {
  return request({
    url: `/api/system/role/menu/${roleId}`,
    method: 'get'
  })
}

// 分配角色菜单权限
export function assignRoleMenus(roleId, menuIds) {
  return request({
    url: `/api/system/role/allotMenu`,
    method: 'put',
    data: { roleId, menuIds }
  })
}

// 导出角色
export function exportRole(params) {
  return request({
    url: '/api/system/role/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// ==================== 菜单管理 ====================

// 获取菜单列表（树形）
export function getMenuList(params) {
  return request({
    url: '/api/system/menu/list',
    method: 'get',
    params
  })
}

// 获取菜单树（用于下拉选择）
export function getMenuTree() {
  return request({
    url: '/api/system/menu/tree',
    method: 'get'
  })
}

// 获取菜单详情
export function getMenuDetail(id) {
  return request({
    url: `/api/system/menu/${id}`,
    method: 'get'
  })
}

// 创建菜单
export function createMenu(data) {
  return request({
    url: '/api/system/menu',
    method: 'post',
    data
  })
}

// 更新菜单
export function updateMenu(data) {
  return request({
    url: '/api/system/menu',
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `/api/system/menu/${id}`,
    method: 'delete'
  })
}

// 获取图标列表
export function getIconList() {
  return request({
    url: '/api/system/menu/icons',
    method: 'get'
  })
}

// ==================== 部门管理 ====================

// 获取部门列表（树形）
export function getDeptList(params) {
  return request({
    url: '/api/system/dept/list',
    method: 'get',
    params
  })
}

// 获取部门树（用于下拉选择）
export function getDeptTree() {
  return request({
    url: '/api/system/dept/tree',
    method: 'get'
  })
}

// 获取部门详情
export function getDeptDetail(id) {
  return request({
    url: `/api/system/dept/${id}`,
    method: 'get'
  })
}

// 创建部门
export function createDept(data) {
  return request({
    url: '/api/system/dept',
    method: 'post',
    data
  })
}

// 更新部门
export function updateDept(data) {
  return request({
    url: '/api/system/dept',
    method: 'put',
    data
  })
}

// 删除部门
export function deleteDept(id) {
  return request({
    url: `/api/system/dept/${id}`,
    method: 'delete'
  })
}

// 修改部门状态
export function changeDeptStatus(id, status) {
  return request({
    url: `/api/system/dept/status/${id}`,
    method: 'put',
    params: { status }
  })
}
