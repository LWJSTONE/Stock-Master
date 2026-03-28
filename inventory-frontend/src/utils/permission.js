import store from '@/store'

/**
 * 检查是否具有权限
 * @param {Array} value - 权限标识数组
 * @returns {Boolean}
 */
export function checkPermi(value) {
  if (value && value instanceof Array && value.length > 0) {
    const permissions = store.getters && store.getters.permissions
    const permissionRoles = value

    return permissions.some(permission => {
      return permissionRoles.includes(permission)
    })
  } else {
    console.error(`need permissions! Like checkPermi(['system:user:add'])`)
    return false
  }
}

/**
 * 检查是否具有角色
 * @param {Array} value - 角色标识数组
 * @returns {Boolean}
 */
export function checkRole(value) {
  if (value && value instanceof Array && value.length > 0) {
    const roles = store.getters && store.getters.roles
    const roleList = value

    return roles.some(role => {
      return roleList.includes(role)
    })
  } else {
    console.error(`need roles! Like checkRole(['admin'])`)
    return false
  }
}
