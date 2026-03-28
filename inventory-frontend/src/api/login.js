import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录信息
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 */
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 获取用户信息
 * @returns {Promise} 用户信息
 */
export function getInfo() {
  return request({
    url: '/api/auth/getInfo',
    method: 'get'
  })
}

/**
 * 获取用户路由
 * @returns {Promise} 路由列表
 */
export function getRouters() {
  return request({
    url: '/api/auth/getRouters',
    method: 'get'
  })
}
