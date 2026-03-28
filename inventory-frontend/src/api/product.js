import request from '@/utils/request'

// 获取产品列表
export function getProductList(params) {
  return request({
    url: '/api/product/list',
    method: 'get',
    params
  })
}

// 获取产品详情
export function getProductDetail(id) {
  return request({
    url: `/api/product/${id}`,
    method: 'get'
  })
}

// 创建产品
export function createProduct(data) {
  return request({
    url: '/api/product',
    method: 'post',
    data
  })
}

// 更新产品
export function updateProduct(data) {
  return request({
    url: '/api/product',
    method: 'put',
    data
  })
}

// 删除产品
export function deleteProduct(id) {
  return request({
    url: `/api/product/${id}`,
    method: 'delete'
  })
}

// 获取产品分类
export function getProductCategories() {
  return request({
    url: '/api/product/categories',
    method: 'get'
  })
}
