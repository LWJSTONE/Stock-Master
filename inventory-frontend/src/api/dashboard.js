import request from '@/utils/request'

/**
 * 获取概览数据
 * @returns {Promise} 包含总库存、待审核订单、今日入库、今日出库等统计数据
 */
export function getOverview() {
  return request({
    url: '/api/monitor/dashboard/overview',
    method: 'get'
  })
}

/**
 * 获取出入库趋势数据
 * @param {Object} params - 查询参数
 * @param {number} params.days - 查询天数，默认7天
 * @returns {Promise} 包含日期、入库数量、出库数量的趋势数据
 */
export function getTrend(params) {
  return request({
    url: '/api/monitor/dashboard/trend',
    method: 'get',
    params
  })
}

/**
 * 获取商品分类库存占比
 * @returns {Promise} 包含各分类名称和库存占比的数据
 */
export function getCategoryRatio() {
  return request({
    url: '/api/monitor/dashboard/category',
    method: 'get'
  })
}

/**
 * 获取库存预警列表
 * @param {Object} params - 查询参数
 * @returns {Promise} 包含低于安全库存的商品列表
 */
export function getWarningList(params) {
  return request({
    url: '/api/monitor/dashboard/warning',
    method: 'get',
    params
  })
}

/**
 * 获取销量TOP商品
 * @param {Object} params - 查询参数
 * @param {number} params.limit - 返回数量，默认10
 * @returns {Promise} 包含商品名称和销量的TOP商品列表
 */
export function getTopProducts(params) {
  return request({
    url: '/api/monitor/dashboard/topProducts',
    method: 'get',
    params
  })
}
