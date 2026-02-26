<template>
  <div class="document-table-container">
    <el-table
      :data="documents"
      :loading="loading"
      v-loading="loading"
      element-loading-text="加载中..."
      border
      row-key="id"
      style="width: 100%"
    >
      <el-table-column
        prop="title"
        label="标题"
        min-width="200"
      >
        <template #default="scope">
          <div class="document-title-cell">
            <el-icon><Document /></el-icon>
            <span 
              class="title-link" 
              @click="handleView(scope.row)"
            >
              {{ scope.row.title }}
            </span>
            <el-tag 
              v-if="scope.row.isPublic" 
              size="small" 
              type="success" 
              class="public-tag"
            >
              公开
            </el-tag>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="categoryName"
        label="分类"
        width="120"
      >
        <template #default="scope">
          <el-tag 
            v-if="scope.row.categoryName" 
            size="small"
            effect="plain"
          >
            {{ scope.row.categoryName }}
          </el-tag>
          <span v-else class="empty-text">未分类</span>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="tagNames"
        label="标签"
        width="150"
      >
        <template #default="scope">
          <div class="tags-container">
            <el-tag 
              v-for="(tag, index) in (scope.row.tagNames || []).slice(0, 2)" 
              :key="index" 
              size="small" 
              type="info" 
              effect="plain"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
            <el-tag 
              v-if="(scope.row.tagNames || []).length > 2" 
              size="small" 
              type="warning"
              class="tag-more"
            >
              +{{ (scope.row.tagNames || []).length - 2 }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="viewCount"
        label="浏览"
        width="80"
        align="center"
      >
        <template #default="scope">
          <span class="view-count">{{ scope.row.viewCount || 0 }}</span>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="updatedAt"
        label="更新时间"
        width="160"
      >
        <template #default="scope">
          <span class="update-time">{{ formatTime(scope.row.updatedAt) }}</span>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button 
            size="small" 
            type="primary" 
            link 
            @click.stop="handleEdit(scope.row)"
          >
            编辑
          </el-button>
          <el-button 
            size="small" 
            type="success" 
            link 
            @click.stop="handleShare(scope.row)"
          >
            分享
          </el-button>
          <el-button 
            size="small" 
            type="danger" 
            link 
            @click.stop="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="empty-container" v-if="!loading && documents.length === 0">
      <el-empty description="暂无文档">
        <el-button type="primary" @click="$emit('refresh')">
          刷新列表
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
// ✅ 修复：添加 computed 导入
import { ref, computed } from 'vue'  // 确保包含 computed
import { useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'

const props = defineProps<{
  documents: any[]
  loading: boolean
}>()

const emit = defineEmits(['refresh', 'edit', 'delete', 'view', 'share'])

// 格式化时间
const formatTime = (time: string | Date) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

// 查看文档
const handleView = (row: any) => {
  emit('view', row)
}

// 编辑文档
const handleEdit = (row: any) => {
  emit('edit', row)
}

// 分享文档
const handleShare = (row: any) => {
  emit('share', row)
}

// 删除文档
const handleDelete = (row: any) => {
  emit('delete', row)
}

const documents = computed(() => props.documents || [])
</script>

<style scoped>
.document-table-container {
  position: relative;
}

.document-title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.document-title-cell i {
  color: #409EFF;
  font-size: 18px;
}

.title-link {
  color: #409EFF;
  cursor: pointer;
  font-weight: 500;
}

.title-link:hover {
  text-decoration: underline;
}

.public-tag {
  margin-left: 8px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag-item {
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tag-more {
  cursor: default;
}

.empty-text {
  color: #909399;
  font-size: 13px;
}

.view-count {
  color: #606266;
  font-size: 14px;
}

.update-time {
  color: #909399;
  font-size: 13px;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}
</style>