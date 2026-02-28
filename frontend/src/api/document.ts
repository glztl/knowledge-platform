// src/api/document.ts
import request from './request'

export interface DocumentListItem {
  id: number
  title: string
  categoryName?: string
  tagNames?: string[]
  viewCount: number
  isPublic: number
  updatedAt: string
}

export interface DocumentDetail {
  id: number
  title: string
  content: string
  categoryName?: string
  categoryId?: number
  tagNames?: string[]
  isPublic: number
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface DocumentCreateDTO {
  title: string
  content: string
  categoryId?: number
  tagIds?: number[]
  isPublic?: number
}

export interface DocumentUpdateDTO extends DocumentCreateDTO {
  id: number
}

// ========== 分享相关接口 ==========

export interface DocumentShareDTO {
  id: number
  requirePassword?: boolean
  password?: string
  expireHours?: number
  publicAccess?: boolean
}

export interface DocumentShareVO {
  title: string
  shareUrl: string
  shareToken: string
  requirePassword: boolean
  expireAt?: string
  permanent: boolean
}

export interface DocumentAccessDTO {
  token: string
  password?: string
}

// 获取文档列表
export function listDocuments(page: number = 1, size: number = 10) {
  return request.get<any>(`/document/list?page=${page}&size=${size}`)
}

// 获取文档详情
export function getDocument(id: number | string) {
  return request.get<DocumentDetail>(`/document/${id}`)
}

// 创建文档
export function createDocument(data: DocumentCreateDTO) {
  return request.post<{ id: number }>(`/document/create`, data)
}

// 更新文档
export function updateDocument(id: number, data: DocumentUpdateDTO) {
  return request.put<DocumentDetail>(`/document/update`, { ...data, id })
}


// 生成分享链接
export function generateShareLink(data: DocumentShareDTO) {
  return request.post<DocumentShareVO>('/document/share/generate', data)
}

// 取消分享
export function cancelShare(documentId: number) {
  return request.delete<void>(`/document/share/cancel/${documentId}`)
}

// 更新分享设置
export function updateShareSettings(data: DocumentShareDTO) {
  return request.put<DocumentShareVO>('/document/share/update', data)
}

// 通过令牌访问分享文档
export function accessSharedDocument(data: DocumentAccessDTO) {
  return request.post<DocumentDetail>('/document/share/access', data)
}