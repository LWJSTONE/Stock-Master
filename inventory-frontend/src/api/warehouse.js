import request from '@/utils/request'

// 获取仓库列表
export function getWarehouseList(params) {
  return request({
    url: '/api/warehouse/list',
    method: 'get',
    params
  })
}

// 获取仓库详情
export function getWarehouseDetail(id) {
  return request({
    url: `/api/warehouse/${id}`,
    method: 'get'
  })
}

// 创建仓库
export function createWarehouse(data) {
  return request({
    url: '/api/warehouse',
    method: 'post',
    data
  })
}

// 更新仓库
export function updateWarehouse(data) {
  return request({
    url: '/api/warehouse',
    method: 'put',
    data
  })
}

// 删除仓库
export function deleteWarehouse(id) {
  return request({
    url: `/api/warehouse/${id}`,
    method: 'delete'
  })
}
