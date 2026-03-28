import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// 创建 axios 实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 如果存在 token，则添加到请求头
    if (store.getters.token) {
      config.headers['Authorization'] = 'Bearer ' + getToken()
    }
    console.log('API Request:', config.method?.toUpperCase(), config.url, config.params || config.data)
    return config
  },
  error => {
    console.log('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    console.log('API Response:', response.config.url, 'status:', response.status, 'data:', res)

    // 如果自定义代码不是 200，则判断为错误
    if (res.code !== 200) {
      Message({
        message: res.msg || res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 50008: 非法令牌; 50012: 其他客户端登录; 50014: 令牌过期;
      if (res.code === 401) {
        // 重新登录
        MessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      return Promise.reject(new Error(res.msg || res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('Response error:', error.message, error.response)
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
