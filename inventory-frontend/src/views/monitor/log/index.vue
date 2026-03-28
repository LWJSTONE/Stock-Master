<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="demo-form-inline">
        <el-form-item label="模块标题">
          <el-input
            v-model="listQuery.title"
            placeholder="请输入模块标题"
            style="width: 180px"
            clearable
          />
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input
            v-model="listQuery.operName"
            placeholder="请输入操作人员"
            style="width: 150px"
            clearable
          />
        </el-form-item>
        <el-form-item label="请求方式">
          <el-select v-model="listQuery.requestMethod" placeholder="请选择请求方式" style="width: 120px" clearable>
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="listQuery.status" placeholder="请选择状态" style="width: 100px" clearable>
            <el-option label="成功" :value="0" />
            <el-option label="失败" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="listQuery.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
            value-format="yyyy-MM-dd"
          />
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
        v-permission="'monitor:log:delete'"
        type="danger"
        icon="el-icon-delete"
        @click="handleClear"
      >
        清空
      </el-button>
      <el-button
        v-permission="'monitor:log:export'"
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
    >
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="title" label="模块标题" min-width="120" />
      <el-table-column prop="requestMethod" label="请求方式" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="getRequestMethodTag(scope.row.requestMethod)">
            {{ scope.row.requestMethod }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operUrl" label="请求URL" min-width="200" show-overflow-tooltip />
      <el-table-column prop="operName" label="操作人员" width="100" />
      <el-table-column prop="operIp" label="IP地址" width="130" />
      <el-table-column prop="operLocation" label="操作地点" min-width="120" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
            {{ scope.row.status === 0 ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operTime" label="操作时间" width="180" />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-permission="'monitor:log:detail'"
            type="text"
            size="small"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >
            详情
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

    <!-- 详情弹窗 -->
    <el-dialog title="操作日志详情" :visible.sync="detailDialogVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">
          {{ currentLog.id }}
        </el-descriptions-item>
        <el-descriptions-item label="模块标题">
          {{ currentLog.title }}
        </el-descriptions-item>
        <el-descriptions-item label="请求方式">
          <el-tag :type="getRequestMethodTag(currentLog.requestMethod)">
            {{ currentLog.requestMethod }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="currentLog.status === 0 ? 'success' : 'danger'">
            {{ currentLog.status === 0 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作人员">
          {{ currentLog.operName }}
        </el-descriptions-item>
        <el-descriptions-item label="操作IP">
          {{ currentLog.operIp }}
        </el-descriptions-item>
        <el-descriptions-item label="操作地点">
          {{ currentLog.operLocation }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">
          {{ currentLog.operTime }}
        </el-descriptions-item>
        <el-descriptions-item label="耗时">
          {{ currentLog.costTime }}ms
        </el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">
          {{ currentLog.method }}
        </el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">
          {{ currentLog.operUrl }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentLog.operParam" label="请求参数" :span="2">
          <el-input
            type="textarea"
            :value="currentLog.operParam"
            :rows="4"
            readonly
          />
        </el-descriptions-item>
        <el-descriptions-item v-if="currentLog.jsonResult" label="响应结果" :span="2">
          <el-input
            type="textarea"
            :value="currentLog.jsonResult"
            :rows="4"
            readonly
          />
        </el-descriptions-item>
        <el-descriptions-item v-if="currentLog.errorMsg" label="错误信息" :span="2">
          <el-input
            type="textarea"
            :value="currentLog.errorMsg"
            :rows="4"
            readonly
            class="error-text"
          />
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getLogList,
  deleteLog,
  clearLog,
  exportLog
} from '@/api/monitor'

export default {
  name: 'OperationLog',
  data() {
    return {
      // 列表数据
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        title: '',
        operName: '',
        requestMethod: '',
        status: null,
        startTime: '',
        endTime: ''
      },

      // 详情弹窗
      detailDialogVisible: false,
      currentLog: {}
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
        const res = await getLogList(this.listQuery)
        this.list = res.data.rows || []
        this.total = res.data.total || 0
      } catch (error) {
        console.error('获取日志列表失败:', error)
        this.$message.error('获取日志列表失败')
      } finally {
        this.listLoading = false
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
        title: '',
        operName: '',
        requestMethod: '',
        status: null,
        startTime: '',
        endTime: ''
      }
      this.getList()
    },

    // 查看详情
    handleView(row) {
      this.currentLog = row
      this.detailDialogVisible = true
    },

    // 删除
    handleDelete(row) {
      this.$confirm('确认删除该日志记录?', '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await deleteLog(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {})
    },

    // 清空日志
    handleClear() {
      this.$confirm('确认清空所有日志记录?', '提示', {
        type: 'warning'
      }).then(async() => {
        try {
          await clearLog()
          this.$message.success('清空成功')
          this.getList()
        } catch (error) {
          console.error('清空失败:', error)
        }
      }).catch(() => {})
    },

    // 导出
    async handleExport() {
      try {
        const res = await exportLog(this.listQuery)
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '操作日志.xlsx'
        link.click()
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('导出失败:', error)
      }
    },

    // 获取请求方式标签
    getRequestMethodTag(method) {
      const methodMap = {
        'GET': 'success',
        'POST': 'primary',
        'PUT': 'warning',
        'DELETE': 'danger'
      }
      return methodMap[method] || 'info'
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

.error-text {
  ::v-deep .el-textarea__inner {
    color: #F56C6C;
  }
}
</style>
