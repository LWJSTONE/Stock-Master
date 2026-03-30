import store from '@/store'

/**
 * v-permission 权限指令
 * 使用方式：
 * v-permission="'system:user:add'" - 需要具有 system:user:add 权限（字符串格式）
 * v-permission="['system:user:add']" - 需要具有 system:user:add 权限（数组格式）
 * v-permission="['system:user:add', 'system:user:edit']" - 需要具有其中一个权限
 */
export default {
  inserted(el, binding, vnode) {
    const { value } = binding
    const permissions = store.getters && store.getters.permissions
    const roles = store.getters && store.getters.roles

    // admin 角色拥有所有权限，直接显示
    if (roles && roles.includes('admin')) {
      return
    }

    // 支持字符串和数组两种格式
    let permissionRoles = []
    if (typeof value === 'string' && value.trim()) {
      // 字符串格式，转为数组
      permissionRoles = [value.trim()]
    } else if (value && value instanceof Array && value.length > 0) {
      // 数组格式
      permissionRoles = value
    } else {
      // 无效的权限值，静默移除元素（不抛出错误）
      el.parentNode && el.parentNode.removeChild(el)
      return
    }

    // 如果用户没有权限数据，静默移除元素
    if (!permissions || permissions.length === 0) {
      el.parentNode && el.parentNode.removeChild(el)
      return
    }

    // 检查用户是否具有指定权限
    const hasPermission = permissions.some(permission => {
      return permissionRoles.includes(permission)
    })

    if (!hasPermission) {
      // 无权限，移除元素
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}

/**
 * v-role 角色指令
 * 使用方式：
 * v-role="'admin'" - 需要 admin 角色（字符串格式）
 * v-role="['admin']" - 需要 admin 角色（数组格式）
 * v-role="['admin', 'editor']" - 需要其中一个角色
 */
export const role = {
  inserted(el, binding, vnode) {
    const { value } = binding
    const roles = store.getters && store.getters.roles

    // 支持字符串和数组两种格式
    let roleList = []
    if (typeof value === 'string' && value.trim()) {
      // 字符串格式，转为数组
      roleList = [value.trim()]
    } else if (value && value instanceof Array && value.length > 0) {
      // 数组格式
      roleList = value
    } else {
      // 无效的角色值，静默移除元素（不抛出错误）
      el.parentNode && el.parentNode.removeChild(el)
      return
    }

    // 如果用户没有角色数据，静默移除元素
    if (!roles || roles.length === 0) {
      el.parentNode && el.parentNode.removeChild(el)
      return
    }

    // 检查用户是否具有指定角色
    const hasRole = roles.some(role => {
      return roleList.includes(role)
    })

    if (!hasRole) {
      // 无角色，移除元素
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}
