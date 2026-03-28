import store from '@/store'

/**
 * 检查是否具有权限
 * @param {Array|String} value - 权限标识数组或字符串
 * @returns {Boolean}
 */
export function checkPermi(value) {
  const permissions = store.getters && store.getters.permissions

  // 如果用户没有权限数据，返回 false
  if (!permissions || permissions.length === 0) {
    return false
  }

  // 支持字符串和数组两种格式
  let permissionRoles = []
  if (typeof value === 'string' && value.trim()) {
    permissionRoles = [value.trim()]
  } else if (value && value instanceof Array && value.length > 0) {
    permissionRoles = value
  } else {
    console.error(`need permissions! Like checkPermi(['system:user:add']) or checkPermi('system:user:add')`)
    return false
  }

  return permissions.some(permission => {
    return permissionRoles.includes(permission)
  })
}

/**
 * 检查是否具有角色
 * @param {Array|String} value - 角色标识数组或字符串
 * @returns {Boolean}
 */
export function checkRole(value) {
  const roles = store.getters && store.getters.roles

  // 如果用户没有角色数据，返回 false
  if (!roles || roles.length === 0) {
    return false
  }

  // 支持字符串和数组两种格式
  let roleList = []
  if (typeof value === 'string' && value.trim()) {
    roleList = [value.trim()]
  } else if (value && value instanceof Array && value.length > 0) {
    roleList = value
  } else {
    console.error(`need roles! Like checkRole(['admin']) or checkRole('admin')`)
    return false
  }

  return roles.some(role => {
    return roleList.includes(role)
  })
}
