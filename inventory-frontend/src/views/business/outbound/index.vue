<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.orderNo"
        placeholder="出库单号"
        style="width: 200px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
      />
      <el-select
        v-model="listQuery.warehouseId"
        placeholder="选择仓库"
        clearable
        style="width: 180px"
        class="filter-item"
      >
        <el-option
          v-for="item in warehouseOptions"
          :key="item.id"
          :label="item.whName"
          :value="item.id"
        />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" type="info" icon="el-icon-refresh" @click="handleReset">
        重置
      </el-button>
    </div>

    <el-table v-loading="listLoading" :data="list" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="出库单号" width="160" />
      <el-table-column prop="orderType" label="出库类型" width="100">
        <template slot-scope="scope">
          {{ getOrderTypeText(scope.row.orderType) }}
        </template>
      </el-table-column>
      <el-table-column prop="warehouseName" label="仓库" width="120">
        <template slot-scope="scope">
          {{ getWarehouseName(scope.row.warehouseId) }}
        </template>
      </el-table-column>
      <el-table-column prop="skuName" label="商品" min-width="150">
        <template slot-scope="scope">
          {{ scope.row.skuCode }} - {{ scope.row.skuName }}
        </template>
      </el-table-column>
      <el-table-column prop="changeQty" label="出库数量" width="100">
        <template slot-scope="scope">
          <span style="color: #F56C6C">{{ scope.row.changeQty }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="出库时间" width="160" />
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
    </el-table>

    <el-pagination
      v-show="total > 0"
      :current-page="listQuery.pageNum"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="listQuery.pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; text-align: right"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script>
import { getOutboundList } from '@/api/business'
import { getWarehouseOptions } from '@/api/base'

export default {
  name: 'OutboundManagement',
  data() {
    return {
      list: [],
      warehouseOptions: [],
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        warehouseId: '',
        orderType: 2 // 2表示销售出库
      },
      total: 0
    }
  },
  created() {
    this.loadOptions()
    this.getList()
  },
  methods: {
    // 加载下拉选项
    async loadOptions() {
      try {
        const warehouseRes = await getWarehouseOptions()
        this.warehouseOptions = warehouseRes.data || []
      } catch (error) {
        console.error('加载选项失败:', error)
      }
    },

    // 获取仓库名称
    getWarehouseName(warehouseId) {
      const warehouse = this.warehouseOptions.find(item => item.id === warehouseId)
      return warehouse ? warehouse.whName : warehouseId
    },

    // 获取订单类型文本
    getOrderTypeText(type) {
      const types = {
        1: '采购入库',
        2: '销售出库',
        3: '调拨入',
        4: '调拨出',
        5: '盘盈',
        6: '盘亏',
        7: '报损'
      }
      return types[type] || '未知'
    },

    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        const res = await getOutboundList(this.listQuery)
        this.list = res.data.rows || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error('获取出库列表失败:', error)
        this.list = []
        this.total = 0
      } finally {
        this.listLoading = false
      }
    },

    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },

    handleReset() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        warehouseId: '',
        orderType: 2
      }
      this.getList()
    },

    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },

    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    }
  }
}
</script>

<style scoped>
.filter-container {
  padding-bottom: 15px;
}
.filter-item {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
