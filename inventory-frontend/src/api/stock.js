import request from '@/utils/request'

// ==================== 实时库存API ====================

// 获取实时库存列表
export function getInventoryList(params) {
  return request({
    url: '/api/stock/main/list',
    method: 'get',
    params
  })
}

// 获取库存详情
export function getInventoryDetail(id) {
  return request({
    url: `/api/stock/main/${id}`,
    method: 'get'
  })
}

// 获取库存流水
export function getInventoryLog(params) {
  return request({
    url: '/api/stock/record/list',
    method: 'get',
    params
  })
}

// 获取库存预警列表
export function getWarningList(params) {
  return request({
    url: '/api/stock/warning',
    method: 'get',
    params
  })
}

// ==================== 库存盘点API ====================

// 获取盘点单列表
export function getCheckList(params) {
  return request({
    url: '/api/stock/check/list',
    method: 'get',
    params
  })
}

// 获取盘点单详情
export function getCheckDetail(id) {
  return request({
    url: `/api/stock/check/${id}`,
    method: 'get'
  })
}

// 创建盘点单
export function createCheck(data) {
  return request({
    url: '/api/stock/check',
    method: 'post',
    data
  })
}

// 更新盘点单
export function updateCheck(data) {
  return request({
    url: '/api/stock/check',
    method: 'put',
    data
  })
}

// 删除盘点单
export function deleteCheck(id) {
  return request({
    url: `/api/stock/check/${id}`,
    method: 'delete'
  })
}

// 确认盘点单
export function confirmCheck(checkId) {
  return request({
    url: '/api/stock/check/confirm',
    method: 'put',
    params: { checkId }
  })
}

// 更新盘点明细（录入实盘数量）
export function updateCheckItem(data) {
  return request({
    url: '/api/stock/check/item',
    method: 'put',
    data
  })
}
