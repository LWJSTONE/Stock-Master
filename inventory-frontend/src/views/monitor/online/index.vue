<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="searchUsername"
        placeholder="用户名"
        style="width: 200px"
        class="filter-item"
        clearable
        @keyup.enter.native="handleFilter"
        @clear="handleFilter"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" type="info" icon="el-icon-refresh" @click="handleReset">
        重置
      </el-button>
    </div>

    <el-table :data="filteredList" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="ip" label="登录IP" />
      <el-table-column prop="location" label="登录地点" />
      <el-table-column prop="browser" label="浏览器" />
      <el-table-column prop="os" label="操作系统" />
      <el-table-column prop="loginTime" label="登录时间" width="180" />
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button type="text" size="small" style="color: #F56C6C" @click="handleForceLogout(scope.row)">
            强制下线
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="filteredList.length === 0" description="暂无在线用户数据" />
  </div>
</template>

<script>
export default {
  name: 'OnlineUsers',
  data() {
    return {
      list: [
        { id: 1, username: 'admin', ip: '192.168.1.100', location: '北京市', browser: 'Chrome 120', os: 'Windows 10', loginTime: '2024-01-01 08:00:00' },
        { id: 2, username: 'user1', ip: '192.168.1.101', location: '上海市', browser: 'Firefox 121', os: 'Mac OS', loginTime: '2024-01-01 09:30:00' },
        { id: 3, username: 'warehouse', ip: '192.168.1.102', location: '广州市', browser: 'Edge 120', os: 'Windows 11', loginTime: '2024-01-01 10:15:00' },
        { id: 4, username: 'sales', ip: '192.168.1.103', location: '深圳市', browser: 'Chrome 120', os: 'Linux', loginTime: '2024-01-01 11:00:00' }
      ],
      searchUsername: ''
    }
  },
  computed: {
    filteredList() {
      if (!this.searchUsername) {
        return this.list
      }
      return this.list.filter(item =>
        item.username.toLowerCase().includes(this.searchUsername.toLowerCase())
      )
    }
  },
  methods: {
    handleFilter() {
      // 搜索通过计算属性自动实现
    },
    handleReset() {
      this.searchUsername = ''
    },
    handleForceLogout(row) {
      this.$confirm(`确认强制下线用户 "${row.username}"?`, '提示', {
        type: 'warning'
      }).then(() => {
        // 从列表中移除
        const index = this.list.findIndex(item => item.id === row.id)
        if (index > -1) {
          this.list.splice(index, 1)
        }
        this.$message.success(`已强制下线用户: ${row.username}`)
      }).catch(() => {})
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
