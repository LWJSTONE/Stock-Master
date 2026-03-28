<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.name"
        placeholder="商品名称"
        style="width: 200px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
      />
      <el-select
        v-model="listQuery.categoryId"
        placeholder="商品分类"
        clearable
        style="width: 180px"
        class="filter-item"
      >
        <el-option
          v-for="item in categoryOptions"
          :key="item.id"
          :label="item.name"
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
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
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
      <el-table-column prop="spuCode" label="商品编码" width="120" />
      <el-table-column prop="spuName" label="商品名称" min-width="150" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="brandName" label="品牌" width="120" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="handleSkuManage(scope.row)">SKU管理</el-button>
          <el-button type="text" size="small" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" @close="resetForm">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品编码" prop="spuCode">
              <el-input v-model="form.spuCode" placeholder="请输入商品编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品名称" prop="spuName">
              <el-input v-model="form.spuName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="item in categoryOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brandId">
              <el-select v-model="form.brandId" placeholder="请选择品牌" style="width: 100%" clearable>
                <el-option
                  v-for="item in brandOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="form.unit" placeholder="请选择单位" style="width: 100%">
                <el-option
                  v-for="item in unitOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- SKU管理弹窗 -->
    <el-dialog title="SKU管理" :visible.sync="skuDialogVisible" width="900px" top="5vh">
      <div style="margin-bottom: 15px;">
        <span style="font-weight: bold; margin-right: 20px;">商品：{{ currentProduct.name }}</span>
        <el-button type="primary" size="small" @click="handleGenerateSku">批量生成SKU</el-button>
        <el-button type="success" size="small" @click="handleAddSku">新增SKU</el-button>
      </div>
      
      <el-table :data="skuList" border style="width: 100%">
        <el-table-column prop="skuCode" label="SKU编码" width="150" />
        <el-table-column prop="skuName" label="SKU名称" />
        <el-table-column prop="specValues" label="规格值" width="180" />
        <el-table-column prop="price" label="价格" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本价" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.costPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEditSku(scope.row)">编辑</el-button>
            <el-button type="text" size="small" style="color: #F56C6C" @click="handleDeleteSku(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- SKU编辑弹窗 -->
    <el-dialog :title="skuDialogTitle" :visible.sync="skuFormDialogVisible" width="500px">
      <el-form ref="skuForm" :model="skuForm" :rules="skuRules" label-width="100px">
        <el-form-item label="SKU编码" prop="skuCode">
          <el-input v-model="skuForm.skuCode" placeholder="请输入SKU编码" />
        </el-form-item>
        <el-form-item label="SKU名称" prop="skuName">
          <el-input v-model="skuForm.skuName" placeholder="请输入SKU名称" />
        </el-form-item>
        <el-form-item label="规格值" prop="specValues">
          <el-input v-model="skuForm.specValues" placeholder="如：红色-L" />
        </el-form-item>
        <el-form-item label="销售价" prop="price">
          <el-input-number v-model="skuForm.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="成本价" prop="costPrice">
          <el-input-number v-model="skuForm.costPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="skuForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="skuFormDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmitSku">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 批量生成SKU弹窗 -->
    <el-dialog title="批量生成SKU（笛卡尔积）" :visible.sync="generateSkuDialogVisible" width="600px">
      <div style="margin-bottom: 15px;">
        <el-button type="primary" size="small" @click="addSpecRow">添加规格</el-button>
        <span style="color: #909399; font-size: 12px; margin-left: 10px;">系统将自动根据规格组合生成SKU</span>
      </div>
      
      <div v-for="(spec, index) in specList" :key="index" style="margin-bottom: 15px; padding: 15px; border: 1px solid #EBEEF5; border-radius: 4px;">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-input v-model="spec.name" placeholder="规格名称（如：颜色）" />
          </el-col>
          <el-col :span="14">
            <el-input v-model="spec.values" placeholder="规格值，用逗号分隔（如：红色,蓝色,绿色）" />
          </el-col>
          <el-col :span="2">
            <el-button type="danger" icon="el-icon-delete" circle @click="removeSpecRow(index)" />
          </el-col>
        </el-row>
      </div>
      
      <div v-if="previewSkuList.length > 0" style="margin-top: 20px;">
        <div style="font-weight: bold; margin-bottom: 10px;">预览生成结果：</div>
        <el-tag v-for="(sku, index) in previewSkuList" :key="index" style="margin: 3px;">
          {{ sku }}
        </el-tag>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="generateSkuDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmGenerateSku">生成SKU</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getProductList, 
  createProduct, 
  updateProduct, 
  deleteProduct,
  getCategoryList,
  getSkuList,
  createSku,
  updateSku,
  deleteSku,
  generateSku,
  getBrandOptions,
  getUnitList
} from '@/api/base'

