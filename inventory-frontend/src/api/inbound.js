import request from '@/utils/request'

// 获取入库单列表
export function getInboundList(params) {
  return request({
    url: '/api/inbound/list',
    method: 'get',
    params
  })
}

// 获取入库单详情
export function getInboundDetail(id) {
  return request({
    url: `/api/inbound/${id}`,
    method: 'get'
  })
}

// 创建入库单
export function createInbound(data) {
  return request({
    url: '/api/inbound',
    method: 'post',
    data
  })
}

// 更新入库单
export function updateInbound(data) {
  return request({
    url: '/api/inbound',
    method: 'put',
    data
  })
}

// 删除入库单
export function deleteInbound(id) {
  return request({
    url: `/api/inbound/${id}`,
    method: 'delete'
  })
}

// 审核入库单
export function approveInbound(id) {
  return request({
    url: `/api/inbound/${id}/approve`,
    method: 'post'
  })
}
