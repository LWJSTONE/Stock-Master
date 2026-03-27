import request from '@/utils/request'

// 获取供应商列表
export function getSupplierList(params) {
  return request({
    url: '/api/supplier/list',
    method: 'get',
    params
  })
}

// 获取供应商详情
export function getSupplierDetail(id) {
  return request({
    url: `/api/supplier/${id}`,
    method: 'get'
  })
}

// 创建供应商
export function createSupplier(data) {
  return request({
    url: '/api/supplier',
    method: 'post',
    data
  })
}

// 更新供应商
export function updateSupplier(data) {
  return request({
    url: '/api/supplier',
    method: 'put',
    data
  })
}

// 删除供应商
export function deleteSupplier(id) {
  return request({
    url: `/api/supplier/${id}`,
    method: 'delete'
  })
}
