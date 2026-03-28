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
        style="width: 150px"
        class="filter-item"
      >
        <el-option
          v-for="item in warehouseOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-date-picker
        v-model="listQuery.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="width: 260px"
        class="filter-item"
        value-format="yyyy-MM-dd"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" type="info" icon="el-icon-refresh" @click="handleReset">
        重置
      </el-button>
    </div>

    <el-table :data="filteredList" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="出库单号" width="160" />
      <el-table-column prop="relatedOrderNo" label="销售单号" width="160" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="customerName" label="客户" width="150" />
      <el-table-column prop="totalQuantity" label="总数量" width="100" />
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template slot-scope="scope">
          ¥{{ scope.row.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="handleApprove(scope.row)" 
            v-if="scope.row.status === 0"
          >审核</el-button>
          <el-button 
            type="text" 
            size="small" 
            style="color: #67C23A" 
            @click="handleConfirm(scope.row)"
            v-if="scope.row.status === 1"
          >确认出库</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-show="total > 0"
      :current-page="listQuery.page"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="listQuery.pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; text-align: right"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 出库单详情弹窗 -->
    <el-dialog title="出库单详情" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="3" border size="small" style="margin-bottom: 15px;">
        <el-descriptions-item label="出库单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="销售单号">{{ detailData.relatedOrderNo }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ detailData.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detailData.customerName }}</el-descriptions-item>
        <el-descriptions-item label="总数量">{{ detailData.totalQuantity }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ detailData.totalAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider content-position="left">出库明细</el-divider>
      <el-table :data="detailItems" border style="width: 100%">
        <el-table-column prop="skuCode" label="SKU编码" width="140" />
        <el-table-column prop="skuName" label="SKU名称" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="unitPrice" label="单价" width="120">
          <template slot-scope="scope">¥{{ scope.row.unitPrice.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="120">
          <template slot-scope="scope">¥{{ scope.row.totalPrice.toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'OutboundManagement',
  data() {
    return {
      list: [
        { 
          id: 1, 
          orderNo: 'CK202401010001', 
          relatedOrderNo: 'XS202401010001',
          warehouseId: 1,
          warehouseName: '主仓库', 
          customerName: '客户A', 
          totalQuantity: 50, 
          totalAmount: 25000, 
          status: 2, 
          createTime: '2024-01-01 11:00:00',
          remark: ''
        },
        { 
          id: 2, 
          orderNo: 'CK202401020001', 
          relatedOrderNo: 'XS202401020001',
          warehouseId: 2,
          warehouseName: '分仓库', 
          customerName: '客户B', 
          totalQuantity: 80, 
          totalAmount: 40000, 
          status: 1, 
          createTime: '2024-01-02 15:30:00',
          remark: '加急订单'
        },
        { 
          id: 3, 
          orderNo: 'CK202401030001', 
          relatedOrderNo: 'XS202401030001',
          warehouseId: 1,
          warehouseName: '主仓库', 
          customerName: '客户C', 
          totalQuantity: 30, 
          totalAmount: 15000, 
          status: 0, 
          createTime: '2024-01-03 09:30:00',
          remark: ''
        }
      ],
      warehouseOptions: [
        { id: 1, name: '主仓库' },
        { id: 2, name: '分仓库' }
      ],
      listQuery: {
        page: 1,
        pageSize: 10,
        orderNo: '',
        warehouseId: '',
        dateRange: null
      },
      total: 3,
      detailVisible: false,
      detailData: {},
      detailItems: []
    }
  },
  computed: {
    filteredList() {
      let result = this.list
      
      if (this.listQuery.orderNo) {
        result = result.filter(item => 
          item.orderNo.includes(this.listQuery.orderNo)
        )
      }
      
      if (this.listQuery.warehouseId) {
        result = result.filter(item => 
          item.warehouseId === this.listQuery.warehouseId
        )
      }
      
      if (this.listQuery.dateRange && this.listQuery.dateRange.length === 2) {
        const [start, end] = this.listQuery.dateRange
        result = result.filter(item => {
          const itemDate = item.createTime.split(' ')[0]
          return itemDate >= start && itemDate <= end
        })
      }
      
      return result
    }
  },
  methods: {
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '待审核', 1: '待出库', 2: '已出库', 3: '已取消' }
      return texts[status] || '未知'
    },
    
    handleFilter() {
      // 搜索通过计算属性自动实现
    },
    
    handleReset() {
      this.listQuery = {
        page: 1,
        pageSize: 10,
        orderNo: '',
        warehouseId: '',
        dateRange: null
      }
    },
    
    handleSizeChange(val) {
      this.listQuery.pageSize = val
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
    },
    
    handleView(row) {
      this.detailData = row
      // 模拟明细数据
      this.detailItems = [
        { skuCode: 'PRD001-RED-L', skuName: '笔记本电脑-红色-L', quantity: 25, unitPrice: 500, totalPrice: 12500 },
        { skuCode: 'PRD002-BLUE-M', skuName: '智能手机-蓝色-M', quantity: 25, unitPrice: 500, totalPrice: 12500 }
      ]
      this.detailVisible = true
    },
    
    handleApprove(row) {
      this.$confirm('确认审核通过该出库单?', '提示', {
        type: 'warning'
      }).then(() => {
        row.status = 1
        this.$message.success('审核成功')
      }).catch(() => {})
    },
    
    handleConfirm(row) {
      this.$confirm('确认执行出库操作? 出库后库存将减少。', '提示', {
        type: 'warning'
      }).then(() => {
        row.status = 2
        this.$message.success('出库成功，库存已更新')
      }).catch(() => {})
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
