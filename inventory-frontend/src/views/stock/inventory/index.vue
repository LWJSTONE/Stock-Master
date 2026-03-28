<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
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
      <el-input
        v-model="listQuery.keyword"
        placeholder="SKU编码/名称"
        style="width: 200px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" type="warning" icon="el-icon-download" @click="handleExport">
        导出
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table v-loading="listLoading" :data="list" border style="width: 100%">
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="skuCode" label="SKU编码" width="140" />
      <el-table-column prop="skuName" label="SKU名称" min-width="150" />
      <el-table-column prop="availableQuantity" label="可用库存" width="100">
        <template slot-scope="scope">
          <span :class="{ 'low-stock': scope.row.availableQuantity <= scope.row.minQuantity }">
            {{ scope.row.availableQuantity }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="lockedQuantity" label="冻结库存" width="100" />
      <el-table-column prop="totalQuantity" label="总库存" width="100" />
      <el-table-column prop="batchNo" label="批次号" width="140" />
      <el-table-column prop="location" label="库位" width="100" />
      <el-table-column prop="minQuantity" label="最低库存" width="100" />
      <el-table-column label="库存状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStockStatusType(scope.row)" size="small">
            {{ getStockStatusText(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleAdjust(scope.row)">调整</el-button>
          <el-button type="text" size="small" @click="handleDetail(scope.row)">明细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 库存调整弹窗 -->
    <el-dialog title="库存调整" :visible.sync="adjustVisible" width="500px">
      <el-form ref="adjustForm" :model="adjustForm" :rules="adjustRules" label-width="100px">
        <el-form-item label="SKU编码">
          <el-input :value="adjustForm.skuCode" disabled />
        </el-form-item>
        <el-form-item label="SKU名称">
          <el-input :value="adjustForm.skuName" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input :value="adjustForm.currentQuantity" disabled />
        </el-form-item>
        <el-form-item label="调整类型" prop="adjustType">
          <el-radio-group v-model="adjustForm.adjustType">
            <el-radio label="in">入库</el-radio>
            <el-radio label="out">出库</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整数量" prop="quantity">
          <el-input-number v-model="adjustForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-select v-model="adjustForm.reason" placeholder="请选择原因" style="width: 100%">
            <el-option label="盘点调整" value="check" />
            <el-option label="损耗" value="loss" />
            <el-option label="退货" value="return" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="adjustForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="adjustVisible = false">取 消</el-button>
        <el-button type="primary" :loading="adjustLoading" @click="confirmAdjust">确认调整</el-button>
      </div>
    </el-dialog>

    <!-- 库存明细弹窗 -->
    <el-dialog title="库存明细" :visible.sync="detailVisible" width="800px">
      <el-descriptions :column="3" border size="small" style="margin-bottom: 15px;">
        <el-descriptions-item label="SKU编码">{{ detailData.skuCode }}</el-descriptions-item>
        <el-descriptions-item label="SKU名称">{{ detailData.skuName }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ detailData.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="可用库存">{{ detailData.availableQuantity }}</el-descriptions-item>
        <el-descriptions-item label="冻结库存">{{ detailData.lockedQuantity }}</el-descriptions-item>
        <el-descriptions-item label="总库存">{{ detailData.totalQuantity }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider content-position="left">库存流水</el-divider>
      <el-table :data="inventoryLogs" border style="width: 100%">
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 'in' ? 'success' : 'danger'" size="small">
              {{ scope.row.type === 'in' ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="beforeQuantity" label="调整前" width="100" />
        <el-table-column prop="afterQuantity" label="调整后" width="100" />
        <el-table-column prop="orderNo" label="关联单号" width="160" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { getInventoryList, adjustInventory, getInventoryLog } from '@/api/stock'
import { getWarehouseOptions } from '@/api/base'

export default {
  name: 'InventoryQuery',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        pageSize: 10,
        warehouseId: '',
        keyword: ''
      },
      // 仓库选项
      warehouseOptions: [
        { id: 1, name: '主仓库' },
        { id: 2, name: '分仓库' }
      ],
      // 调整
      adjustVisible: false,
      adjustLoading: false,
      adjustForm: {
        inventoryId: null,
        skuCode: '',
        skuName: '',
        currentQuantity: 0,
        adjustType: 'in',
        quantity: 1,
        reason: '',
        remark: ''
      },
      adjustRules: {
        adjustType: [{ required: true, message: '请选择调整类型', trigger: 'change' }],
        quantity: [{ required: true, message: '请输入调整数量', trigger: 'blur' }],
        reason: [{ required: true, message: '请选择调整原因', trigger: 'change' }]
      },
      // 明细
      detailVisible: false,
      detailData: {},
      inventoryLogs: []
    }
  },
  created() {
    this.getList()
    // this.loadWarehouseOptions()
  },
  methods: {
    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        // 模拟数据
        this.list = [
          { id: 1, warehouseId: 1, warehouseName: '主仓库', skuCode: 'PRD001-RED-L', skuName: '笔记本电脑-红色-L', availableQuantity: 80, lockedQuantity: 20, totalQuantity: 100, minQuantity: 50, batchNo: 'B20240101001', location: 'A-01-01' },
          { id: 2, warehouseId: 1, warehouseName: '主仓库', skuCode: 'PRD002-BLUE-M', skuName: '智能手机-蓝色-M', availableQuantity: 200, lockedQuantity: 0, totalQuantity: 200, minQuantity: 100, batchNo: 'B20240102001', location: 'A-02-01' },
          { id: 3, warehouseId: 2, warehouseName: '分仓库', skuCode: 'PRD003-2L', skuName: '洗衣液-2L', availableQuantity: 30, lockedQuantity: 0, totalQuantity: 30, minQuantity: 50, batchNo: 'B20240103001', location: 'B-01-01' },
          { id: 4, warehouseId: 1, warehouseName: '主仓库', skuCode: 'PRD001-RED-M', skuName: '笔记本电脑-红色-M', availableQuantity: 45, lockedQuantity: 5, totalQuantity: 50, minQuantity: 30, batchNo: 'B20240104001', location: 'A-01-02' }
        ]
        this.total = 4
        // const res = await getInventoryList(this.listQuery)
        // this.list = res.data.list
        // this.total = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        this.listLoading = false
      }
    },
    
    // 库存状态
    getStockStatusType(row) {
      if (row.availableQuantity <= row.minQuantity) {
        return 'danger'
      } else if (row.availableQuantity <= row.minQuantity * 1.5) {
        return 'warning'
      }
      return 'success'
    },
    getStockStatusText(row) {
      if (row.availableQuantity <= row.minQuantity) {
        return '库存不足'
      } else if (row.availableQuantity <= row.minQuantity * 1.5) {
        return '库存偏低'
      }
      return '正常'
    },
    
    // 搜索
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    
    // 分页
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },
    
    // 导出
    handleExport() {
      this.$message.info('导出功能开发中')
    },
    
    // 调整库存
    handleAdjust(row) {
      this.adjustForm = {
        inventoryId: row.id,
        skuCode: row.skuCode,
        skuName: row.skuName,
        currentQuantity: row.availableQuantity,
        adjustType: 'in',
        quantity: 1,
        reason: '',
        remark: ''
      }
      this.adjustVisible = true
    },
    
    // 确认调整
    confirmAdjust() {
      this.$refs.adjustForm.validate(async valid => {
        if (!valid) return
        
        this.adjustLoading = true
        try {
          await adjustInventory(this.adjustForm)
          this.$message.success('调整成功')
          this.adjustVisible = false
          this.getList()
        } catch (error) {
          console.error(error)
        } finally {
          this.adjustLoading = false
        }
      })
    },
    
    // 查看明细
    async handleDetail(row) {
      this.detailData = row
      // 模拟流水数据
      this.inventoryLogs = [
        { createTime: '2024-01-01 10:00:00', type: 'in', quantity: 100, beforeQuantity: 0, afterQuantity: 100, orderNo: 'RK202401010001', remark: '采购入库' },
        { createTime: '2024-01-02 11:00:00', type: 'out', quantity: 20, beforeQuantity: 100, afterQuantity: 80, orderNo: 'CK202401020001', remark: '销售出库' }
      ]
      this.detailVisible = true
      // const res = await getInventoryLog({ inventoryId: row.id })
      // this.inventoryLogs = res.data
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
.low-stock {
  color: #F56C6C;
  font-weight: bold;
}
</style>
