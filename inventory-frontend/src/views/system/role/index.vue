<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="demo-form-inline">
        <el-form-item label="角色名称">
          <el-input
            v-model="listQuery.name"
            placeholder="请输入角色名称"
            style="width: 200px"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="listQuery.status" placeholder="请选择状态" style="width: 150px" clearable>
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
        v-permission="'system:role:add'"
        type="primary"
        icon="el-icon-plus"
        @click="handleCreate"
      >
        新增
      </el-button>
      <el-button
        v-permission="'system:role:delete'"
        type="danger"
        icon="el-icon-delete"
        :disabled="multipleSelection.length === 0"
        @click="handleBatchDelete"
      >
        删除
      </el-button>
      <el-button
        v-permission="'system:role:export'"
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
      <el-table-column prop="name" label="角色名称" min-width="120" />
      <el-table-column prop="code" label="权限字符" min-width="120" />
      <el-table-column prop="sort" label="排序" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="250" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-permission="'system:role:edit'"
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            v-permission="'system:role:delete'"
            type="text"
            size="small"
            style="color: #F56C6C"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
          <el-button
            v-permission="'system:role:menu'"
            type="text"
            size="small"
            style="color: #67C23A"
            icon="el-icon-menu"
            @click="handleAssignMenu(scope.row)"
          >
            菜单权限
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :page-size="listQuery.size"
      :current-page="listQuery.page"
      :page-sizes="[10, 20, 50, 100]"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" @close="handleDialogClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="code">
          <el-input v-model="form.code" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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

    <!-- 分配菜单权限弹窗 -->
    <el-dialog title="分配菜单权限" :visible.sync="menuDialogVisible" width="500px">
      <el-form ref="menuForm" label-width="80px">
        <el-form-item label="角色名称">
          <el-input :value="currentRole.name" disabled />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree
            ref="menuTree"
            :data="menuTree"
            :props="{ children: 'children', label: 'name' }"
            :default-checked-keys="checkedMenuKeys"
            show-checkbox
            node-key="id"
            check-strictly
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="menuDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="menuSubmitLoading" @click="handleMenuSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  changeRoleStatus,
  exportRole,
  getRoleMenus,
  assignRoleMenus
} from '@/api/system'
import { getMenuTree } from '@/api/system'

export default {
  name: 'RoleManagement',
  data() {
    return {
      // 列表数据
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        size: 10,
        name: '',
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
        name: '',
        code: '',
        sort: 0,
        status: 1,
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入权限字符', trigger: 'blur' }
        ]
      },

      // 菜单权限相关
      menuDialogVisible: false,
      menuSubmitLoading: false,
      currentRole: {},
      menuTree: [],
      checkedMenuKeys: []
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
        const res = await getRoleList(this.listQuery)
        this.list = res.data.list || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error('获取角色列表失败:', error)
      } finally {
        this.listLoading = false
      }
    },

    // 搜索
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    // 重置搜索
    handleReset() {
      this.listQuery = {
        page: 1,
        size: 10,
        name: '',
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
      this.dialogTitle = '新增角色'
      this.isEdit = false
      this.dialogVisible = true
    },

    // 编辑
    handleUpdate(row) {
      this.resetForm()
      this.dialogTitle = '编辑角色'
      this.isEdit = true
      this.form = { ...this.form, ...row }
      this.dialogVisible = true
    },

    // 删除
    handleDelete(row) {
      this.$confirm(`确认删除角色 "${row.name}" ?`, '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await deleteRole(row.id)
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
      const names = this.multipleSelection.map(item => item.name).join('、')
      this.$confirm(`确认删除以下角色: ${names} ?`, '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await Promise.all(ids.map(id => deleteRole(id)))
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error('批量删除失败:', error)
        }
      }).catch(() => {})
    },

    // 状态切换
    async handleStatusChange(row) {
      try {
        await changeRoleStatus(row.id, row.status)
        this.$message.success('状态修改成功')
      } catch (error) {
        row.status = row.status === 1 ? 0 : 1
        console.error('修改状态失败:', error)
      }
    },

    // 导出
    async handleExport() {
      try {
        const res = await exportRole(this.listQuery)
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '角色数据.xlsx'
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
            await updateRole(this.form)
            this.$message.success('修改成功')
          } else {
            await createRole(this.form)
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
        name: '',
        code: '',
        sort: 0,
        status: 1,
        remark: ''
      }
    },

    // 分配菜单权限
    async handleAssignMenu(row) {
      this.currentRole = row
      this.menuDialogVisible = true

      // 获取菜单树
      try {
        const res = await getMenuTree()
        this.menuTree = res.data || []
      } catch (error) {
        console.error('获取菜单树失败:', error)
      }

      // 获取角色已有菜单
      try {
        const res = await getRoleMenus(row.id)
        this.checkedMenuKeys = res.data || []
        this.$nextTick(() => {
          this.$refs.menuTree.setCheckedKeys(this.checkedMenuKeys)
        })
      } catch (error) {
        console.error('获取角色菜单失败:', error)
      }
    },

    // 提交菜单权限
    async handleMenuSubmit() {
      this.menuSubmitLoading = true
      try {
        const menuIds = this.$refs.menuTree.getCheckedKeys()
        await assignRoleMenus(this.currentRole.id, menuIds)
        this.$message.success('分配菜单权限成功')
        this.menuDialogVisible = false
      } catch (error) {
        console.error('分配菜单权限失败:', error)
      } finally {
        this.menuSubmitLoading = false
      }
    },

    // 分页
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },

    handleSizeChange(val) {
      this.listQuery.size = val
      this.listQuery.page = 1
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
