import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

/**
 * Note: 子菜单只在路由子菜单 children.length >= 1 时出现
 * 
 * hidden: true                   如果设置为 true，项目将不会显示在侧边栏中（默认为 false）
 * alwaysShow: true               如果设置为 true，将始终显示根菜单
 *                                如果不设置，则当子菜单多于一个时显示根菜单
 * redirect: noRedirect           如果设置为 noRedirect，则面包屑中不会重定向
 * name:'router-name'             名称由 <keep-alive> 使用（必须设置！！！）
 * meta : {
    title: 'title'               名称显示在侧边栏和面包屑中（推荐设置）
    icon: 'svg-name'/'el-icon-x' 图标显示在侧边栏中
    breadcrumb: false            如果设置为 false，项目将不会显示在面包屑中（默认为 true）
    activeMenu: '/example/list'  如果设置路径，侧边栏将高亮您设置的路径
  }
 */

// 静态路由
export const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    hidden: true,
    meta: { title: '登录' }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    hidden: true,
    meta: { title: '404' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'el-icon-s-home' }
      }
    ]
  }
]

// 动态路由（根据权限动态加载）
export const asyncRoutes = [
  {
    path: '/system',
    name: 'System',
    component: () => import('@/layout/index.vue'),
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'el-icon-setting' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'el-icon-user' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'el-icon-s-custom' }
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'el-icon-menu' }
      }
    ]
  },
  {
    path: '/base',
    name: 'Base',
    component: () => import('@/layout/index.vue'),
    redirect: '/base/warehouse',
    meta: { title: '基础资料', icon: 'el-icon-document' },
    children: [
      {
        path: 'warehouse',
        name: 'Warehouse',
        component: () => import('@/views/base/warehouse/index.vue'),
        meta: { title: '仓库管理', icon: 'el-icon-office-building' }
      },
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('@/views/base/supplier/index.vue'),
        meta: { title: '供应商管理', icon: 'el-icon-truck' }
      },
      {
        path: 'customer',
        name: 'Customer',
        component: () => import('@/views/base/customer/index.vue'),
        meta: { title: '客户管理', icon: 'el-icon-user-solid' }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/base/product/index.vue'),
        meta: { title: '产品管理', icon: 'el-icon-goods' }
      }
    ]
  },
  {
    path: '/business',
    name: 'Business',
    component: () => import('@/layout/index.vue'),
    redirect: '/business/purchase',
    meta: { title: '业务管理', icon: 'el-icon-s-order' },
    children: [
      {
        path: 'purchase',
        name: 'Purchase',
        component: () => import('@/views/business/purchase/index.vue'),
        meta: { title: '采购订单', icon: 'el-icon-shopping-cart-2' }
      },
      {
        path: 'sale',
        name: 'Sale',
        component: () => import('@/views/business/sale/index.vue'),
        meta: { title: '销售订单', icon: 'el-icon-sold-out' }
      },
      {
        path: 'inbound',
        name: 'Inbound',
        component: () => import('@/views/business/inbound/index.vue'),
        meta: { title: '入库管理', icon: 'el-icon-download' }
      },
      {
        path: 'outbound',
        name: 'Outbound',
        component: () => import('@/views/business/outbound/index.vue'),
        meta: { title: '出库管理', icon: 'el-icon-upload2' }
      }
    ]
  },
  {
    path: '/stock',
    name: 'Stock',
    component: () => import('@/layout/index.vue'),
    redirect: '/stock/inventory',
    meta: { title: '库存管理', icon: 'el-icon-s-grid' },
    children: [
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('@/views/stock/inventory/index.vue'),
        meta: { title: '库存查询', icon: 'el-icon-search' }
      },
      {
        path: 'check',
        name: 'StockCheck',
        component: () => import('@/views/stock/check/index.vue'),
        meta: { title: '库存盘点', icon: 'el-icon-document-checked' }
      }
    ]
  },
  {
    path: '/monitor',
    name: 'Monitor',
    component: () => import('@/layout/index.vue'),
    redirect: '/monitor/online',
    meta: { title: '系统监控', icon: 'el-icon-monitor' },
    children: [
      {
        path: 'online',
        name: 'Online',
        component: () => import('@/views/monitor/online/index.vue'),
        meta: { title: '在线用户', icon: 'el-icon-connection' }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/monitor/log/index.vue'),
        meta: { title: '操作日志', icon: 'el-icon-document-copy' }
      }
    ]
  },
  // 404 页面必须放在最后
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new VueRouter({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// 重置路由
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
