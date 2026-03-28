<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.orderNo"
        placeholder="入库单号"
        style="width: 200px"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-date-picker
        v-model="listQuery.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="width: 260px"
        class="filter-item"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px" type="primary" icon="el-icon-plus" @click="handleCreate">
        新增入库单
      </el-button>
    </div>

    <el-table :data="list" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="入库单号" />
      <el-table-column prop="warehouseName" label="仓库" />
      <el-table-column prop="supplierName" label="供应商" />
      <el-table-column prop="totalQuantity" label="总数量" width="100" />
      <el-table-column prop="totalAmount" label="总金额" width="120" />
      <el-table-column prop="status" label="状态">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button type="text" size="small" @click="handleApprove(scope.row)" v-if="scope.row.status === 0">审核</el-button>
          <el-button type="text" size="small" style="color: #F56C6C" @click="handleDelete(scope.row)" v-if="scope.row.status === 0">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'InboundManagement',
  data() {
    return {
      list: [
        { id: 1, orderNo: 'RK202401010001', warehouseName: '主仓库', supplierName: '供应商A', totalQuantity: 100, totalAmount: 50000, status: 1, createTime: '2024-01-01 10:00:00' },
        { id: 2, orderNo: 'RK202401020001', warehouseName: '分仓库', supplierName: '供应商B', totalQuantity: 200, totalAmount: 80000, status: 0, createTime: '2024-01-02 14:30:00' }
      ],
      listQuery: {
        orderNo: '',
        dateRange: null
      }
    }
  },
  methods: {
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'success', 2: 'danger' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '待审核', 1: '已完成', 2: '已取消' }
      return texts[status] || '未知'
    },
    handleFilter() {
      this.$message.info('搜索功能开发中')
    },
    handleCreate() {
      this.$message.info('新增入库单功能开发中')
    },
    handleView(row) {
      this.$message.info('查看出库单: ' + row.orderNo)
    },
    handleApprove(row) {
      this.$confirm('确认审核通过该入库单?', '提示', {
        type: 'warning'
      }).then(() => {
        this.$message.success('审核成功')
      }).catch(() => {})
    },
    handleDelete(row) {
      this.$confirm('确认删除该入库单?', '提示', {
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
