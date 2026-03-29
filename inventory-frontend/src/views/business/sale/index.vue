<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.saleNo"
        placeholder="销售单号"
        style="width: 180px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
      />
      <el-select
        v-model="listQuery.customerId"
        placeholder="客户"
        clearable
        style="width: 180px"
        class="filter-item"
      >
        <el-option
          v-for="item in customerOptions"
          :key="item.id"
          :label="item.custName"
          :value="item.id"
        />
      </el-select>
      <el-select
        v-model="listQuery.status"
        placeholder="状态"
        clearable
        style="width: 140px"
        class="filter-item"
      >
        <el-option label="待审核" :value="0" />
        <el-option label="已审核" :value="1" />
        <el-option label="已出库" :value="2" />
        <el-option label="已取消" :value="3" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px" type="primary" icon="el-icon-plus" @click="handleCreate">
        新增
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table v-loading="listLoading" :data="list" border style="width: 100%">
      <el-table-column prop="saleNo" label="销售单号" width="160" />
      <el-table-column prop="customerName" label="客户" width="150">
        <template slot-scope="scope">
          {{ getCustomerName(scope.row.customerId) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template slot-scope="scope">
          ¥{{ (scope.row.totalAmount || 0).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
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
            @click="handleOutbound(scope.row)"
            v-if="scope.row.status === 1"
          >出库</el-button>
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
            v-if="scope.row.status === 0 || scope.row.status === 3"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="1000px" @close="resetForm">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="销售单号" prop="saleNo">
              <el-input v-model="form.saleNo" placeholder="自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" style="width: 100%">
                <el-option
                  v-for="item in customerOptions"
                  :key="item.id"
                  :label="item.custName"
                  :value="item.id"
                />
              </el-select>
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
          <el-table-column prop="specInfo" label="规格" width="120">
            <template slot-scope="scope">
              {{ formatSpecInfo(scope.row.specInfo) }}
            </template>
          </el-table-column>
          <el-table-column prop="price" label="销售价" width="140">
            <template slot-scope="scope">
              <el-input-number
                v-model="scope.row.price"
                :min="0"
                :precision="2"
                size="small"
                style="width: 120px"
                @change="calculateAmount(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="140">
            <template slot-scope="scope">
              <el-input-number
                v-model="scope.row.quantity"
                :min="1"
                size="small"
                style="width: 120px"
                @change="calculateAmount(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="100">
            <template slot-scope="scope">
              ¥{{ (scope.row.amount || 0).toFixed(2) }}
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
          v-model="productQuery.skuCode"
          placeholder="SKU编码"
          style="width: 200px"
          class="filter-item"
          clearable
          @keyup.enter.native="searchProduct"
        />
        <el-input
          v-model="productQuery.skuName"
          placeholder="商品名称"
          style="width: 200px; margin-left: 10px"
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
        <el-table-column prop="specInfo" label="规格" width="120">
          <template slot-scope="scope">
            {{ formatSpecInfo(scope.row.specInfo) }}
          </template>
        </el-table-column>
        <el-table-column prop="salePrice" label="销售价" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.salePrice || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本价" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.costPrice || 0 }}
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="productSelectVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmSelectProduct">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog title="销售订单详情" :visible.sync="detailVisible" width="900px">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="销售单号">{{ detailData.saleNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ getCustomerName(detailData.customerId) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ (detailData.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">商品明细</el-divider>
      <el-table :data="detailItems" border style="width: 100%">
        <el-table-column prop="skuCode" label="SKU编码" width="140" />
        <el-table-column prop="skuName" label="商品名称" />
        <el-table-column prop="specInfo" label="规格" width="120">
          <template slot-scope="scope">
            {{ formatSpecInfo(scope.row.specInfo) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="totalPrice" label="金额" width="100">
          <template slot-scope="scope">¥{{ scope.row.totalPrice }}</template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 15px; text-align: right; font-size: 16px;">
        合计金额：<span style="color: #F56C6C; font-weight: bold;">¥{{ (detailData.totalAmount || 0).toFixed(2) }}</span>
      </div>

      <el-divider content-position="left">备注</el-divider>
      <div>{{ detailData.remark || '无' }}</div>
    </el-dialog>

    <!-- 出库弹窗 -->
    <el-dialog title="销售出库" :visible.sync="outboundVisible" width="600px">
      <el-form ref="outboundForm" :model="outboundForm" :rules="outboundRules" label-width="100px">
        <el-form-item label="出库仓库" prop="warehouseId">
          <el-select v-model="outboundForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
            <el-option
              v-for="item in warehouseOptions"
              :key="item.id"
              :label="item.whName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="outboundVisible = false">取 消</el-button>
        <el-button type="primary" :loading="outboundLoading" @click="confirmOutbound">确认出库</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getSaleList,
  getSaleDetail,
  createSale,
  updateSale,
  deleteSale,
  auditSale,
  cancelSale,
  saleOutbound
} from '@/api/business'
import { getCustomerOptions, getWarehouseOptions, getAllSkuList } from '@/api/base'

export default {
  name: 'SaleManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        saleNo: '',
        customerId: '',
        status: ''
      },
      // 客户选项
      customerOptions: [],
      // 仓库选项
      warehouseOptions: [],
      // 弹窗相关
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      isEdit: false,
      form: {
        id: null,
        saleNo: '',
        customerId: '',
        remark: '',
        items: []
      },
      rules: {
        customerId: [{ required: true, message: '请选择客户', trigger: 'change' }]
      },
      // 商品选择
      productSelectVisible: false,
      productQuery: {
        skuCode: '',
        skuName: ''
      },
      productList: [],
      selectedProducts: [],
      // 详情
      detailVisible: false,
      detailData: {},
      detailItems: [],
      // 出库
      outboundVisible: false,
      outboundLoading: false,
      outboundForm: {
        saleId: null,
        warehouseId: ''
      },
      outboundRules: {
        warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }]
      }
    }
  },
  computed: {
    totalAmount() {
      return this.form.items.reduce((sum, item) => sum + (item.amount || 0), 0)
    }
  },
  created() {
    this.getList()
    this.loadOptions()
  },
  methods: {
    // 加载下拉选项
    async loadOptions() {
      try {
        const [customerRes, warehouseRes] = await Promise.all([
          getCustomerOptions(),
          getWarehouseOptions()
        ])
        this.customerOptions = customerRes.data || []
        this.warehouseOptions = warehouseRes.data || []
      } catch (error) {
        console.error('加载选项失败:', error)
      }
    },

    // 获取客户名称
    getCustomerName(customerId) {
      const customer = this.customerOptions.find(item => item.id === customerId)
      return customer ? customer.custName : customerId
    },

    // 格式化规格信息
    formatSpecInfo(specInfo) {
      if (!specInfo) return '-'
      try {
        const spec = JSON.parse(specInfo)
        return Object.values(spec).join(' / ')
      } catch {
        return specInfo
      }
    },

    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        const res = await getSaleList(this.listQuery)
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
      const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '待审核', 1: '已审核', 2: '已出库', 3: '已取消' }
      return texts[status] || '未知'
    },

    // 搜索
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },

    // 分页
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },

    // 新增
    handleCreate() {
      this.dialogTitle = '新增销售订单'
      this.isEdit = false
      this.form = {
        id: null,
        saleNo: 'SO' + new Date().getTime(),
        customerId: '',
        remark: '',
        items: []
      }
      this.dialogVisible = true
    },

    // 编辑
    async handleUpdate(row) {
      this.dialogTitle = '编辑销售订单'
      this.isEdit = true
      try {
        const res = await getSaleDetail(row.id)
        const data = res.data
        this.form = {
          id: data.id,
          saleNo: data.saleNo,
          customerId: data.customerId,
          remark: data.remark,
          items: (data.items || []).map(item => ({
            skuId: item.skuId,
            skuCode: item.skuCode,
            skuName: item.skuName,
            specInfo: item.specInfo,
            price: item.price,
            quantity: item.quantity,
            amount: item.totalPrice
          }))
        }
        this.dialogVisible = true
      } catch (error) {
        this.$message.error('获取订单详情失败')
        console.error(error)
      }
    },

    // 查看
    async handleView(row) {
      try {
        const res = await getSaleDetail(row.id)
        this.detailData = res.data
        this.detailItems = res.data.items || []
        this.detailVisible = true
      } catch (error) {
        this.$message.error('获取订单详情失败')
        console.error(error)
      }
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
            id: this.form.id,
            saleNo: this.form.saleNo,
            customerId: this.form.customerId,
            totalAmount: this.totalAmount,
            remark: this.form.remark,
            items: this.form.items.map(item => ({
              skuId: item.skuId,
              skuCode: item.skuCode,
              skuName: item.skuName,
              price: item.price,
              quantity: item.quantity
            }))
          }

          if (this.isEdit) {
            await updateSale(data)
            this.$message.success('修改成功')
          } else {
            await createSale(data)
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
      this.$confirm('确认删除该销售订单？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteSale(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },

    // 审核
    handleAudit(row) {
      this.$confirm('确认审核通过该销售订单？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await auditSale({ id: row.id, status: 1 })
          this.$message.success('审核成功')
          this.getList()
        } catch (error) {
          this.$message.error(error.message || '审核失败')
          console.error(error)
        }
      }).catch(() => {})
    },

    // 取消
    handleCancel(row) {
      this.$confirm('确认取消该销售订单？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await cancelSale(row.id)
          this.$message.success('取消成功')
          this.getList()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },

    // 出库
    handleOutbound(row) {
      this.outboundForm.saleId = row.id
      this.outboundForm.warehouseId = ''
      this.outboundVisible = true
    },

    // 确认出库
    confirmOutbound() {
      this.$refs.outboundForm.validate(async valid => {
        if (!valid) return

        this.outboundLoading = true
        try {
          await saleOutbound(this.outboundForm.saleId, this.outboundForm.warehouseId)
          this.$message.success('出库成功')
          this.outboundVisible = false
          this.getList()
        } catch (error) {
          this.$message.error(error.message || '出库失败')
          console.error(error)
        } finally {
          this.outboundLoading = false
        }
      })
    },

    // 重置表单
    resetForm() {
      this.form = {
        id: null,
        saleNo: '',
        customerId: '',
        remark: '',
        items: []
      }
      if (this.$refs.form) {
        this.$refs.form.resetFields()
      }
    },

    // 选择商品
    handleSelectProduct() {
      this.productQuery.skuCode = ''
      this.productQuery.skuName = ''
      this.loadProductList()
      this.productSelectVisible = true
    },

    // 加载商品列表
    async loadProductList() {
      try {
        const res = await getAllSkuList({ pageNum: 1, pageSize: 100, ...this.productQuery })
        this.productList = res.data.rows || []
      } catch (error) {
        console.error('加载商品列表失败:', error)
        this.productList = []
      }
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
        if (!this.form.items.find(item => item.skuId === product.id)) {
          this.form.items.push({
            skuId: product.id,
            skuCode: product.skuCode,
            skuName: product.skuName,
            specInfo: product.specInfo,
            price: product.salePrice || 0,
            quantity: 1,
            amount: product.salePrice || 0
          })
        }
      })

      this.productSelectVisible = false
      this.selectedProducts = []
    },

    // 计算金额
    calculateAmount(row) {
      row.amount = (row.price || 0) * (row.quantity || 0)
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
