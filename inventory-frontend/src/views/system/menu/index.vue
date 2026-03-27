<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="demo-form-inline">
        <el-form-item label="菜单名称">
          <el-input
            v-model="listQuery.name"
            placeholder="请输入菜单名称"
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
        v-permission="'system:menu:add'"
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
      <el-table-column prop="name" label="菜单名称" min-width="180" />
      <el-table-column prop="icon" label="图标" width="80" align="center">
        <template slot-scope="scope">
          <i v-if="scope.row.icon" :class="scope.row.icon" style="font-size: 18px" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" align="center" />
      <el-table-column prop="path" label="路由地址" min-width="150" />
      <el-table-column prop="component" label="组件路径" min-width="180" />
      <el-table-column prop="permission" label="权限标识" min-width="150" />
      <el-table-column prop="type" label="类型" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type === 1" type="primary">目录</el-tag>
          <el-tag v-else-if="scope.row.type === 2" type="success">菜单</el-tag>
          <el-tag v-else type="warning">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-permission="'system:menu:add'"
            type="text"
            size="small"
            icon="el-icon-plus"
            @click="handleCreate(scope.row)"
          >
            新增
          </el-button>
          <el-button
            v-permission="'system:menu:edit'"
            type="text"
            size="small"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            v-permission="'system:menu:delete'"
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="680px" @close="handleDialogClose">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单" prop="parentId">
              <el-cascader
                v-model="form.parentId"
                :options="menuOptions"
                :props="{ value: 'id', label: 'name', checkStrictly: true, emitPath: false }"
                placeholder="请选择上级菜单"
                style="width: 100%"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="type">
              <el-radio-group v-model="form.type">
                <el-radio :label="1">目录</el-radio>
                <el-radio :label="2">菜单</el-radio>
                <el-radio :label="3">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.type !== 3" :span="12">
            <el-form-item label="菜单图标" prop="icon">
              <el-popover
                v-model="showIconSelect"
                placement="bottom-start"
                trigger="click"
                width="400"
              >
                <div class="icon-select">
                  <div
                    v-for="icon in iconList"
                    :key="icon"
                    class="icon-item"
                    :class="{ 'active': form.icon === icon }"
                    @click="handleSelectIcon(icon)"
                  >
                    <i :class="icon" />
                  </div>
                </div>
                <el-input
                  slot="reference"
                  v-model="form.icon"
                  placeholder="请选择图标"
                  readonly
                  style="cursor: pointer"
                >
                  <template v-if="form.icon" slot="prefix">
                    <i :class="form.icon" style="line-height: 32px" />
                  </template>
                </el-input>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col v-if="form.type !== 3" :span="12">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.type === 2" :span="12">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.type === 3" :span="12">
            <el-form-item label="权限标识" prop="permission">
              <el-input v-model="form.permission" placeholder="请输入权限标识" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" :max="999" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.type !== 3" :span="12">
            <el-form-item label="是否外链" prop="isExternal">
              <el-radio-group v-model="form.isExternal">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.type !== 3" :span="12">
            <el-form-item label="是否缓存" prop="isCache">
              <el-radio-group v-model="form.isCache">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.type !== 3" :span="12">
            <el-form-item label="是否可见" prop="isVisible">
              <el-radio-group v-model="form.isVisible">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
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
  getMenuList,
  getMenuTree,
  createMenu,
  updateMenu,
  deleteMenu
} from '@/api/system'

