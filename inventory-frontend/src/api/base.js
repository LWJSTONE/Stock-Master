import request from '@/utils/request'

// ==================== 商品管理API ====================

// 获取商品列表
export function getProductList(params) {
  return request({
    url: '/api/base/product/list',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProductDetail(id) {
  return request({
    url: `/api/base/product/${id}`,
    method: 'get'
  })
}

// 创建商品
export function createProduct(data) {
  return request({
    url: '/api/base/product',
    method: 'post',
    data
  })
}

// 更新商品
export function updateProduct(data) {
  return request({
    url: '/api/base/product',
    method: 'put',
    data
  })
}

// 删除商品
export function deleteProduct(id) {
  return request({
    url: `/api/base/product/${id}`,
    method: 'delete'
  })
}

// 获取商品分类列表
export function getCategoryList() {
  return request({
    url: '/api/base/category/list',
    method: 'get'
  })
}

// ==================== SKU管理API ====================

// 获取SKU列表
export function getSkuList(productId) {
  return request({
    url: `/api/base/product/${productId}/skuList`,
    method: 'get'
  })
}

// 获取所有SKU列表（用于商品选择）
export function getAllSkuList(params) {
  return request({
    url: '/api/base/sku/list',
    method: 'get',
    params
  })
}

// 创建SKU
export function createSku(data) {
  return request({
    url: '/api/base/sku',
    method: 'post',
    data
  })
}

// 更新SKU
export function updateSku(data) {
  return request({
    url: '/api/base/sku',
    method: 'put',
    data
  })
}

// 删除SKU
export function deleteSku(skuId) {
  return request({
    url: `/api/base/sku/${skuId}`,
    method: 'delete'
  })
}

// 批量生成SKU（笛卡尔积）
export function generateSku(productId, data) {
  return request({
    url: `/api/base/sku/batchGenerate/${productId}`,
    method: 'post',
    data
  })
}

// ==================== 仓库管理API ====================

// 获取仓库列表
export function getWarehouseList(params) {
  return request({
    url: '/api/base/warehouse/list',
    method: 'get',
    params
  })
}

// 获取仓库详情
export function getWarehouseDetail(id) {
  return request({
    url: `/api/base/warehouse/${id}`,
    method: 'get'
  })
}

// 创建仓库
export function createWarehouse(data) {
  return request({
    url: '/api/base/warehouse',
    method: 'post',
    data
  })
}

// 更新仓库
export function updateWarehouse(data) {
  return request({
    url: '/api/base/warehouse',
    method: 'put',
    data
  })
}

// 删除仓库
export function deleteWarehouse(id) {
  return request({
    url: `/api/base/warehouse/${id}`,
    method: 'delete'
  })
}

// 获取所有启用的仓库（下拉选择用）
export function getWarehouseOptions() {
  return request({
    url: '/api/base/warehouse/listAll',
    method: 'get'
  })
}

// ==================== 供应商管理API ====================

// 获取供应商列表
export function getSupplierList(params) {
  return request({
    url: '/api/base/supplier/list',
    method: 'get',
    params
  })
}

// 获取供应商详情
export function getSupplierDetail(id) {
  return request({
    url: `/api/base/supplier/${id}`,
    method: 'get'
  })
}

// 创建供应商
export function createSupplier(data) {
  return request({
    url: '/api/base/supplier',
    method: 'post',
    data
  })
}

// 更新供应商
export function updateSupplier(data) {
  return request({
    url: '/api/base/supplier',
    method: 'put',
    data
  })
}

// 删除供应商
export function deleteSupplier(id) {
  return request({
    url: `/api/base/supplier/${id}`,
    method: 'delete'
  })
}

// 获取所有启用的供应商（下拉选择用）
export function getSupplierOptions() {
  return request({
    url: '/api/base/supplier/listAll',
    method: 'get'
  })
}

// ==================== 客户管理API ====================

// 获取客户列表
export function getCustomerList(params) {
  return request({
    url: '/api/base/customer/list',
    method: 'get',
    params
  })
}

// 获取客户详情
export function getCustomerDetail(id) {
  return request({
    url: `/api/base/customer/${id}`,
    method: 'get'
  })
}

// 创建客户
export function createCustomer(data) {
  return request({
    url: '/api/base/customer',
    method: 'post',
    data
  })
}

// 更新客户
export function updateCustomer(data) {
  return request({
    url: '/api/base/customer',
    method: 'put',
    data
  })
}

// 删除客户
export function deleteCustomer(id) {
  return request({
    url: `/api/base/customer/${id}`,
    method: 'delete'
  })
}

// 获取所有启用的客户（下拉选择用）
export function getCustomerOptions() {
  return request({
    url: '/api/base/customer/listAll',
    method: 'get'
  })
}

// ==================== 品牌管理API ====================

// 获取品牌列表
export function getBrandList(params) {
  return request({
    url: '/api/base/brand/list',
    method: 'get',
    params
  })
}

// 获取所有品牌选项
export function getBrandOptions() {
  return request({
    url: '/api/base/brand/options',
    method: 'get'
  })
}

// ==================== 单位管理API ====================

// 获取单位列表
export function getUnitList() {
  return request({
    url: '/api/base/unit/list',
    method: 'get'
  })
}
