// src/store/modules/user.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/user'
import type { LoginParams, UserInfo } from '@/api/user'

export interface UserInfo {
  id: number
  username: string
  email: string
  nickname: string
  avatar: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  // 登录
  const login = async (account: string, password: string) => {
    const res = await loginApi({ account, password })
    token.value = res.data.token
    userInfo.value = res.data.user
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data.user))
    return res
  }

  // 获取用户信息
  const getUserInfo = async () => {
    const res = await getUserInfoApi()
    userInfo.value = res.data
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 初始化：从 localStorage 恢复状态
  if (token.value) {
    try {
      const savedUserInfo = localStorage.getItem('userInfo')
      if (savedUserInfo) {
        userInfo.value = JSON.parse(savedUserInfo)
      }
    } catch (e) {
      console.error('恢复用户信息失败:', e)
    }
  }

  return {
    token,
    userInfo,
    login,
    getUserInfo,
    logout
  }
})