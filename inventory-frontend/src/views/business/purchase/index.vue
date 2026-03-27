<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.orderNo"
        placeholder="采购单号"
        style="width: 180px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
      />
      <el-select
        v-model="listQuery.supplierId"
        placeholder="供应商"
        clearable
        style="width: 150px"
        class="filter-item"
      >
        <el-option
          v-for="item in supplierOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-select
        v-model="listQuery.status"
        placeholder="状态"
        clearable
        style="width: 120px"
        class="filter-item"
      >
        <el-option label="待审核" :value="0" />
        <el-option label="已审核" :value="1" />
        <el-option label="已入库" :value="2" />
        <el-option label="已取消" :value="3" />
      </el-select>
      <el-date-picker
        v-model="listQuery.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="width: 240px"
        class="filter-item"
        value-format="yyyy-MM-dd"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px" type="primary" icon="el-icon-plus" @click="handleCreate">
        新增
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table v-loading="listLoading" :data="list" border style="width: 100%">
      <el-table-column prop="orderNo" label="采购单号" width="160" />
      <el-table-column prop="supplierName" label="供应商" width="150" />
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
      <el-table-column prop="applicant" label="申请人" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="260" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="handleUpdate(scope.row)" 
            v-if="scope.row.status === 0"
          >编辑</el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="handleAudit(scope.row)" 
            v-if="scope.row.status === 0"
          >审核</el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="handleInbound(scope.row)" 
            v-if="scope.row.status === 1"
          >入库</el-button>
          <el-button 
            type="text" 
            size="small" 
            style="color: #E6A23C" 
            @click="handleCancel(scope.row)" 
            v-if="scope.row.status === 0"
          >取消</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="1000px" @close="resetForm">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="采购单号" prop="orderNo">
              <el-input v-model="form.orderNo" placeholder="自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" style="width: 100%">
                <el-option
                  v-for="item in supplierOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计到货日期" prop="expectedDate">
              <el-date-picker
                v-model="form.expectedDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 商品明细 -->
        <el-divider content-position="left">商品明细</el-divider>
        <div style="margin-bottom: 15px;">
          <el-button type="primary" size="small" @click="handleSelectProduct">选择商品</el-button>
        </div>
        
        <el-table :data="form.items" border style="width: 100%">
          <el-table-column prop="skuCode" label="SKU编码" width="140" />
          <el-table-column prop="skuName" label="商品名称" min-width="150" />
          <el-table-column prop="specValues" label="规格" width="120" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="price" label="单价" width="100">
            <template slot-scope="scope">
              <el-input-number 
                v-model="scope.row.price" 
                :min="0" 
                :precision="2" 
                size="small" 
                style="width: 100%"
                @change="calculateAmount(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100">
            <template slot-scope="scope">
              <el-input-number 
                v-model="scope.row.quantity" 
                :min="1" 
                size="small" 
                style="width: 100%"
                @change="calculateAmount(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="100">
            <template slot-scope="scope">
              ¥{{ scope.row.amount.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="scope">
              <el-button 
                type="text" 
                size="small" 
                style="color: #F56C6C" 
                @click="removeItem(scope.$index)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div style="margin-top: 15px; text-align: right; font-size: 16px;">
          合计金额：<span style="color: #F56C6C; font-weight: bold;">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
        
        <el-form-item label="备注" prop="remark" style="margin-top: 15px;">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 商品选择弹窗 -->
    <el-dialog title="选择商品" :visible.sync="productSelectVisible" width="800px">
      <div class="filter-container" style="padding: 0;">
        <el-input
          v-model="productQuery.keyword"
          placeholder="商品编码/名称"
          style="width: 200px"
          class="filter-item"
          clearable
          @keyup.enter.native="searchProduct"
        />
        <el-button class="filter-item" type="primary" size="small" @click="searchProduct">搜索</el-button>
      </div>
      
      <el-table 
        ref="productTable"
        :data="productList" 
        border 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="skuCode" label="SKU编码" width="140" />
        <el-table-column prop="skuName" label="商品名称" />
        <el-table-column prop="specValues" label="规格" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="参考价" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
      </el-table>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="productSelectVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmSelectProduct">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog title="采购订单详情" :visible.sync="detailVisible" width="900px">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="采购单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detailData.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailData.applicant }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="预计到货日期">{{ detailData.expectedDate }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider content-position="left">商品明细</el-divider>
      <el-table :data="detailData.items" border style="width: 100%">
        <el-table-column prop="skuCode" label="SKU编码" width="140" />
        <el-table-column prop="skuName" label="商品名称" />
        <el-table-column prop="specValues" label="规格" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="单价" width="100">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="amount" label="金额" width="100">
          <template slot-scope="scope">¥{{ scope.row.amount }}</template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 15px; text-align: right; font-size: 16px;">
        合计金额：<span style="color: #F56C6C; font-weight: bold;">¥{{ detailData.totalAmount }}</span>
      </div>
      
      <el-divider content-position="left">备注</el-divider>
      <div>{{ detailData.remark || '无' }}</div>
    </el-dialog>

    <!-- 入库弹窗 -->
    <el-dialog title="采购入库" :visible.sync="inboundVisible" width="600px">
      <el-form ref="inboundForm" :model="inboundForm" :rules="inboundRules" label-width="100px">
        <el-form-item label="入库仓库" prop="warehouseId">
          <el-select v-model="inboundForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option
              v-for="item in warehouseOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="inboundVisible = false">取 消</el-button>
        <el-button type="primary" :loading="inboundLoading" @click="confirmInbound">确认入库</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getPurchaseList, 
  createPurchase, 
  updatePurchase, 
  deletePurchase, 
  auditPurchase,
  cancelPurchase,
  purchaseInbound
} from '@/api/business'
import { getSupplierOptions, getWarehouseOptions } from '@/api/base'

export default {
  name: 'PurchaseManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        pageSize: 10,
        orderNo: '',
        supplierId: '',
        status: '',
        dateRange: null
      },
      // 供应商选项
      supplierOptions: [
        { id: 1, name: '供应商A' },
        { id: 2, name: '供应商B' },
        { id: 3, name: '供应商C' }
      ],
      // 仓库选项
      warehouseOptions: [
        { id: 1, name: '主仓库' },
        { id: 2, name: '分仓库' }
      ],
      // 弹窗相关
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      form: {
        id: null,
        orderNo: '',
        supplierId: '',
        expectedDate: '',
        remark: '',
        items: []
      },
      rules: {
        supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
        expectedDate: [{ required: true, message: '请选择预计到货日期', trigger: 'change' }]
      },
      // 商品选择
      productSelectVisible: false,
      productQuery: {
        keyword: ''
      },
      productList: [],
      selectedProducts: [],
      // 详情
      detailVisible: false,
      detailData: {},
      // 入库
      inboundVisible: false,
      inboundLoading: false,
      inboundForm: {
        purchaseId: null,
        warehouseId: ''
      },
      inboundRules: {
        warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }]
      }
    }
  },
  computed: {
    totalAmount() {
      return this.form.items.reduce((sum, item) => sum + item.amount, 0)
    }
  },
  created() {
    this.getList()
    // this.loadOptions()
  },
  methods: {
    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        // 模拟数据
        this.list = [
          { 
            id: 1, 
            orderNo: 'PO202401010001', 
            supplierId: 1,
            supplierName: '供应商A', 
            totalAmount: 59990, 
            status: 0, 
            applicant: '张三',
            expectedDate: '2024-01-10',
            createTime: '2024-01-01 09:00:00',
            remark: '第一批采购',
            items: [
              { skuCode: 'PRD001-RED-L', skuName: '笔记本电脑-红色-L', specValues: '红色,L', unit: '台', price: 5999, quantity: 10, amount: 59990 }
            ]
          },
          { 
            id: 2, 
            orderNo: 'PO202401020001', 
            supplierId: 2,
            supplierName: '供应商B', 
            totalAmount: 79980, 
            status: 1, 
            applicant: '李四',
            expectedDate: '2024-01-12',
            createTime: '2024-01-02 10:00:00',
            remark: '',
            items: [
              { skuCode: 'PRD002-BLUE-M', skuName: '智能手机-蓝色-M', specValues: '蓝色,M', unit: '台', price: 3999, quantity: 20, amount: 79980 }
            ]
          },
          { 
            id: 3, 
            orderNo: 'PO202401030001', 
            supplierId: 1,
            supplierName: '供应商A', 
            totalAmount: 11990, 
            status: 2, 
            applicant: '王五',
            expectedDate: '2024-01-08',
            createTime: '2024-01-03 11:00:00',
            remark: '已入库',
            items: []
          }
        ]
        this.total = 3
        // const res = await getPurchaseList(this.listQuery)
        // this.list = res.data.list
        // this.total = res.data.total
      } catch (error) {
        console.error(error)
      } finally {
        this.listLoading = false
      }
    },
    
    // 状态相关
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '待审核', 1: '已审核', 2: '已入库', 3: '已取消' }
      return texts[status] || '未知'
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
    
    // 新增
    handleCreate() {
      this.dialogTitle = '新增采购订单'
      this.form = {
        id: null,
        orderNo: 'PO' + new Date().getTime(),
        supplierId: '',
        expectedDate: '',
        remark: '',
        items: []
      }
      this.dialogVisible = true
    },
    
    // 编辑
    handleUpdate(row) {
      this.dialogTitle = '编辑采购订单'
      this.form = { ...row, items: [...row.items] }
      this.dialogVisible = true
    },
    
    // 查看
    handleView(row) {
      this.detailData = row
      this.detailVisible = true
    },
    
    // 提交
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        
        if (this.form.items.length === 0) {
          this.$message.warning('请至少添加一个商品')
          return
        }
        
        this.submitLoading = true
        try {
          const data = {
            ...this.form,
            totalAmount: this.totalAmount
          }
          
          if (this.form.id) {
            await updatePurchase(data)
            this.$message.success('修改成功')
          } else {
            await createPurchase(data)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.getList()
        } catch (error) {
          console.error(error)
        } finally {
          this.submitLoading = false
        }
      })
    },
    
    // 删除
    handleDelete(row) {
      this.$confirm('确认删除该采购订单？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deletePurchase(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    
    // 审核
    handleAudit(row) {
      this.$confirm('确认审核通过该采购订单？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await auditPurchase(row.id)
          this.$message.success('审核成功')
          this.getList()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    
    // 取消
    handleCancel(row) {
      this.$confirm('确认取消该采购订单？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await cancelPurchase(row.id)
          this.$message.success('取消成功')
          this.getList()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    
    // 入库
    handleInbound(row) {
      this.inboundForm.purchaseId = row.id
      this.inboundForm.warehouseId = ''
      this.inboundVisible = true
    },
    
    // 确认入库
    confirmInbound() {
      this.$refs.inboundForm.validate(async valid => {
        if (!valid) return
        
        this.inboundLoading = true
        try {
          await purchaseInbound(this.inboundForm.purchaseId, this.inboundForm)
          this.$message.success('入库成功')
          this.inboundVisible = false
          this.getList()
        } catch (error) {
          console.error(error)
        } finally {
          this.inboundLoading = false
        }
      })
    },
    
    // 重置表单
    resetForm() {
      this.form = {
        id: null,
        orderNo: '',
        supplierId: '',
        expectedDate: '',
        remark: '',
        items: []
      }
      this.$refs.form && this.$refs.form.resetFields()
    },
    
    // 选择商品
    handleSelectProduct() {
      this.productQuery.keyword = ''
      this.loadProductList()
      this.productSelectVisible = true
    },
    
    // 加载商品列表
    loadProductList() {
      // 模拟数据
      this.productList = [
        { id: 1, skuCode: 'PRD001-RED-L', skuName: '笔记本电脑-红色-L', specValues: '红色,L', unit: '台', price: 5999, stock: 100 },
        { id: 2, skuCode: 'PRD001-RED-M', skuName: '笔记本电脑-红色-M', specValues: '红色,M', unit: '台', price: 5799, stock: 50 },
        { id: 3, skuCode: 'PRD002-BLUE-M', skuName: '智能手机-蓝色-M', specValues: '蓝色,M', unit: '台', price: 3999, stock: 200 },
        { id: 4, skuCode: 'PRD003-2L', skuName: '洗衣液-2L', specValues: '2L', unit: '瓶', price: 39.9, stock: 500 }
      ]
    },
    
    // 搜索商品
    searchProduct() {
      this.loadProductList()
    },
    
    // 选择变化
    handleSelectionChange(selection) {
      this.selectedProducts = selection
    },
    
    // 确认选择商品
    confirmSelectProduct() {
      if (this.selectedProducts.length === 0) {
        this.$message.warning('请至少选择一个商品')
        return
      }
      
      this.selectedProducts.forEach(product => {
        // 检查是否已存在
        if (!this.form.items.find(item => item.skuCode === product.skuCode)) {
          this.form.items.push({
            skuId: product.id,
            skuCode: product.skuCode,
            skuName: product.skuName,
            specValues: product.specValues,
            unit: product.unit,
            price: product.price,
            quantity: 1,
            amount: product.price
          })
        }
      })
      
      this.productSelectVisible = false
      this.selectedProducts = []
    },
    
    // 计算金额
    calculateAmount(row) {
      row.amount = row.price * row.quantity
    },
    
    // 移除商品
    removeItem(index) {
      this.form.items.splice(index, 1)
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
