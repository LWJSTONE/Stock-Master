<template>
  <div class="dashboard-container">
    <!-- 顶部标题 -->
    <div class="dashboard-header">
      <div class="header-title">
        <i class="el-icon-data-analysis"></i>
        企业级智能库存管理系统
      </div>
      <div class="header-right">
        <span class="update-time">
          <i class="el-icon-time"></i>
          数据更新时间：{{ updateTime }}
        </span>
        <el-button 
          type="primary" 
          size="small" 
          icon="el-icon-refresh" 
          :loading="refreshing"
          @click="refreshAllData"
        >
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 第一行：四个统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6">
        <StatCard
          title="总库存数量"
          :value="overview.totalStock"
          icon="el-icon-s-grid"
          color="#409EFF"
          :trend="overview.stockTrend"
          :loading="loading"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <StatCard
          title="待审核订单"
          :value="overview.pendingOrders"
          icon="el-icon-document-checked"
          color="#E6A23C"
          :loading="loading"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <StatCard
          title="今日入库"
          :value="overview.todayInbound"
          icon="el-icon-download"
          color="#67C23A"
          :trend="overview.inboundTrend"
          :loading="loading"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <StatCard
          title="今日出库"
          :value="overview.todayOutbound"
          icon="el-icon-upload2"
          color="#F56C6C"
          :trend="overview.outboundTrend"
          :loading="loading"
        />
      </el-col>
    </el-row>

    <!-- 第二行：趋势图 + 分类占比 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">
              <i class="el-icon-s-marketing"></i>
              近7天出入库趋势
            </span>
          </div>
          <div class="chart-wrapper">
            <div ref="trendChart" class="chart-container"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">
              <i class="el-icon-pie-chart"></i>
              商品分类库存占比
            </span>
          </div>
          <div class="chart-wrapper">
            <div ref="categoryChart" class="chart-container"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第三行：库存预警 + TOP商品 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">
              <i class="el-icon-warning-outline"></i>
              库存预警
            </span>
            <el-tag type="danger" size="small">低于安全库存标红</el-tag>
          </div>
          <div class="chart-wrapper">
            <div ref="warningChart" class="chart-container"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">
              <i class="el-icon-trophy"></i>
              销量TOP10商品
            </span>
          </div>
          <div class="chart-wrapper">
            <div ref="topProductsChart" class="chart-container"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 自动刷新配置 -->
    <div class="auto-refresh-config">
      <el-switch
        v-model="autoRefresh"
        active-text="自动刷新"
        @change="handleAutoRefreshChange"
      />
      <el-select 
        v-model="refreshInterval" 
        size="mini" 
        style="width: 100px; margin-left: 10px;"
        :disabled="!autoRefresh"
      >
        <el-option label="30秒" :value="30" />
        <el-option label="1分钟" :value="60" />
        <el-option label="5分钟" :value="300" />
      </el-select>
    </div>
  </div>
</template>

<script>
import StatCard from '@/components/StatCard'
import { getOverview, getTrend, getCategoryRatio, getWarningList, getTopProducts } from '@/api/dashboard'
import { colors, getLineOption, getBarOption, getHorizontalBarOption, getPieOption, addResizeListener } from '@/utils/echarts'

