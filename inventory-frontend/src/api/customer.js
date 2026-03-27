import request from '@/utils/request'

// 获取客户列表
export function getCustomerList(params) {
  return request({
    url: '/customer/list',
    method: 'get',
    params
  })
}

// 获取客户详情
export function getCustomerDetail(id) {
  return request({
    url: `/customer/${id}`,
    method: 'get'
  })
}

// 创建客户
export function createCustomer(data) {
  return request({
    url: '/customer',
    method: 'post',
    data
  })
}

// 更新客户
export function updateCustomer(data) {
  return request({
    url: '/customer',
    method: 'put',
    data
  })
}

// 删除客户
export function deleteCustomer(id) {
  return request({
    url: `/customer/${id}`,
    method: 'delete'
  })
}
