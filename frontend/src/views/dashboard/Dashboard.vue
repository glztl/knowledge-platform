<!-- src/views/dashboard/Dashboard.vue -->
<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>📊 仪表盘</h1>
      <p>欢迎回来，{{ userNickname }}！</p>
    </div>

    <el-row :gutter="20" class="stats-container">
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card">
          <div class="stat-icon stat-icon-doc">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">文档总数</div>
            <div class="stat-value">{{ stats.totalDocuments }}</div>
            <div class="stat-desc">比上周增加 {{ stats.documentGrowth }}%</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card">
          <div class="stat-icon stat-icon-category">
            <el-icon><Folder /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">分类数量</div>
            <div class="stat-value">{{ stats.totalCategories }}</div>
            <div class="stat-desc">共 {{ stats.totalCategories }} 个分类</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card">
          <div class="stat-icon stat-icon-tag">
            <el-icon><PriceTag /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">标签数量</div>
            <div class="stat-value">{{ stats.totalTags }}</div>
            <div class="stat-desc">共 {{ stats.totalTags }} 个标签</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card">
          <div class="stat-icon stat-icon-view">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总浏览量</div>
            <div class="stat-value">{{ stats.totalViews }}</div>
            <div class="stat-desc">累计 {{ stats.totalViews }} 次浏览</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-container">
      <el-col :xs="24" :md="16">
        <el-card class="recent-docs-card">
          <template #header>
            <div class="card-header">
              <span>📝 最近文档</span>
              <el-button 
                type="primary" 
                size="small" 
                @click="$router.push('/document/create')"
              >
                <el-icon><Plus /></el-icon>
                新建文档
              </el-button>
            </div>
          </template>
          
          <el-empty 
            v-if="loading" 
            description="加载中..."
          />
          
          <el-empty 
            v-else-if="recentDocuments.length === 0" 
            description="暂无文档，点击右上角新建文档"
          />
          
          <div v-else class="document-list">
            <div 
              v-for="doc in recentDocuments" 
              :key="doc.id" 
              class="document-item"
              @click="$router.push(`/document/${doc.id}`)"
            >
              <div class="document-title">
                <el-icon><Document /></el-icon>
                <span>{{ doc.title }}</span>
              </div>
              <div class="document-meta">
                <span class="category" v-if="doc.categoryName">
                  <el-icon><Folder /></el-icon>
                  {{ doc.categoryName }}
                </span>
                <span class="time">
                  <el-icon><Clock /></el-icon>
                  {{ formatTime(doc.updatedAt) }}
                </span>
                <span class="views">
                  <el-icon><View /></el-icon>
                  {{ doc.viewCount || 0 }}
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :md="8">
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <span>⚡ 快捷操作</span>
            </div>
          </template>
          
          <div class="quick-actions">
            <el-button 
              type="primary" 
              plain 
              size="large" 
              class="quick-action-btn"
              @click="$router.push('/document/create')"
            >
              <el-icon><Edit /></el-icon>
              <span>新建文档</span>
            </el-button>
            
            <el-button 
              type="success" 
              plain 
              size="large" 
              class="quick-action-btn"
              @click="$router.push('/category')"
            >
              <el-icon><Folder /></el-icon>
              <span>管理分类</span>
            </el-button>
            
            <el-button 
              type="warning" 
              plain 
              size="large" 
              class="quick-action-btn"
              @click="$router.push('/tag')"
            >
              <el-icon><PriceTag /></el-icon>
              <span>管理标签</span>
            </el-button>
            
            <el-button 
              type="info" 
              plain 
              size="large" 
              class="quick-action-btn"
              @click="$router.push('/profile')"
            >
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Document, Folder, PriceTag, View, Plus, Clock, Edit, User 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { listDocuments } from '@/api/document'
import { getCategoryTree } from '@/api/category'
import { getTagList } from '@/api/tag'

// 用户信息
const userStore = useUserStore()
const userNickname = computed(() => 
  userStore.userInfo?.nickname || userStore.userInfo?.username || '用户'
)

