<template>
  <div class="navbar">
    <div class="left-menu">
      <hamburger
        :is-active="sidebar.opened"
        class="hamburger-container"
        @toggleClick="toggleSideBar"
      />
      <breadcrumb class="breadcrumb-container" />
    </div>
    <div class="right-menu">
      <el-dropdown class="avatar-container" trigger="click" placement="bottom-end">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar" />
          <span class="user-name">{{ userName }}</span>
          <i class="el-icon-arrow-down el-icon--right" />
        </div>
        <el-dropdown-menu slot="dropdown" class="user-dropdown">
          <router-link to="/">
            <el-dropdown-item>
              <i class="el-icon-s-home"></i>
              首页
            </el-dropdown-item>
          </router-link>
          <el-dropdown-item divided @click.native="logout">
            <i class="el-icon-switch-button"></i>
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from './Breadcrumb.vue'
import Hamburger from './Hamburger.vue'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  computed: {
    ...mapGetters(['sidebar', 'avatar', 'name']),
    userName() {
      return this.name || 'Admin'
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.navbar {
  height: $navbar-height;
  overflow: hidden;
  position: relative;
  background: $navbar-bg;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;

  .left-menu {
    display: flex;
    align-items: center;
  }

  .hamburger-container {
    line-height: $navbar-height - 5;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    display: flex;
    align-items: center;

    &:focus {
      outline: none;
    }

    .avatar-container {
      .avatar-wrapper {
        display: flex;
        align-items: center;
        cursor: pointer;
        padding: 5px 12px;
        border-radius: 20px;
        transition: all 0.3s ease;
        background-color: transparent;

        &:hover {
          background-color: #f5f7fa;
        }

        .user-avatar {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          object-fit: cover;
          border: 2px solid #409EFF;
          box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
        }

        .user-name {
          margin-left: 8px;
          font-size: 14px;
          color: #303133;
          font-weight: 500;
          max-width: 100px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .el-icon-arrow-down {
          margin-left: 6px;
          font-size: 12px;
          color: #909399;
          transition: transform 0.3s ease;
        }
      }

      // 下拉菜单打开时箭头旋转
      &.el-dropdown .el-icon-arrow-down {
        transform: rotate(180deg);
      }
    }
  }
}
</style>

<style lang="scss">
// 全局样式 - 下拉菜单样式优化
.user-dropdown {
  margin-top: 10px !important;
  padding: 8px 0 !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12) !important;
  border: 1px solid #e4e7ed !important;

  .el-dropdown-item {
    padding: 10px 20px !important;
    font-size: 14px;
    color: #606266;
    transition: all 0.2s ease;

    i {
      margin-right: 8px;
      color: #909399;
    }

    &:hover {
      background-color: #f5f7fa !important;
      color: #409EFF;

      i {
        color: #409EFF;
      }
    }
  }

  .el-dropdown-menu__item--divided {
    border-top: 1px solid #e4e7ed;
    margin-top: 8px;
    padding-top: 16px !important;
  }
}
</style>
