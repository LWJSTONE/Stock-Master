import router from './router'
import store from './store'
import { Message } from 'element-ui'
import { getToken } from '@/utils/auth'
import { isExternal } from '@/utils/validate'

// 白名单，不需要登录即可访问
const whiteList = ['/login', '/404']

router.beforeEach(async(to, from, next) => {
  // 启动进度条
  // NProgress.start()

  // 获取Token
  const hasToken = getToken()

  if (hasToken) {
    // 已登录状态
    if (to.path === '/login') {
      // 如果已登录，跳转到首页
      next({ path: '/' })
      // NProgress.done()
    } else {
      // 检查是否已获取用户信息
      const hasRoles = store.getters.roles && store.getters.roles.length > 0
      if (hasRoles) {
        // 已有用户信息，直接放行
        next()
      } else {
        try {
          // 获取用户信息
          const { roles } = await store.dispatch('user/getInfo')

          // 根据角色生成动态路由
          const accessRoutes = await store.dispatch('permission/generateRoutes', roles)

          // 动态添加路由（使用addRoute替代已弃用的addRoutes）
          accessRoutes.forEach(route => {
            router.addRoute(route)
          })

          // hack方法 确保addRoutes已完成
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，移除token并跳转登录页
          await store.dispatch('user/resetToken')
          // 不显示错误消息，静默重定向到登录页
          // Message.error(error || 'Has Error')
          next(`/login?redirect=${to.path}`)
          // NProgress.done()
        }
      }
    }
  } else {
    // 未登录状态
    if (whiteList.indexOf(to.path) !== -1) {
      // 在白名单中，直接放行
      next()
    } else {
      // 不在白名单中，跳转登录页
      next(`/login?redirect=${to.path}`)
      // NProgress.done()
    }
  }
})

router.afterEach(() => {
  // 关闭进度条
  // NProgress.done()
})