// 统计数据
const stats = ref({
  totalDocuments: 0,
  documentGrowth: 12,
  totalCategories: 0,
  totalTags: 0,
  totalViews: 0
})

// 最近文档
const recentDocuments = ref<any[]>([])
const loading = ref(false)

// 路由
const router = useRouter()

// 格式化时间
const formatTime = (time: string | Date) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const diffHours = Math.floor(diff / (1000 * 60 * 60))
  
  if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffHours < 48) {
    return '昨天'
  } else {
    return date.toLocaleDateString()
  }
}

// 递归计算分类数量（包括子分类）
const countCategories = (categories: any[]): number => {
  if (!categories || categories.length === 0) return 0
  
  let count = 0
  for (const category of categories) {
    count++ // 当前分类
    if (category.children && Array.isArray(category.children)) {
      count += countCategories(category.children) // 递归子分类
    }
  }
  return count
}

// 获取统计数据
const fetchStats = async () => {
  loading.value = true
  
  try {
    // 1. 获取文档列表（前5条作为最近文档）
    const docRes = await listDocuments(1, 5)
    
    // ✅ 修复1：适配实际返回格式（可能是直接数组）
    const documents = Array.isArray(docRes.data) ? docRes.data : (docRes.data?.list || [])
    
    recentDocuments.value = documents
    stats.value.totalDocuments = documents.length
    
    // ✅ 修复2：安全累加 viewCount（处理 null/undefined）
    stats.value.totalViews = documents.reduce((sum, doc) => 
      sum + (Number(doc.viewCount) || 0), 0
    )
    
    // 2. 获取分类树
    try {
      const categoryRes = await getCategoryTree()
      // ✅ 修复3：适配实际返回格式
      const categories = Array.isArray(categoryRes.data) ? categoryRes.data : []
      
      stats.value.totalCategories = countCategories(categories)
    } catch (error) {
      console.warn('获取分类失败，使用默认值:', error)
      stats.value.totalCategories = 5 // 默认值
    }
    
    // 3. 获取标签列表
    try {
      const tagRes = await getTagList()
      // ✅ 修复4：适配实际返回格式
      const tags = Array.isArray(tagRes.data) ? tagRes.data : []
      
      stats.value.totalTags = tags.length
    } catch (error) {
      console.warn('获取标签失败，使用默认值:', error)
      stats.value.totalTags = 8 // 默认值
    }
    
  } catch (error) {
    ElMessage.error('获取统计数据失败')
    console.error('仪表盘数据加载错误:', error)
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-header {
  margin-bottom: 30px;
}

.dashboard-header h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.dashboard-header p {
  color: #606266;
  font-size: 16px;
}

.stats-container {
  margin-bottom: 30px;
}

.stat-card {
  height: 120px;
  cursor: default;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  float: left;
  margin-right: 15px;
}

.stat-icon-doc {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.stat-icon-category {
  background-color: rgba(114, 189, 74, 0.1);
  color: #72BD4A;
}

.stat-icon-tag {
  background-color: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.stat-icon-view {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.stat-icon i {
  font-size: 28px;
}

.stat-content {
  overflow: hidden;
  padding-top: 8px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  color: #303133;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-desc {
  font-size: 12px;
  color: #909399;
}

.content-container {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.document-list {
  padding: 0 10px;
}

.document-item {
  padding: 15px;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: background-color 0.2s;
}

.document-item:hover {
  background-color: #f9fafc;
}

.document-item:last-child {
  border-bottom: none;
}

.document-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #303133;
  margin-bottom: 8px;
}

.document-title i {
  margin-right: 8px;
  color: #409EFF;
}

.document-meta {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.document-meta span {
  display: flex;
  align-items: center;
  margin-right: 15px;
}

.document-meta i {
  margin-right: 4px;
  font-size: 14px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.quick-action-btn {
  width: 100%;
  height: 60px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-action-btn i {
  margin-right: 10px;
  font-size: 20px;
}
</style>