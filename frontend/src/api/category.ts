// src/api/category.ts
import request from './request'

export interface CategoryItem {
  id: number
  name: string
  parentId: number
  sort: number
  userId: number
  createdAt: string
  updatedAt: string
  children?: CategoryItem[]
}

export interface CategoryCreateDTO {
  name: string
  parentId?: number
}

export interface CategoryUpdateDTO {
  id: number
  name: string
  parentId?: number
}

// 获取分类树
export function getCategoryTree() {
  return request.get<CategoryItem[]>('/category/tree')
}

// 创建分类
export function createCategory(data: CategoryCreateDTO) {
  return request.post<CategoryItem>('/category', data)
}

// 更新分类
export function updateCategory(id: number, data: CategoryUpdateDTO) {
  return request.put<CategoryItem>(`/category/${id}`, data)
}

// 删除分类
export function deleteCategory(id: number) {
  return request.delete<void>(`/category/${id}`)
}