export default {
  name: 'MenuManagement',
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
        name: '',
        path: '',
        component: '',
        permission: '',
        type: 1,
        icon: '',
        sort: 0,
        isExternal: 0,
        isCache: 0,
        isVisible: 1,
        status: 1
      },
      rules: {
        name: [
          { required: true, message: '请输入菜单名称', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择菜单类型', trigger: 'change' }
        ],
        path: [
          { required: true, message: '请输入路由地址', trigger: 'blur' }
        ]
      },

      // 菜单选项
      menuOptions: [],

      // 图标选择
      showIconSelect: false,
      iconList: [
        'el-icon-s-home',
        'el-icon-setting',
        'el-icon-user',
        'el-icon-s-custom',
        'el-icon-menu',
        'el-icon-s-goods',
        'el-icon-s-order',
        'el-icon-s-platform',
        'el-icon-s-management',
        'el-icon-s-data',
        'el-icon-s-marketing',
        'el-icon-s-shop',
        'el-icon-s-open',
        'el-icon-s-flag',
        'el-icon-s-comment',
        'el-icon-s-finance',
        'el-icon-s-claim',
        'el-icon-s-tools',
        'el-icon-s-cooperation',
        'el-icon-s-opportunity',
        'el-icon-s-operation',
        'el-icon-s-grid',
        'el-icon-s-check',
        'el-icon-s-release',
        'el-icon-s-ticket',
        'el-icon-document',
        'el-icon-folder',
        'el-icon-folder-opened',
        'el-icon-files',
        'el-icon-data-line',
        'el-icon-data-board',
        'el-icon-pie-chart',
        'el-icon-monitor',
        'el-icon-mobile-phone',
        'el-icon-edit',
        'el-icon-delete',
        'el-icon-search',
        'el-icon-refresh',
        'el-icon-key',
        'el-icon-lock',
        'el-icon-unlock',
        'el-icon-view',
        'el-icon-download',
        'el-icon-upload',
        'el-icon-plus',
        'el-icon-minus',
        'el-icon-check',
        'el-icon-close',
        'el-icon-star-on',
        'el-icon-star-off',
        'el-icon-location',
        'el-icon-phone',
        'el-icon-message',
        'el-icon-bell'
      ]
    }
  },
  created() {
    this.getList()
    this.getMenuOptions()
  },
  methods: {
    // 获取列表
    async getList() {
      this.listLoading = true
      try {
        const res = await getMenuList(this.listQuery)
        this.list = this.buildTree(res.data || [])
      } catch (error) {
        console.error('获取菜单列表失败:', error)
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

    // 获取菜单选项
    async getMenuOptions() {
      try {
        const res = await getMenuTree()
        this.menuOptions = [
          { id: 0, name: '根目录', children: res.data || [] }
        ]
      } catch (error) {
        console.error('获取菜单树失败:', error)
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
      this.dialogTitle = '新增菜单'
      this.isEdit = false
      if (row) {
        this.form.parentId = row.id
        this.form.type = row.type === 3 ? 3 : 2
      }
      this.dialogVisible = true
    },

    // 编辑
    handleUpdate(row) {
      this.resetForm()
      this.dialogTitle = '编辑菜单'
      this.isEdit = true
      this.form = { ...this.form, ...row }
      this.dialogVisible = true
    },

    // 删除
    handleDelete(row) {
      this.$confirm(`确认删除菜单 "${row.name}" ?`, '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await deleteMenu(row.id)
          this.$message.success('删除成功')
          this.getList()
          this.getMenuOptions()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    },

    // 选择图标
    handleSelectIcon(icon) {
      this.form.icon = icon
      this.showIconSelect = false
    },

    // 提交表单
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return

        this.submitLoading = true
        try {
          if (this.isEdit) {
            await updateMenu(this.form)
            this.$message.success('修改成功')
          } else {
            await createMenu(this.form)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.getList()
          this.getMenuOptions()
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
        name: '',
        path: '',
        component: '',
        permission: '',
        type: 1,
        icon: '',
        sort: 0,
        isExternal: 0,
        isCache: 0,
        isVisible: 1,
        status: 1
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

.icon-select {
  display: flex;
  flex-wrap: wrap;
  max-height: 300px;
  overflow-y: auto;

  .icon-item {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    margin: 5px;
    font-size: 20px;

    &:hover {
      color: #409EFF;
      border-color: #409EFF;
    }

    &.active {
      color: #409EFF;
      border-color: #409EFF;
      background: #ecf5ff;
    }
  }
}
</style>
