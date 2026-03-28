<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.checkNo"
        placeholder="盘点单号"
        style="width: 180px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
      />
      <el-select
        v-model="listQuery.warehouseId"
        placeholder="仓库"
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
      <el-select
        v-model="listQuery.checkStatus"
        placeholder="状态"
        clearable
        style="width: 140px"
        class="filter-item"
      >
        <el-option label="待盘点" :value="0" />
        <el-option label="已完成" :value="1" />
        <el-option label="已取消" :value="2" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px" type="primary" icon="el-icon-plus" @click="handleCreate">
        新增盘点
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table v-loading="listLoading" :data="list" border style="width: 100%">
      <el-table-column prop="checkNo" label="盘点单号" width="160" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="productCount" label="盘点商品数" width="120" />
      <el-table-column prop="differenceCount" label="差异数量" width="100">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.differenceCount > 0 ? '#67C23A' : scope.row.differenceCount < 0 ? '#F56C6C' : '' }">
            {{ scope.row.differenceCount > 0 ? '+' : '' }}{{ scope.row.differenceCount }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="differenceAmount" label="差异金额" width="120">
        <template slot-scope="scope">
          <span :style="{ color: scope.row.differenceAmount > 0 ? '#67C23A' : scope.row.differenceAmount < 0 ? '#F56C6C' : '' }">
            ¥{{ scope.row.differenceAmount.toFixed(2) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="checkTime" label="盘点时间" width="160" />
      <el-table-column prop="createBy" label="创建人" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="handleCheck(scope.row)" 
            v-if="scope.row.status === 0"
          >盘点</el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="handleConfirm(scope.row)" 
            v-if="scope.row.status === 0"
          >确认</el-button>
          <el-button 
            type="text" 
            size="small" 
            style="color: #F56C6C" 
            @click="handleDelete(scope.row)" 
            v-if="scope.row.status === 0"
          >删除</el-button>
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

    <!-- 新增盘点弹窗 -->
    <el-dialog title="新增盘点单" :visible.sync="createVisible" width="800px">
      <el-form ref="createForm" :model="createForm" :rules="createRules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="盘点单号" prop="checkNo">
              <el-input v-model="createForm.checkNo" placeholder="自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="盘点仓库" prop="warehouseId">
              <el-select v-model="createForm.warehouseId" placeholder="请选择仓库" style="width: 100%" @change="handleWarehouseChange">
                <el-option
                  v-for="item in warehouseOptions"
                  :key="item.id"
                  :label="item.whName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 商品选择 -->
        <el-divider content-position="left">选择盘点商品</el-divider>
        <el-table 
          ref="skuTable"
          :data="availableSkuList" 
          border 
          style="width: 100%"
          max-height="300"
          @selection-change="handleSkuSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="skuCode" label="SKU编码" width="140" />
          <el-table-column prop="skuName" label="商品名称" min-width="150" />
          <el-table-column prop="specValues" label="规格" width="100" />
          <el-table-column prop="quantity" label="当前库存" width="100" />
        </el-table>
        <div style="margin-top: 10px; color: #909399;">
          已选择 <span style="color: #409EFF;">{{ selectedSkuIds.length }}</span> 个商品
        </div>
        
        <el-form-item label="备注" prop="remark" style="margin-top: 15px;">
          <el-input v-model="createForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createVisible = false">取 消</el-button>
        <el-button type="primary" :loading="createLoading" @click="confirmCreate">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 盘点详情弹窗 -->
    <el-dialog title="盘点详情" :visible.sync="detailVisible" width="1000px">
      <el-descriptions :column="4" border size="small" style="margin-bottom: 15px;">
        <el-descriptions-item label="盘点单号">{{ detailData.checkNo }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ detailData.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="盘点时间">{{ detailData.checkTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-table :data="detailData.items" border style="width: 100%">
        <el-table-column prop="skuCode" label="SKU编码" width="140" />
        <el-table-column prop="skuName" label="商品名称" min-width="150" />
        <el-table-column prop="specValues" label="规格" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="systemQty" label="系统库存" width="100" />
        <el-table-column prop="actualQty" label="实盘数量" width="100">
          <template slot-scope="scope">
            <span v-if="!isChecking">{{ scope.row.actualQty }}</span>
            <el-input-number 
              v-else
              v-model="scope.row.actualQty" 
              :min="0" 
              size="small" 
              style="width: 100%"
            />
          </template>
        </el-table-column>
        <el-table-column prop="diffQty" label="差异" width="80">
          <template slot-scope="scope">
            <span :style="{ color: getDifferenceColor(scope.row) }">
              {{ getDifference(scope.row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template slot-scope="scope">¥{{ scope.row.price || 0 }}</template>
        </el-table-column>
        <el-table-column prop="differenceAmount" label="差异金额" width="100">
          <template slot-scope="scope">
            <span :style="{ color: getDifferenceColor(scope.row) }">
              ¥{{ getDifferenceAmount(scope.row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="库位" width="80" />
      </el-table>
      
      <div style="margin-top: 15px; text-align: right; font-size: 16px;">
        差异数量：<span :style="{ color: detailData.differenceCount > 0 ? '#67C23A' : detailData.differenceCount < 0 ? '#F56C6C' : '' }">
          {{ detailData.differenceCount > 0 ? '+' : '' }}{{ detailData.differenceCount }}
        </span>
        &nbsp;&nbsp;
        差异金额：<span :style="{ color: detailData.differenceAmount > 0 ? '#67C23A' : detailData.differenceAmount < 0 ? '#F56C6C' : '' }">
          ¥{{ detailData.differenceAmount }}
        </span>
      </div>
      
      <div slot="footer" class="dialog-footer" v-if="isChecking">
        <el-button @click="detailVisible = false">取 消</el-button>
        <el-button type="primary" :loading="saveLoading" @click="saveCheck">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCheckList, createCheck, deleteCheck, confirmCheck, updateCheckItem, getCheckItems } from '@/api/stock'
import { getWarehouseOptions, getInventoryList } from '@/api/base'

export default {
  name: 'StockCheck',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        pageSize: 10,
        checkNo: '',
        warehouseId: '',
        checkStatus: ''
      },
      // 仓库选项
      warehouseOptions: [],
      // 新增
      createVisible: false,
      createLoading: false,
      createForm: {
        checkNo: '',
        warehouseId: '',
        skuIds: [],
        remark: ''
      },
      createRules: {
        warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }]
      },
      // SKU选择
      availableSkuList: [],
      selectedSkuIds: [],
      // 详情
      detailVisible: false,
      detailData: {},
      isChecking: false,
      saveLoading: false
    }
  },
  created() {
    this.getList()
    this.loadWarehouseOptions()
  },
  methods: {
    // 加载仓库选项
    async loadWarehouseOptions() {
      try {
        const res = await getWarehouseOptions()
        this.warehouseOptions = res.data || []
      } catch (error) {
        console.error('加载仓库选项失败:', error)
      }
    },
    
    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        const res = await getCheckList(this.listQuery)
        this.list = res.data.rows || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error(error)
        this.list = []
        this.total = 0
      } finally {
        this.listLoading = false
      }
    },
    
    // 状态相关
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'success', 2: 'info' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '待盘点', 1: '已完成', 2: '已取消' }
      return texts[status] || '未知'
    },
    
    // 差异计算
    getDifference(row) {
      if (row.actualQty === null || row.actualQty === undefined) return '-'
      const diff = parseFloat(row.actualQty) - parseFloat(row.systemQty || 0)
      return diff > 0 ? '+' + diff : diff
    },
    getDifferenceColor(row) {
      if (row.actualQty === null || row.actualQty === undefined) return ''
      const diff = parseFloat(row.actualQty) - parseFloat(row.systemQty || 0)
      if (diff > 0) return '#67C23A'
      if (diff < 0) return '#F56C6C'
      return ''
    },
    getDifferenceAmount(row) {
      if (row.actualQty === null || row.actualQty === undefined) return '-'
      const diff = (parseFloat(row.actualQty) - parseFloat(row.systemQty || 0)) * (row.price || 0)
      return diff.toFixed(2)
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
    
    // 新增盘点
    handleCreate() {
      this.createForm = {
        checkNo: 'PD' + new Date().getTime(),
        warehouseId: '',
        skuIds: [],
        remark: ''
      }
      this.availableSkuList = []
      this.selectedSkuIds = []
      this.createVisible = true
    },
    
    // 仓库变化时加载商品
    async handleWarehouseChange(warehouseId) {
      if (!warehouseId) {
        this.availableSkuList = []
        return
      }
      try {
        const res = await getInventoryList({ warehouseId, pageNum: 1, pageSize: 100 })
        this.availableSkuList = (res.data.rows || []).map(item => ({
          id: item.skuId,
          skuCode: item.skuCode,
          skuName: item.skuName,
          specValues: item.specValues,
          quantity: item.availableQuantity
        }))
      } catch (error) {
        console.error('加载库存商品失败:', error)
        this.availableSkuList = []
      }
    },
    
    // 商品选择变化
    handleSkuSelectionChange(selection) {
      this.selectedSkuIds = selection.map(item => item.id)
    },
    
    // 确认新增
    confirmCreate() {
      this.$refs.createForm.validate(async valid => {
        if (!valid) return
        
        if (this.selectedSkuIds.length === 0) {
          this.$message.warning('请选择要盘点的商品')
          return
        }
        
        this.createLoading = true
        try {
          const data = {
            warehouseId: this.createForm.warehouseId,
            skuIds: this.selectedSkuIds,
            remark: this.createForm.remark
          }
          await createCheck(data)
          this.$message.success('创建成功')
          this.createVisible = false
          this.getList()
        } catch (error) {
          console.error(error)
          this.$message.error('创建失败，请稍后重试')
        } finally {
          this.createLoading = false
        }
      })
    },
    
    // 查看
    handleView(row) {
      this.detailData = { ...row, items: [...row.items] }
      this.isChecking = false
      this.detailVisible = true
    },
    
    // 盘点
    handleCheck(row) {
      this.detailData = { ...row, items: row.items.map(item => ({ ...item })) }
      this.isChecking = true
      this.detailVisible = true
    },
    
    // 保存盘点
    async saveCheck() {
      this.saveLoading = true
      try {
        // 更新盘点明细
        for (const item of this.detailData.items) {
          if (item.actualQuantity === null || item.actualQuantity === undefined) {
            this.$message.warning('请录入所有商品的实盘数量')
            return
          }
        }
        
        await updateCheckItem(this.detailData.id, { items: this.detailData.items })
        this.$message.success('保存成功')
        this.detailVisible = false
        this.getList()
      } catch (error) {
        console.error(error)
      } finally {
        this.saveLoading = false
      }
    },
    
    // 确认盘点
    handleConfirm(row) {
      this.$confirm('确认完成盘点？确认后将调整库存', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await confirmCheck(row.id)
          this.$message.success('确认成功')
          this.getList()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    
    // 删除
    handleDelete(row) {
      this.$confirm('确认删除该盘点单？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteCheck(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error(error)
        }
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
