<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.name"
        placeholder="仓库名称"
        style="width: 200px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
      />
      <el-select
        v-model="listQuery.status"
        placeholder="状态"
        clearable
        style="width: 120px"
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
      <el-table-column prop="whCode" label="仓库编码" width="120" />
      <el-table-column prop="whName" label="仓库名称" min-width="150" />
      <el-table-column prop="address" label="地址" min-width="200" />
      <el-table-column prop="principal" label="负责人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleUpdate(scope.row)">编辑</el-button>
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
            <el-form-item label="仓库编码" prop="whCode">
              <el-input v-model="form.whCode" placeholder="请输入仓库编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="whName">
              <el-input v-model="form.whName" placeholder="请输入仓库名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="仓库地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入仓库地址" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责人" prop="principal">
              <el-input v-model="form.principal" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="仓库面积" prop="area">
              <el-input v-model="form.area" placeholder="平方米">
                <template slot="append">㎡</template>
              </el-input>
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
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getWarehouseList, createWarehouse, updateWarehouse, deleteWarehouse } from '@/api/base'

export default {
  name: 'WarehouseManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        pageSize: 10,
        name: '',
        status: ''
      },
      // 弹窗相关
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      form: {
        id: null,
        whCode: '',
        whName: '',
        address: '',
        principal: '',
        phone: '',
        area: '',
        status: 1,
        remark: ''
      },
      rules: {
        whCode: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
        whName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
        address: [{ required: true, message: '请输入仓库地址', trigger: 'blur' }],
        principal: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        const res = await getWarehouseList({
          pageNum: this.listQuery.page,
          pageSize: this.listQuery.pageSize,
          whCode: this.listQuery.name,
          whName: this.listQuery.name,
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
      this.dialogTitle = '新增仓库'
      this.dialogVisible = true
    },
    
    // 编辑
    handleUpdate(row) {
      this.resetForm()
      this.dialogTitle = '编辑仓库'
      this.form = { ...this.form, ...row }
      this.dialogVisible = true
    },
    
    // 提交
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        
        this.submitLoading = true
        try {
          if (this.form.id) {
            await updateWarehouse(this.form)
            this.$message.success('修改成功')
          } else {
            await createWarehouse(this.form)
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
      this.$confirm('确认删除该仓库？删除后无法恢复', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteWarehouse(row.id)
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
        whCode: '',
        whName: '',
        address: '',
        principal: '',
        phone: '',
        area: '',
        status: 1,
        remark: ''
      }
      this.$refs.form && this.$refs.form.resetFields()
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
