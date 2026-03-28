import request from '@/utils/request'

// ==================== 操作日志 ====================

// 获取操作日志列表
export function getLogList(params) {
  return request({
    url: '/api/monitor/log/list',
    method: 'get',
    params
  })
}

// 获取日志详情
export function getLogDetail(id) {
  return request({
    url: `/api/monitor/log/${id}`,
    method: 'get'
  })
}

// 删除日志
export function deleteLog(id) {
  return request({
    url: `/api/monitor/log/${id}`,
    method: 'delete'
  })
}

// 清空日志
export function clearLog() {
  return request({
    url: '/api/monitor/log/clear',
    method: 'delete'
  })
}

// 导出日志
export function exportLog(params) {
  return request({
    url: '/api/monitor/log/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取操作类型列表
export function getOperateTypes() {
  return request({
    url: '/api/monitor/log/operate-types',
    method: 'get'
  })
}

// 获取模块列表
export function getModules() {
  return request({
    url: '/api/monitor/log/modules',
    method: 'get'
  })
}

// ==================== 在线用户 ====================

// 获取在线用户列表
export function getOnlineUsers(params) {
  return request({
    url: '/api/monitor/online/list',
    method: 'get',
    params
  })
}

// 强制下线
export function forceLogout(token) {
  return request({
    url: `/api/monitor/online/${token}`,
    method: 'delete'
  })
}

// ==================== 登录日志 ====================

// 获取登录日志列表
export function getLoginLogList(params) {
  return request({
    url: '/api/monitor/login-log/list',
    method: 'get',
    params
  })
}

// 删除登录日志
export function deleteLoginLog(id) {
  return request({
    url: `/api/monitor/login-log/${id}`,
    method: 'delete'
  })
}

// 清空登录日志
export function clearLoginLog() {
  return request({
    url: '/api/monitor/login-log/clear',
    method: 'delete'
  })
}

// 导出登录日志
export function exportLoginLog(params) {
  return request({
    url: '/api/monitor/login-log/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