export default {
  name: 'ProductManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        pageSize: 10,
        name: '',
        categoryId: '',
        status: ''
      },
      // 分类选项
      categoryOptions: [
        { id: 1, name: '电子产品' },
        { id: 2, name: '日用品' },
        { id: 3, name: '食品' },
        { id: 4, name: '服装' },
        { id: 5, name: '家居' }
      ],
      // 品牌选项
      brandOptions: [
        { id: 1, name: '华为' },
        { id: 2, name: '小米' },
        { id: 3, name: '苹果' },
        { id: 4, name: '联想' }
      ],
      // 单位选项
      unitOptions: [
        { value: '件', label: '件' },
        { value: '台', label: '台' },
        { value: '个', label: '个' },
        { value: '箱', label: '箱' },
        { value: '套', label: '套' },
        { value: '瓶', label: '瓶' },
        { value: 'kg', label: '千克' }
      ],
      // 弹窗相关
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      form: {
        id: null,
        spuCode: '',
        spuName: '',
        categoryId: '',
        brand: '',
        unit: '',
        status: 1,
        description: ''
      },
      rules: {
        spuCode: [{ required: true, message: '请输入商品编码', trigger: 'blur' }],
        spuName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
        unit: [{ required: true, message: '请选择单位', trigger: 'change' }]
      },
      // SKU相关
      skuDialogVisible: false,
      skuFormDialogVisible: false,
      generateSkuDialogVisible: false,
      skuDialogTitle: '',
      currentProduct: {},
      skuList: [],
      skuForm: {
        id: null,
        skuCode: '',
        skuName: '',
        specValues: '',
        price: 0,
        costPrice: 0,
        status: 1
      },
      skuRules: {
        skuCode: [{ required: true, message: '请输入SKU编码', trigger: 'blur' }],
        skuName: [{ required: true, message: '请输入SKU名称', trigger: 'blur' }],
        price: [{ required: true, message: '请输入销售价', trigger: 'blur' }]
      },
      // 规格列表
      specList: [],
      previewSkuList: []
    }
  },
  created() {
    this.getList()
    this.loadCategories()
    this.loadBrands()
    this.loadUnits()
  },
  watch: {
    specList: {
      handler() {
        this.generatePreview()
      },
      deep: true
    }
  },
  methods: {
    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        const res = await getProductList({
          pageNum: this.listQuery.page,
          pageSize: this.listQuery.pageSize,
          spuCode: this.listQuery.name,
          spuName: this.listQuery.name,
          categoryId: this.listQuery.categoryId,
          status: this.listQuery.status
        })
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
    
    // 加载分类列表
    async loadCategories() {
      try {
        const res = await getCategoryList()
        this.categoryOptions = res.data || []
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    },
    
    // 加载品牌列表
    async loadBrands() {
      try {
        const res = await getBrandOptions()
        this.brandOptions = res.data || []
      } catch (error) {
        console.error('加载品牌失败:', error)
      }
    },
    
    // 加载单位列表
    async loadUnits() {
      try {
        const res = await getUnitList()
        this.unitOptions = res.data || []
      } catch (error) {
        console.error('加载单位失败:', error)
      }
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
      this.dialogTitle = '新增商品'
      this.dialogVisible = true
    },
    
    // 编辑
    handleUpdate(row) {
      this.dialogTitle = '编辑商品'
      this.form = { ...row }
      this.dialogVisible = true
    },
    
    // 提交
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        
        this.submitLoading = true
        try {
          if (this.form.id) {
            await updateProduct(this.form)
            this.$message.success('修改成功')
          } else {
            await createProduct(this.form)
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
      this.$confirm('确认删除该商品？删除后无法恢复', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteProduct(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    
    // 重置表单
    resetForm() {
      this.form = {
        id: null,
        spuCode: '',
        spuName: '',
        categoryId: '',
        brand: '',
        unit: '',
        status: 1,
        description: ''
      }
      this.$refs.form && this.$refs.form.resetFields()
    },
    
    // SKU管理
    async handleSkuManage(row) {
      this.currentProduct = row
      this.skuDialogVisible = true
      await this.loadSkuList(row.id)
    },
    
    // 加载SKU列表
    async loadSkuList(productId) {
      try {
        const res = await getSkuList(productId)
        this.skuList = res.data || []
      } catch (error) {
        console.error('加载SKU列表失败:', error)
        this.skuList = []
      }
    },
    
    // 新增SKU
    handleAddSku() {
      this.skuDialogTitle = '新增SKU'
      this.skuForm = {
        id: null,
        skuCode: '',
        skuName: '',
        specValues: '',
        price: 0,
        costPrice: 0,
        status: 1
      }
      this.skuFormDialogVisible = true
    },
    
    // 编辑SKU
    handleEditSku(row) {
      this.skuDialogTitle = '编辑SKU'
      this.skuForm = { ...row }
      this.skuFormDialogVisible = true
    },
    
    // 提交SKU
    handleSubmitSku() {
      this.$refs.skuForm.validate(async valid => {
        if (!valid) return
        
        try {
          const data = {
            ...this.skuForm,
            spuId: this.currentProduct.id
          }
          if (this.skuForm.id) {
            await updateSku(data)
            this.$message.success('修改成功')
          } else {
            await createSku(data)
            this.$message.success('新增成功')
          }
          this.skuFormDialogVisible = false
          this.loadSkuList(this.currentProduct.id)
        } catch (error) {
          console.error(error)
        }
      })
    },
    
    // 删除SKU
    handleDeleteSku(row) {
      this.$confirm('确认删除该SKU？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteSku(row.id)
          this.$message.success('删除成功')
          this.loadSkuList(this.currentProduct.id)
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    
    // 批量生成SKU
    handleGenerateSku() {
      this.specList = []
      this.previewSkuList = []
      this.generateSkuDialogVisible = true
    },
    
    // 添加规格行
    addSpecRow() {
      this.specList.push({
        name: '',
        values: ''
      })
    },
    
    // 移除规格行
    removeSpecRow(index) {
      this.specList.splice(index, 1)
    },
    
    // 生成预览
    generatePreview() {
      const specs = this.specList.filter(s => s.name && s.values)
      if (specs.length === 0) {
        this.previewSkuList = []
        return
      }
      
      // 笛卡尔积计算
      const valuesArrays = specs.map(s => s.values.split(',').map(v => v.trim()).filter(v => v))
      const combinations = this.cartesianProduct(valuesArrays)
      
      this.previewSkuList = combinations.map(combo => combo.join('-'))
    },
    
    // 笛卡尔积算法
    cartesianProduct(arrays) {
      if (arrays.length === 0) return [[]]
      if (arrays.length === 1) return arrays[0].map(item => [item])
      
      const [first, ...rest] = arrays
      const restProduct = this.cartesianProduct(rest)
      
      const result = []
      for (const item of first) {
        for (const combo of restProduct) {
          result.push([item, ...combo])
        }
      }
      return result
    },
    
    // 确认生成SKU
    async confirmGenerateSku() {
      const specs = this.specList.filter(s => s.name && s.values)
      if (specs.length === 0) {
        this.$message.warning('请至少添加一个规格')
        return
      }
      
      try {
        await generateSku(this.currentProduct.id, { specs })
        this.$message.success('SKU生成成功')
        this.generateSkuDialogVisible = false
        this.loadSkuList(this.currentProduct.id)
      } catch (error) {
        console.error(error)
      }
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