export default {
  name: 'Dashboard',
  components: {
    StatCard
  },
  data() {
    return {
      loading: false,
      refreshing: false,
      updateTime: '',
      autoRefresh: true,
      refreshInterval: 60,
      refreshTimer: null,
      // 概览数据
      overview: {
        totalStock: 0,
        pendingOrders: 0,
        todayInbound: 0,
        todayOutbound: 0,
        stockTrend: null,
        inboundTrend: null,
        outboundTrend: null
      },
      // 图表实例
      charts: {
        trend: null,
        category: null,
        warning: null,
        topProducts: null
      }
    }
  },
  mounted() {
    // 先初始化图表，再获取数据
    this.initCharts()
    this.initData()
    this.startAutoRefresh()
  },
  beforeDestroy() {
    this.stopAutoRefresh()
    // 销毁图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose()
      }
    })
  },
  methods: {
    // 初始化数据
    async initData() {
      this.loading = true
      try {
        await Promise.all([
          this.fetchOverview(),
          this.fetchTrend(),
          this.fetchCategoryRatio(),
          this.fetchWarningList(),
          this.fetchTopProducts()
        ])
        this.updateTime = this.formatTime(new Date())
      } catch (error) {
        console.error('获取数据失败:', error)
        // 使用模拟数据
        this.loadMockData()
      } finally {
        this.loading = false
      }
    },

    // 刷新所有数据
    async refreshAllData() {
      this.refreshing = true
      try {
        await this.initData()
        this.$message.success('数据刷新成功')
      } finally {
        this.refreshing = false
      }
    },

    // 获取概览数据
    async fetchOverview() {
      try {
        const res = await getOverview()
        console.log('Dashboard overview response:', res)
        if (res && res.data) {
          // 合并数据而不是替换，保留默认值
          this.overview = {
            ...this.overview,
            ...res.data
          }
          console.log('Overview data updated:', this.overview)
        } else {
          console.warn('Dashboard overview response has no data:', res)
        }
      } catch (error) {
        console.error('获取概览数据失败:', error)
      }
    },

    // 获取趋势数据
    async fetchTrend() {
      try {
        const res = await getTrend({ days: 7 })
        if (res.data) {
          // 后端返回的是数组格式，需要转换
          const trendData = Array.isArray(res.data) ? {
            dates: res.data.map(item => item.date),
            inbound: res.data.map(item => item.inbound),
            outbound: res.data.map(item => item.outbound)
          } : res.data
          this.renderTrendChart(trendData)
        }
      } catch (error) {
        console.error('获取趋势数据失败:', error)
      }
    },

    // 获取分类占比
    async fetchCategoryRatio() {
      try {
        const res = await getCategoryRatio()
        if (res.data) {
          this.renderCategoryChart(res.data)
        }
      } catch (error) {
        console.error('获取分类占比失败:', error)
      }
    },

    // 获取预警列表
    async fetchWarningList() {
      try {
        const res = await getWarningList({ pageNum: 1, pageSize: 10 })
        if (res.data) {
          this.renderWarningChart(res.data)
        }
      } catch (error) {
        console.error('获取预警数据失败:', error)
      }
    },

    // 获取TOP商品
    async fetchTopProducts() {
      try {
        const res = await getTopProducts({ limit: 10 })
        if (res.data) {
          this.renderTopProductsChart(res.data)
        }
      } catch (error) {
        console.error('获取TOP商品失败:', error)
      }
    },

    // 初始化图表
    initCharts() {
      this.charts.trend = this.$echarts.init(this.$refs.trendChart)
      this.charts.category = this.$echarts.init(this.$refs.categoryChart)
      this.charts.warning = this.$echarts.init(this.$refs.warningChart)
      this.charts.topProducts = this.$echarts.init(this.$refs.topProductsChart)

      // 添加resize监听
      Object.values(this.charts).forEach(chart => {
        addResizeListener(chart)
      })
      // 注意：不再调用loadMockData()，由initData()获取真实数据
    },

    // 渲染趋势图
    renderTrendChart(data) {
      const option = getLineOption({
        xAxis: {
          data: data.dates || []
        },
        series: [
          {
            name: '入库',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            itemStyle: {
              color: colors.success
            },
            areaStyle: {
              color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
              ])
            },
            data: data.inbound || []
          },
          {
            name: '出库',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            itemStyle: {
              color: colors.danger
            },
            areaStyle: {
              color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
                { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
              ])
            },
            data: data.outbound || []
          }
        ]
      })
      this.charts.trend.setOption(option)
    },

    // 渲染分类饼图
    renderCategoryChart(data) {
      const pieData = (data || []).map(item => ({
        name: item.categoryName || item.name,
        value: item.quantity || item.stockCount || item.value
      }))
      
      const option = getPieOption({
        data: pieData,
        radius: ['45%', '70%'],
        center: ['55%', '50%']
      })
      this.charts.category.setOption(option)
    },

    // 渲染预警柱状图
    renderWarningChart(data) {
      const products = (data || []).map(item => item.skuName || item.productName || item.name)
      const stocks = (data || []).map(item => item.currentStock || item.value)
      const safes = (data || []).map(item => item.safetyStock || item.safeStock || item.safe)
      
      const option = getBarOption({
        xAxis: {
          data: products,
          axisLabel: {
            rotate: 30,
            interval: 0
          }
        },
        yAxis: {
          name: '库存数量'
        },
        series: [
          {
            name: '当前库存',
            type: 'bar',
            barWidth: '40%',
            itemStyle: {
              color: (params) => {
                return params.value < safes[params.dataIndex] ? colors.danger : colors.primary
              },
              borderRadius: [4, 4, 0, 0]
            },
            data: stocks,
            label: {
              show: true,
              position: 'top',
              formatter: (params) => {
                return params.value < safes[params.dataIndex] ? `⚠️ ${params.value}` : params.value
              }
            }
          },
          {
            name: '安全库存线',
            type: 'line',
            symbol: 'none',
            lineStyle: {
              color: colors.warning,
              type: 'dashed',
              width: 2
            },
            data: safes
          }
        ],
        extra: {
          grid: {
            bottom: 80
          }
        }
      })
      this.charts.warning.setOption(option)
    },

    // 渲染TOP商品横向柱状图
    renderTopProductsChart(data) {
      const products = (data || []).map(item => item.skuName || item.productName || item.name).reverse()
      const sales = (data || []).map(item => item.quantity || item.salesCount || item.value).reverse()
      
      const option = getHorizontalBarOption({
        yAxis: {
          data: products,
          axisLabel: {
            width: 100,
            overflow: 'truncate',
            ellipsis: '...'
          }
        },
        xAxis: {
          name: '销量'
        },
        series: [
          {
            name: '销量',
            type: 'bar',
            barWidth: '60%',
            itemStyle: {
              color: new this.$echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#409EFF' },
                { offset: 1, color: '#79bbff' }
              ]),
              borderRadius: [0, 4, 4, 0]
            },
            data: sales,
            label: {
              show: true,
              position: 'right',
              color: '#606266'
            }
          }
        ],
        extra: {
          grid: {
            left: 120
          }
        }
      })
      this.charts.topProducts.setOption(option)
    },

    // 加载模拟数据
    loadMockData() {
      // 模拟概览数据
      this.overview = {
        totalStock: 88900,
        pendingOrders: 23,
        todayInbound: 1500,
        todayOutbound: 1200,
        stockTrend: 5.2,
        inboundTrend: 12.5,
        outboundTrend: -3.8
      }

      // 模拟趋势数据
      const trendData = {
        dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        inbound: [1200, 1320, 1010, 1340, 900, 2300, 2100],
        outbound: [2200, 1820, 1910, 2340, 2900, 3300, 3100]
      }
      this.renderTrendChart(trendData)

      // 模拟分类数据
      const categoryData = [
        { name: '电子产品', value: 3500 },
        { name: '服装鞋帽', value: 2800 },
        { name: '食品饮料', value: 2000 },
        { name: '家居用品', value: 1500 },
        { name: '其他', value: 800 }
      ]
      this.renderCategoryChart(categoryData)

      // 模拟预警数据
      const warningData = [
        { name: '商品A', value: 45, safe: 100 },
        { name: '商品B', value: 80, safe: 150 },
        { name: '商品C', value: 120, safe: 100 },
        { name: '商品D', value: 60, safe: 200 },
        { name: '商品E', value: 180, safe: 150 }
      ]
      this.renderWarningChart(warningData)

      // 模拟TOP商品数据
      const topData = [
        { name: '智能手机Pro', value: 2580 },
        { name: '无线蓝牙耳机', value: 1890 },
        { name: '平板电脑', value: 1560 },
        { name: '智能手表', value: 1320 },
        { name: '蓝牙音箱', value: 1180 },
        { name: '充电宝', value: 980 },
        { name: '数据线套装', value: 850 },
        { name: '手机壳', value: 720 },
        { name: '钢化膜', value: 650 },
        { name: '支架配件', value: 580 }
      ]
      this.renderTopProductsChart(topData)
    },

    // 开始自动刷新
    startAutoRefresh() {
      if (this.autoRefresh) {
        this.refreshTimer = setInterval(() => {
          this.initData()
        }, this.refreshInterval * 1000)
      }
    },

    // 停止自动刷新
    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },

    // 处理自动刷新变化
    handleAutoRefreshChange(val) {
      if (val) {
        this.startAutoRefresh()
      } else {
        this.stopAutoRefresh()
      }
    },

    // 格式化时间
    formatTime(date) {
      const pad = (n) => n.toString().padStart(2, '0')
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
    }
  },
  watch: {
    refreshInterval() {
      this.stopAutoRefresh()
      this.startAutoRefresh()
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  padding: 20px;

  .dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px 24px;
    background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(64, 158, 255, 0.3);

    .header-title {
      font-size: 24px;
      font-weight: bold;
      color: #fff;
      display: flex;
      align-items: center;

      i {
        font-size: 28px;
        margin-right: 12px;
      }
    }

    .header-right {
      display: flex;
      align-items: center;

      .update-time {
        color: rgba(255, 255, 255, 0.9);
        margin-right: 20px;
        font-size: 14px;

        i {
          margin-right: 6px;
        }
      }
    }
  }

  .stat-row {
    margin-bottom: 20px;
  }

  .chart-row {
    margin-bottom: 20px;
  }

  .chart-card {
    height: 100%;
    border-radius: 12px;
    overflow: hidden;

    ::v-deep .el-card__header {
      padding: 16px 20px;
      border-bottom: 1px solid #EBEEF5;
      background: #fafafa;
    }

    ::v-deep .el-card__body {
      padding: 20px;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .card-title {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
        display: flex;
        align-items: center;

        i {
          margin-right: 8px;
          color: #409EFF;
        }
      }
    }

    .chart-wrapper {
      width: 100%;
    }

    .chart-container {
      height: 320px;
    }
  }

  .auto-refresh-config {
    position: fixed;
    bottom: 30px;
    right: 30px;
    background: #fff;
    padding: 12px 20px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    z-index: 100;
  }
}

// 响应式适配
@media (max-width: 1400px) {
  .dashboard-container {
    .chart-card .chart-container {
      height: 280px;
    }
  }
}

@media (max-width: 1200px) {
  .dashboard-container {
    .dashboard-header .header-title {
      font-size: 20px;
      
      i {
        font-size: 24px;
      }
    }

    .chart-card .chart-container {
      height: 260px;
    }
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;

    .dashboard-header {
      flex-direction: column;
      padding: 16px;

      .header-title {
        font-size: 18px;
        margin-bottom: 12px;
      }

      .header-right {
        width: 100%;
        justify-content: space-between;

        .update-time {
          font-size: 12px;
          margin-right: 10px;
        }
      }
    }

    .chart-card {
      margin-bottom: 10px;

      .chart-container {
        height: 240px;
      }
    }

    .auto-refresh-config {
      bottom: 20px;
      right: 20px;
      padding: 10px 16px;
    }
  }
}
</style>
