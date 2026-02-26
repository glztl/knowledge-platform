// src/api/tag.ts
import request from './request'

export interface TagItem {
  id: number
  name: string
  userId: number
  createdAt: string
}

export interface TagCreateDTO {
  name: string
}

// 获取标签列表
export function getTagList() {
  return request.get<TagItem[]>('/tag/list')
}

// 创建标签
export function createTag(data: TagCreateDTO) {
  return request.post<TagItem>('/tag', data)
}

// 删除标签
export function deleteTag(id: number) {
  return request.delete<void>(`/tag/${id}`)
}