// src/api/user.ts
import { request } from './request' // ✅ 导入封装的 request 对象

export interface LoginParams {
  account: string
  password: string
}

export interface RegisterParams {
  username: string
  email: string
  password: string
  nickname?: string
}

export interface UserInfo {
  id: number
  username: string
  email: string
  nickname: string
  avatar: string
}

export interface LoginResult {
  user: UserInfo
  token: string
  expire: number
}

// ✅ 正确：使用 request.post（request 是封装的对象，有 post 方法）
export function login(data: LoginParams) {
  return request.post<LoginResult>('/user/login', data)
}

export function register(data: RegisterParams) {
  return request.post<UserInfo>('/user/register', data)
}

export function getUserInfo() {
  return request.get<UserInfo>('/user/profile')
}