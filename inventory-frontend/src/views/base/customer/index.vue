<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.name"
        placeholder="客户名称"
        style="width: 200px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
      />
      <el-input
        v-model="listQuery.contact"
        placeholder="联系人"
        style="width: 150px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
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
      <el-table-column prop="code" label="编码" width="120" />
      <el-table-column prop="name" label="客户名称" min-width="150" />
      <el-table-column prop="contact" label="联系人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="address" label="地址" min-width="200" />
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
            <el-form-item label="客户编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入客户编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contact">
              <el-input v-model="form.contact" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
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
        <el-row>
          <el-col :span="12">
            <el-form-item label="信用额度" prop="creditLimit">
              <el-input-number v-model="form.creditLimit" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择等级" style="width: 100%">
                <el-option label="普通客户" value="normal" />
                <el-option label="VIP客户" value="vip" />
                <el-option label="战略客户" value="strategic" />
              </el-select>
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
import { getCustomerList, createCustomer, updateCustomer, deleteCustomer } from '@/api/base'

export default {
  name: 'CustomerManagement',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        pageSize: 10,
        name: '',
        contact: ''
      },
      // 弹窗相关
      dialogVisible: false,
      dialogTitle: '',
      submitLoading: false,
      form: {
        id: null,
        code: '',
        name: '',
        contact: '',
        phone: '',
        address: '',
        email: '',
        creditLimit: 0,
        level: 'normal',
        status: 1,
        remark: ''
      },
      rules: {
        code: [{ required: true, message: '请输入客户编码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
        contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
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
        // 模拟数据
        this.list = [
          { id: 1, code: 'CUS001', name: '客户A', contact: '客户张三', phone: '13700137000', address: '广州市天河区天河路300号', email: 'zhangsan@cust.com', status: 1, level: 'vip', creditLimit: 100000, createTime: '2024-01-01 09:00:00' },
          { id: 2, code: 'CUS002', name: '客户B', contact: '客户李四', phone: '13700137001', address: '深圳市南山区科技园南路100号', email: 'lisi@cust.com', status: 1, level: 'normal', creditLimit: 50000, createTime: '2024-01-02 10:00:00' },
          { id: 3, code: 'CUS003', name: '客户C', contact: '客户王五', phone: '13700137002', address: '杭州市西湖区文三路200号', email: 'wangwu@cust.com', status: 0, level: 'strategic', creditLimit: 500000, createTime: '2024-01-03 11:00:00' }
        ]
        this.total = 3
        // const res = await getCustomerList(this.listQuery)
        // this.list = res.data.list
        // this.total = res.data.total
      } catch (error) {
        console.error(error)
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
      this.dialogTitle = '新增客户'
      this.dialogVisible = true
    },
    
    // 编辑
    handleUpdate(row) {
      this.dialogTitle = '编辑客户'
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
            await updateCustomer(this.form)
            this.$message.success('修改成功')
          } else {
            await createCustomer(this.form)
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
      this.$confirm('确认删除该客户？删除后无法恢复', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await deleteCustomer(row.id)
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
        code: '',
        name: '',
        contact: '',
        phone: '',
        address: '',
        email: '',
        creditLimit: 0,
        level: 'normal',
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
