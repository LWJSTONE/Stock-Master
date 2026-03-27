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
export function auditPurchase(id) {
  return request({
    url: `/api/business/purchase/${id}/audit`,
    method: 'post'
  })
}

// 取消采购订单
export function cancelPurchase(id) {
  return request({
    url: `/api/business/purchase/${id}/cancel`,
    method: 'post'
  })
}

// 采购入库
export function purchaseInbound(id, data) {
  return request({
    url: `/api/business/purchase/${id}/inbound`,
    method: 'post',
    data
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
export function auditSale(id) {
  return request({
    url: `/api/business/sale/${id}/audit`,
    method: 'post'
  })
}

// 取消销售订单
export function cancelSale(id) {
  return request({
    url: `/api/business/sale/${id}/cancel`,
    method: 'post'
  })
}

// 销售出库
export function saleOutbound(id, data) {
  return request({
    url: `/api/business/sale/${id}/outbound`,
    method: 'post',
    data
  })
}

// ==================== 入库管理API ====================

// 获取入库单列表
export function getInboundList(params) {
  return request({
    url: '/api/business/inbound/list',
    method: 'get',
    params
  })
}

// 获取入库单详情
export function getInboundDetail(id) {
  return request({
    url: `/api/business/inbound/${id}`,
    method: 'get'
  })
}

// 创建入库单
export function createInbound(data) {
  return request({
    url: '/api/business/inbound',
    method: 'post',
    data
  })
}

// 审核入库单
export function auditInbound(id) {
  return request({
    url: `/api/business/inbound/${id}/audit`,
    method: 'post'
  })
}

// 删除入库单
export function deleteInbound(id) {
  return request({
    url: `/api/business/inbound/${id}`,
    method: 'delete'
  })
}

// ==================== 出库管理API ====================

// 获取出库单列表
export function getOutboundList(params) {
  return request({
    url: '/api/business/outbound/list',
    method: 'get',
    params
  })
}

// 获取出库单详情
export function getOutboundDetail(id) {
  return request({
    url: `/api/business/outbound/${id}`,
    method: 'get'
  })
}

// 创建出库单
export function createOutbound(data) {
  return request({
    url: '/api/business/outbound',
    method: 'post',
    data
  })
}

// 审核出库单
export function auditOutbound(id) {
  return request({
    url: `/api/business/outbound/${id}/audit`,
    method: 'post'
  })
}

// 删除出库单
export function deleteOutbound(id) {
  return request({
    url: `/api/business/outbound/${id}`,
    method: 'delete'
  })
}
