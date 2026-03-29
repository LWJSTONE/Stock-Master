import request from '@/utils/request'

// ==================== 采购订单API ====================

// 获取采购订单列表
export function getPurchaseList(params) {
  return request({
    url: '/api/business/purchase/list',
    method: 'get',
    params
  })
}

// 获取采购订单详情
export function getPurchaseDetail(id) {
  return request({
    url: `/api/business/purchase/${id}`,
    method: 'get'
  })
}

// 创建采购订单
export function createPurchase(data) {
  return request({
    url: '/api/business/purchase',
    method: 'post',
    data
  })
}

// 更新采购订单
export function updatePurchase(data) {
  return request({
    url: '/api/business/purchase',
    method: 'put',
    data
  })
}

// 删除采购订单
export function deletePurchase(id) {
  return request({
    url: `/api/business/purchase/${id}`,
    method: 'delete'
  })
}

// 审核采购订单
export function auditPurchase(data) {
  return request({
    url: '/api/business/purchase/audit',
    method: 'put',
    data
  })
}

// 取消采购订单
export function cancelPurchase(purchaseId) {
  return request({
    url: '/api/business/purchase/cancel',
    method: 'put',
    params: { purchaseId }
  })
}

// 采购入库
export function purchaseInbound(purchaseId, warehouseId) {
  return request({
    url: '/api/business/purchase/instock',
    method: 'put',
    params: { purchaseId, warehouseId }
  })
}

// ==================== 销售订单API ====================

// 获取销售订单列表
export function getSaleList(params) {
  return request({
    url: '/api/business/sale/list',
    method: 'get',
    params
  })
}

// 获取销售订单详情
export function getSaleDetail(id) {
  return request({
    url: `/api/business/sale/${id}`,
    method: 'get'
  })
}

// 创建销售订单
export function createSale(data) {
  return request({
    url: '/api/business/sale',
    method: 'post',
    data
  })
}

// 更新销售订单
export function updateSale(data) {
  return request({
    url: '/api/business/sale',
    method: 'put',
    data
  })
}

// 删除销售订单
export function deleteSale(id) {
  return request({
    url: `/api/business/sale/${id}`,
    method: 'delete'
  })
}

// 审核销售订单
export function auditSale(data) {
  return request({
    url: '/api/business/sale/audit',
    method: 'put',
    data
  })
}

// 取消销售订单
export function cancelSale(saleId) {
  return request({
    url: '/api/business/sale/cancel',
    method: 'put',
    params: { saleId }
  })
}

// 销售出库
export function saleOutbound(saleId, warehouseId) {
  return request({
    url: '/api/business/sale/outstock',
    method: 'put',
    params: { saleId, warehouseId }
  })
}

// ==================== 库存记录API ====================

// 获取入库记录列表
export function getInboundList(params) {
  return request({
    url: '/api/stock/record/list',
    method: 'get',
    params: { ...params, orderType: 1 } // 1表示采购入库
  })
}

// 获取出库记录列表
export function getOutboundList(params) {
  return request({
    url: '/api/stock/record/list',
    method: 'get',
    params: { ...params, orderType: 2 } // 2表示销售出库
  })
}
