<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="demo-form-inline">
        <el-form-item label="用户名">
          <el-input
            v-model="listQuery.username"
            placeholder="请输入用户名"
            style="width: 200px"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="listQuery.phone"
            placeholder="请输入手机号"
            style="width: 200px"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="listQuery.status" placeholder="请选择状态" style="width: 180px" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar-container">
      <el-button
        v-permission="'system:user:add'"
        type="primary"
        icon="el-icon-plus"
        @click="handleCreate"
      >
        新增
      </el-button>
      <el-button
        v-permission="'system:user:delete'"
        type="danger"
        icon="el-icon-delete"
        :disabled="multipleSelection.length === 0"
        @click="handleBatchDelete"
      >
        删除
      </el-button>
      <el-button
        v-permission="'system:user:export'"
        type="warning"
        icon="el-icon-download"
        @click="handleExport"
      >
        导出
      </el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="username" label="用户名" min-width="120" />
      <el-table-column prop="realName" label="姓名" min-width="100" />
      <el-table-column prop="deptName" label="部门" min-width="120" />
      <el-table-column prop="phone" label="手机号" min-width="130" />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="'0'"
            :inactive-value="'1'"
            active-text="启用"
            inactive-text="禁用"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-permission="'system:user:edit'"
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            v-permission="'system:user:delete'"
            type="text"
            size="small"
            style="color: #F56C6C"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
          <el-button
            v-permission="'system:user:resetPwd'"
            type="text"
            size="small"
            style="color: #E6A23C"
            icon="el-icon-key"
            @click="handleResetPwd(scope.row)"
          >
            重置密码
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-size="listQuery.pageSize"
      :current-page="listQuery.pageNum"
      :page-sizes="[10, 20, 50, 100]"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" @close="handleDialogClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-cascader
            v-model="form.deptId"
            :options="deptOptions"
            :props="{ value: 'id', label: 'deptName', checkStrictly: true, emitPath: false }"
            placeholder="请选择部门"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="item in roleOptions"
              :key="item.id"
              :label="item.roleName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
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
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  batchDeleteUsers,
  resetPassword,
  changeUserStatus,
  exportUser
} from '@/api/system'
import { getDeptTree } from '@/api/system'
import { getAllRoles } from '@/api/system'

export default {
  name: 'UserManagement',
  data() {
    return {
      // 列表数据
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        username: '',
        phone: '',
        status: null
      },
      multipleSelection: [],

      // 弹窗相关
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      submitLoading: false,
      form: {
        id: null,
        username: '',
        password: '',
        realName: '',
        deptId: null,
        phone: '',
        email: '',
        roleIds: [],
        status: '0',
        remark: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },

      // 下拉选项
      deptOptions: [],
      roleOptions: []
    }
  },
  created() {
    this.getList()
    this.getDeptTree()
    this.getRoleOptions()
  },
  methods: {
    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        const res = await getUserList(this.listQuery)
        this.list = res.data.list || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error('获取用户列表失败:', error)
      } finally {
        this.listLoading = false
      }
    },

    // 获取部门树
    async getDeptTree() {
      try {
        const res = await getDeptTree()
        this.deptOptions = res.data || []
      } catch (error) {
        console.error('获取部门树失败:', error)
      }
    },

    // 获取角色选项
    async getRoleOptions() {
      try {
        const res = await getAllRoles()
        this.roleOptions = res.data || []
      } catch (error) {
        console.error('获取角色选项失败:', error)
      }
    },

    // 搜索
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },

    // 重置搜索
    handleReset() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        username: '',
        phone: '',
        status: null
      }
      this.getList()
    },

    // 选择变化
    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    // 新增
    handleCreate() {
      this.resetForm()
      this.dialogTitle = '新增用户'
      this.isEdit = false
      this.dialogVisible = true
    },

    // 编辑
    handleUpdate(row) {
      this.resetForm()
      this.dialogTitle = '编辑用户'
      this.isEdit = true
      this.form = { ...this.form, ...row }
      this.dialogVisible = true
    },

    // 删除
    handleDelete(row) {
      this.$confirm(`确认删除用户 "${row.username}" ?`, '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await deleteUser(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    },

    // 批量删除
    handleBatchDelete() {
      const ids = this.multipleSelection.map(item => item.id)
      const names = this.multipleSelection.map(item => item.username).join('、')
      this.$confirm(`确认删除以下用户: ${names} ?`, '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await batchDeleteUsers(ids)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error('批量删除失败:', error)
        }
      }).catch(() => {})
    },

    // 重置密码
    handleResetPwd(row) {
      this.$confirm(`确认重置用户 "${row.username}" 的密码为初始密码?`, '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await resetPassword(row.id)
          this.$message.success('密码重置成功，初始密码为: 123456')
        } catch (error) {
          console.error('重置密码失败:', error)
        }
      }).catch(() => {})
    },

    // 状态切换
    async handleStatusChange(row) {
      try {
        await changeUserStatus(row.id, row.status)
        this.$message.success('状态修改成功')
      } catch (error) {
        row.status = row.status === 1 ? 0 : 1
        console.error('修改状态失败:', error)
      }
    },

    // 导出
    async handleExport() {
      try {
        const res = await exportUser(this.listQuery)
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '用户数据.xlsx'
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('导出失败:', error)
      }
    },

    // 提交表单
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return

        this.submitLoading = true
        try {
          if (this.isEdit) {
            await updateUser(this.form)
            this.$message.success('修改成功')
          } else {
            await createUser(this.form)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.getList()
        } catch (error) {
          console.error('提交失败:', error)
        } finally {
          this.submitLoading = false
        }
      })
    },

    // 弹窗关闭
    handleDialogClose() {
      this.$refs.form.resetFields()
      this.resetForm()
    },

    // 重置表单
    resetForm() {
      this.form = {
        id: null,
        username: '',
        password: '',
        realName: '',
        deptId: null,
        phone: '',
        email: '',
        roleIds: [],
        status: '0',
        remark: ''
      }
    },

    // 分页
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },

    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.listQuery.pageNum = 1
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
  }

  .toolbar-container {
    margin-bottom: 15px;
  }

  .el-pagination {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
