import request from '@/utils/request'

// ==================== 实时库存API ====================

// 获取实时库存列表
export function getInventoryList(params) {
  return request({
    url: '/api/stock/inventory/list',
    method: 'get',
    params
  })
}

// 获取库存详情
export function getInventoryDetail(id) {
  return request({
    url: `/api/stock/inventory/${id}`,
    method: 'get'
  })
}

// 库存调整
export function adjustInventory(data) {
  return request({
    url: '/api/stock/inventory/adjust',
    method: 'post',
    data
  })
}

// 获取库存流水
export function getInventoryLog(params) {
  return request({
    url: '/api/stock/inventory/log',
    method: 'get',
    params
  })
}

// 导出库存
export function exportInventory(params) {
  return request({
    url: '/api/stock/inventory/export',
    method: 'get',
    params,
    responseType: 'blob'
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
export function confirmCheck(id) {
  return request({
    url: `/api/stock/check/${id}/confirm`,
    method: 'post'
  })
}

// 获取盘点明细
export function getCheckItems(checkId) {
  return request({
    url: `/api/stock/check/${checkId}/items`,
    method: 'get'
  })
}

// 更新盘点明细（录入实盘数量）
export function updateCheckItem(checkId, data) {
  return request({
    url: `/api/stock/check/${checkId}/item`,
    method: 'put',
    data
  })
}

// ==================== 库存预警API ====================

// 获取库存预警列表
export function getWarningList(params) {
  return request({
    url: '/api/stock/warning/list',
    method: 'get',
    params
  })
}

// 设置库存预警阈值
export function setWarningThreshold(data) {
  return request({
    url: '/api/stock/warning/threshold',
    method: 'post',
    data
  })
}

// ==================== 库位管理API ====================

// 获取库位列表
export function getLocationList(warehouseId) {
  return request({
    url: '/api/stock/location/list',
    method: 'get',
    params: { warehouseId }
  })
}

// 创建库位
export function createLocation(data) {
  return request({
    url: '/api/stock/location',
    method: 'post',
    data
  })
}

// 更新库位
export function updateLocation(data) {
  return request({
    url: '/api/stock/location',
    method: 'put',
    data
  })
}

// 删除库位
export function deleteLocation(id) {
  return request({
    url: `/api/stock/location/${id}`,
    method: 'delete'
  })
}
