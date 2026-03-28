import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// Element UI
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

// ECharts
import * as echarts from 'echarts'

// 全局样式
import '@/assets/styles/index.scss'

// 权限指令
import permission from '@/directives/permission'
import { role } from '@/directives/permission'

// 权限检查方法
import { checkPermi, checkRole } from '@/utils/permission'

// 路由守卫
import './permission'

Vue.use(ElementUI, { size: 'medium' })

// 注册权限指令
Vue.directive('permission', permission)
Vue.directive('role', role)

Vue.config.productionTip = false

// 挂载 echarts 到 Vue 原型
Vue.prototype.$echarts = echarts

// 挂载权限检查方法到 Vue 原型
Vue.prototype.$checkPermi = checkPermi
Vue.prototype.$checkRole = checkRole

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
