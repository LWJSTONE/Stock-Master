import store from '@/store'

/**
 * v-permission 权限指令
 * 使用方式：
 * v-permission="['system:user:add']" - 需要具有 system:user:add 权限
 * v-permission="['system:user:add', 'system:user:edit']" - 需要具有其中一个权限
 */
export default {
  inserted(el, binding, vnode) {
    const { value } = binding
    const permissions = store.getters && store.getters.permissions

    if (value && value instanceof Array && value.length > 0) {
      const permissionRoles = value

      // 检查用户是否具有指定权限
      const hasPermission = permissions.some(permission => {
        return permissionRoles.includes(permission)
      })

      if (!hasPermission) {
        // 无权限，移除元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`需要权限！如：v-permission="['system:user:add']"`)
    }
  }
}

/**
 * v-role 角色指令
 * 使用方式：
 * v-role="['admin']" - 需要 admin 角色
 * v-role="['admin', 'editor']" - 需要其中一个角色
 */
export const role = {
  inserted(el, binding, vnode) {
    const { value } = binding
    const roles = store.getters && store.getters.roles

    if (value && value instanceof Array && value.length > 0) {
      const roleList = value

      // 检查用户是否具有指定角色
      const hasRole = roles.some(role => {
        return roleList.includes(role)
      })

      if (!hasRole) {
        // 无角色，移除元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`需要角色！如：v-role="['admin']"`)
    }
  }
}
