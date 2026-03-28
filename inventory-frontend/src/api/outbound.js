import request from '@/utils/request'

// 获取出库单列表
export function getOutboundList(params) {
  return request({
    url: '/api/outbound/list',
    method: 'get',
    params
  })
}

// 获取出库单详情
export function getOutboundDetail(id) {
  return request({
    url: `/api/outbound/${id}`,
    method: 'get'
  })
}

// 创建出库单
export function createOutbound(data) {
  return request({
    url: '/api/outbound',
    method: 'post',
    data
  })
}

// 更新出库单
export function updateOutbound(data) {
  return request({
    url: '/api/outbound',
    method: 'put',
    data
  })
}

// 删除出库单
export function deleteOutbound(id) {
  return request({
    url: `/api/outbound/${id}`,
    method: 'delete'
  })
}

// 审核出库单
export function approveOutbound(id) {
  return request({
    url: `/api/outbound/${id}/approve`,
    method: 'post'
  })
}
