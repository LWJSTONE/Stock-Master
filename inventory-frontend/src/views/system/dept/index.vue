<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="demo-form-inline">
        <el-form-item label="部门名称">
          <el-input
            v-model="listQuery.name"
            placeholder="请输入部门名称"
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
        v-permission="'system:dept:add'"
        type="primary"
        icon="el-icon-plus"
        @click="handleCreate"
      >
        新增
      </el-button>
      <el-button
        type="info"
        icon="el-icon-sort"
        @click="toggleExpandAll"
      >
        {{ isExpandAll ? '折叠' : '展开' }}
      </el-button>
    </div>

    <!-- 树形表格 -->
    <el-table
      v-if="refreshTable"
      v-loading="listLoading"
      :data="list"
      border
      row-key="id"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      style="width: 100%"
    >
      <el-table-column prop="deptName" label="部门名称" min-width="180" />
      <el-table-column prop="orderNum" label="排序" width="80" align="center" />
      <el-table-column prop="leader" label="负责人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column prop="status" label="状态" width="80" align="center">
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
            v-permission="'system:dept:add'"
            type="text"
            size="small"
            icon="el-icon-plus"
            @click="handleCreate(scope.row)"
          >
            新增
          </el-button>
          <el-button
            v-permission="'system:dept:edit'"
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            v-permission="'system:dept:delete'"
            type="text"
            size="small"
            style="color: #F56C6C"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" @close="handleDialogClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级部门" prop="parentId">
              <el-cascader
                v-model="form.parentId"
                :options="deptOptions"
                :props="{ value: 'id', label: 'deptName', checkStrictly: true, emitPath: false }"
                placeholder="请选择上级部门"
                style="width: 100%"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="orderNum">
              <el-input-number v-model="form.orderNum" :min="0" :max="999" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">启用</el-radio>
                <el-radio label="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
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
  getDeptList,
  getDeptTree,
  createDept,
  updateDept,
  deleteDept,
  changeDeptStatus
} from '@/api/system'

export default {
  name: 'DeptManagement',
  data() {
    return {
      // 列表数据
      list: [],
      listLoading: false,
      isExpandAll: true,
      refreshTable: true,
      listQuery: {
        name: '',
        status: null
      },

      // 弹窗相关
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      submitLoading: false,
      form: {
        id: null,
        parentId: null,
        deptName: '',
        orderNum: 0,
        leader: '',
        phone: '',
        email: '',
        status: '0'
      },
      rules: {
        deptName: [
          { required: true, message: '请输入部门名称', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },

      // 部门选项
      deptOptions: []
    }
  },
  created() {
    this.getList()
    this.getDeptOptions()
  },
  methods: {
    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        const res = await getDeptList(this.listQuery)
        this.list = this.buildTree(res.data || [])
      } catch (error) {
        console.error('获取部门列表失败:', error)
      } finally {
        this.listLoading = false
      }
    },

    // 构建树结构
    buildTree(data) {
      const map = {}
      const result = []

      data.forEach(item => {
        map[item.id] = { ...item, children: [] }
      })

      data.forEach(item => {
        const parent = map[item.parentId]
        if (parent) {
          parent.children.push(map[item.id])
        } else {
          result.push(map[item.id])
        }
      })

      // 清空空的 children 数组
      const clearEmptyChildren = (arr) => {
        arr.forEach(item => {
          if (item.children && item.children.length === 0) {
            delete item.children
          } else if (item.children) {
            clearEmptyChildren(item.children)
          }
        })
      }
      clearEmptyChildren(result)

      return result
    },

    // 获取部门选项
    async getDeptOptions() {
      try {
        const res = await getDeptTree()
        this.deptOptions = [
          { id: 0, deptName: '根部门', children: res.data || [] }
        ]
      } catch (error) {
        console.error('获取部门树失败:', error)
      }
    },

    // 搜索
    handleFilter() {
      this.getList()
    },

    // 重置搜索
    handleReset() {
      this.listQuery = {
        name: '',
        status: null
      }
      this.getList()
    },

    // 展开/折叠
    toggleExpandAll() {
      this.refreshTable = false
      this.isExpandAll = !this.isExpandAll
      this.$nextTick(() => {
        this.refreshTable = true
      })
    },

    // 新增
    handleCreate(row) {
      this.resetForm()
      this.dialogTitle = '新增部门'
      this.isEdit = false
      if (row) {
        this.form.parentId = row.id
      }
      this.dialogVisible = true
    },

    // 编辑
    handleUpdate(row) {
      this.resetForm()
      this.dialogTitle = '编辑部门'
      this.isEdit = true
      this.form = { ...this.form, ...row }
      this.dialogVisible = true
    },

    // 删除
    handleDelete(row) {
      this.$confirm(`确认删除部门 "${row.deptName}" ?`, '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await deleteDept(row.id)
          this.$message.success('删除成功')
          this.getList()
          this.getDeptOptions()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    },

    // 状态切换
    async handleStatusChange(row) {
      try {
        await changeDeptStatus(row.id, row.status)
        this.$message.success('状态修改成功')
      } catch (error) {
        row.status = row.status === 1 ? 0 : 1
        console.error('修改状态失败:', error)
      }
    },

    // 提交表单
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return

        this.submitLoading = true
        try {
          if (this.isEdit) {
            await updateDept(this.form)
            this.$message.success('修改成功')
          } else {
            await createDept(this.form)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.getList()
          this.getDeptOptions()
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
        parentId: null,
        deptName: '',
        orderNum: 0,
        leader: '',
        phone: '',
        email: '',
        status: '0'
      }
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
}
</style>
