import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getInfo() {
  return request({
    url: '/api/auth/getInfo',
    method: 'get'
  })
}

// 获取用户路由
export function getRouters() {
  return request({
    url: '/api/auth/getRouters',
    method: 'get'
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}
