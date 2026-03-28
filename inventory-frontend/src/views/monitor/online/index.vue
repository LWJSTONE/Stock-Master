<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.username"
        placeholder="用户名"
        style="width: 200px"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
    </div>

    <el-table :data="list" border style="width: 100%">
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
  </div>
</template>

<script>
export default {
  name: 'OnlineUsers',
  data() {
    return {
      list: [
        { id: 1, username: 'admin', ip: '192.168.1.100', location: '北京市', browser: 'Chrome', os: 'Windows 10', loginTime: '2024-01-01 08:00:00' },
        { id: 2, username: 'user1', ip: '192.168.1.101', location: '上海市', browser: 'Firefox', os: 'Mac OS', loginTime: '2024-01-01 09:30:00' }
      ],
      listQuery: {
        username: ''
      }
    }
  },
  methods: {
    handleFilter() {
      this.$message.info('搜索功能开发中')
    },
    handleForceLogout(row) {
      this.$confirm('确认强制下线该用户?', '提示', {
        type: 'warning'
      }).then(() => {
        this.$message.success('已强制下线')
      }).catch(() => {})
    }
  }
}
</script>
