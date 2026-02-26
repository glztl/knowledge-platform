// src/api/request.ts
import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// 响应数据接口
export interface ResponseData<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: '/api', // 会被 vite proxy 代理到后端
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器（关键：返回 response.data）
service.interceptors.response.use(
  (response: AxiosResponse<ResponseData>) => {
    const res = response.data
    
    // 如果返回的状态码不是 200，则判断为错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: 未授权
      if (res.code === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res // ✅ 直接返回 data，后续调用无需 .data
  },
  (error) => {
    console.error('Response error:', error)
    
    // 处理 401 未授权
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(error)
    }
    
    // 处理其他错误
    const errorMsg = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(errorMsg)
    return Promise.reject(error)
  }
)

// 导出 service 实例（供直接使用）
export default service

// 封装快捷方法（推荐使用）
export const request = {
  get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<ResponseData<T>> => {
    return service.get(url, config)
  },
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ResponseData<T>> => {
    return service.post(url, data, config)
  },
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ResponseData<T>> => {
    return service.put(url, data, config)
  },
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<ResponseData<T>> => {
    return service.delete(url, config)
  }
}