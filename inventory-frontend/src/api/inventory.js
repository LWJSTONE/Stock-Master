import request from '@/utils/request'

// 获取库存列表
export function getInventoryList(params) {
  return request({
    url: '/inventory/list',
    method: 'get',
    params
  })
}

// 获取库存详情
export function getInventoryDetail(id) {
  return request({
    url: `/inventory/${id}`,
    method: 'get'
  })
}

// 获取库存统计
export function getInventoryStats() {
  return request({
    url: '/inventory/stats',
    method: 'get'
  })
}

// 获取库存预警列表
export function getInventoryWarning(params) {
  return request({
    url: '/inventory/warning',
    method: 'get',
    params
  })
}

// 调整库存
export function adjustInventory(data) {
  return request({
    url: '/inventory/adjust',
    method: 'post',
    data
  })
}
