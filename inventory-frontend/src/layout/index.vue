<template>
  <div class="layout-container">
    <div class="sidebar-container" :class="{ 'is-collapse': isCollapse }">
      <div class="logo-container">
        <h1 v-if="!isCollapse">库存管理系统</h1>
        <span v-else>库存</span>
      </div>
      <el-scrollbar wrap-class="scrollbar-wrapper">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :background-color="variables.sidebarBg"
          :text-color="variables.sidebarText"
          :active-text-color="variables.sidebarActiveText"
          :unique-opened="true"
          :collapse-transition="false"
          mode="vertical"
          router
        >
          <sidebar-item
            v-for="route in routes"
            :key="route.path"
            :item="route"
            :base-path="route.path"
          />
        </el-menu>
      </el-scrollbar>
    </div>
    <div class="main-container" :class="{ 'sidebar-hide': isCollapse }">
      <navbar />
      <app-main />
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Navbar from './components/Navbar.vue'
import AppMain from './components/AppMain.vue'
import SidebarItem from './components/SidebarItem.vue'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'Layout',
  components: {
    Navbar,
    AppMain,
    SidebarItem
  },
  computed: {
    ...mapGetters(['sidebar', 'permission_routes']),
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    },
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    routes() {
      return this.permission_routes
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.layout-container {
  display: flex;
  height: 100vh;
}

.sidebar-container {
  width: $sidebar-width;
  height: 100%;
  background-color: $sidebar-bg;
  transition: width 0.3s;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1001;
  overflow: hidden;

  &.is-collapse {
    width: $sidebar-collapsed-width;

    .logo-container h1 {
      display: none;
    }

    .logo-container span {
      display: block;
    }
  }

  .logo-container {
    height: $navbar-height;
    line-height: $navbar-height;
    text-align: center;
    background-color: #2b3649;

    h1 {
      margin: 0;
      color: #fff;
      font-size: 16px;
      font-weight: 600;
    }

    span {
      display: none;
      color: #fff;
      font-size: 14px;
    }
  }

  ::v-deep .el-menu {
    border: none;
  }

  ::v-deep .el-scrollbar {
    height: calc(100% - #{$navbar-height});
  }
}

.main-container {
  margin-left: $sidebar-width;
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s;
  min-height: 100vh;

  &.sidebar-hide {
    margin-left: $sidebar-collapsed-width;
  }
}
</style>
