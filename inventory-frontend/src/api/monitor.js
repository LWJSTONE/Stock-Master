import request from '@/utils/request'

// ==================== 操作日志 ====================

// 获取操作日志列表
export function getLogList(params) {
  return request({
    url: '/api/monitor/operlog/list',
    method: 'get',
    params
  })
}

// 删除日志
export function deleteLog(ids) {
  return request({
    url: `/api/monitor/operlog/${ids}`,
    method: 'delete'
  })
}

// 清空日志
export function clearLog() {
  return request({
    url: '/api/monitor/operlog/clean',
    method: 'delete'
  })
}

// 导出日志
export function exportLog(params) {
  return request({
    url: '/api/monitor/operlog/export',
    method: 'post',
    params,
    responseType: 'blob'
  })
}
