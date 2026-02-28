<!-- src/views/dashboard/Dashboard.vue -->
<template>
  <div class="dashboard">

    <!-- Header -->
    <div class="header">
      <div class="header-text">
        <h1>仪表盘</h1>
        <p>欢迎回来，{{ userNickname }}</p>
      </div>
    </div>

    <!-- Stats -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue"><el-icon><Document /></el-icon></div>
        <div>
          <div class="stat-label">文档</div>
          <div class="stat-value">{{ stats.totalDocuments }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon green"><el-icon><Folder /></el-icon></div>
        <div>
          <div class="stat-label">分类</div>
          <div class="stat-value">{{ stats.totalCategories }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orange"><el-icon><PriceTag /></el-icon></div>
        <div>
          <div class="stat-label">标签</div>
          <div class="stat-value">{{ stats.totalTags }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon gray"><el-icon><View /></el-icon></div>
        <div>
          <div class="stat-label">浏览</div>
          <div class="stat-value">{{ stats.totalViews }}</div>
        </div>
      </div>
    </div>

    <!-- Content -->
    <div class="content">
      <!-- Recent Documents -->
      <div class="card recent-docs">
        <div class="card-header">
          <div>最近文档</div>
          <button class="apple-btn" @click="$router.push('/document/create')">新建</button>
        </div>

        <div v-if="loading" class="empty">加载中...</div>
        <div v-else-if="recentDocuments.length === 0" class="empty">暂无文档</div>
        <div v-else>
          <div v-for="doc in recentDocuments" :key="doc.id" class="doc-item" @click="$router.push(`/document/${doc.id}`)">
            <div class="doc-title">{{ doc.title }}</div>
            <div class="doc-meta">
              <span v-if="doc.categoryName">{{ doc.categoryName }}</span>
              <span>{{ formatTime(doc.updatedAt) }}</span>
              <span>{{ doc.viewCount || 0 }} 浏览</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="card quick-actions">
        <div class="card-header">快捷操作</div>
        <div class="actions">
          <button class="action-btn" @click="$router.push('/document/create')">新建文档</button>
          <button class="action-btn" @click="$router.push('/category')">管理分类</button>
          <button class="action-btn" @click="$router.push('/tag')">管理标签</button>
          <button class="action-btn" @click="$router.push('/profile')">个人中心</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue"
import { useRouter } from "vue-router"
import { Document, Folder, PriceTag, View } from "@element-plus/icons-vue"
import { useUserStore } from "@/store/modules/user"
import { listDocuments } from "@/api/document"
import { getCategoryTree } from "@/api/category"
import { getTagList } from "@/api/tag"
import { ElMessage } from "element-plus"

const router = useRouter()
const userStore = useUserStore()

const userNickname = computed(() =>
  userStore.userInfo?.nickname || userStore.userInfo?.username || "用户"
)

const loading = ref(false)
const recentDocuments = ref<any[]>([])
const stats = ref({
  totalDocuments: 0,
  totalCategories: 0,
  totalTags: 0,
  totalViews: 0
})

const formatTime = (time: string | Date) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const hours = Math.floor(diff / 3600000)
  if (hours < 24) return `${hours}小时前`
  if (hours < 48) return "昨天"
  return date.toLocaleDateString()
}

const countCategories = (list: any[]): number => {
  let count = 0
  list.forEach(item => {
    count++
    if (item.children) count += countCategories(item.children)
  })
  return count
}

type DocumentItem = {
  id: number | string
  title: string
  updatedAt: string | Date
  viewCount?: number
  categoryName?: string
}

const fetchStats = async () => {
  loading.value = true
  try {
    const docRes = await listDocuments(1, 5)
    const docs = Array.isArray(docRes.data) ? docRes.data : docRes.data?.list || []
    recentDocuments.value = docs
    stats.value.totalDocuments = docs.length
    stats.value.totalViews = docs.reduce((sum: number, d: DocumentItem) => sum + (Number(d.viewCount) || 0), 0)

    const categoryRes = await getCategoryTree()
    stats.value.totalCategories = countCategories(categoryRes.data || [])

    const tagRes = await getTagList()
    stats.value.totalTags = tagRes.data?.length || 0
  } catch {
    ElMessage.error("加载失败")
  } finally {
    loading.value = false
  }
}

onMounted(fetchStats)
</script>

<style scoped>
.dashboard {
  padding: 40px;
  min-height: 100vh;
  background:
    radial-gradient(circle at 20% 10%, #e8f2ff 0%, transparent 40%),
    radial-gradient(circle at 80% 20%, #fff1f1 0%, transparent 40%),
    radial-gradient(circle at 40% 80%, #f0fff4 0%, transparent 40%),
    #f5f5f7;
}

/* Header */
.header h1 {
  font-size: 34px;
  font-weight: 700;
  background: linear-gradient(90deg,#0071e3,#5e5ce6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.header p { color: #6e6e73; font-size: 15px; }

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit,minmax(220px,1fr));
  gap: 22px;
  margin-bottom: 30px;
}
.stat-card {
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 22px;
  display: flex;
  gap: 16px;
  align-items: center;
  transition: all .25s ease;
  border: 1px solid rgba(255,255,255,0.4);
  box-shadow: 0 8px 30px rgba(0,0,0,0.06);
}
.stat-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 15px 40px rgba(0,0,0,0.12);
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}
.blue { background: linear-gradient(135deg,#0071e3,#5ac8fa); color: white; box-shadow: 0 6px 20px rgba(0,113,227,0.4);}
.green { background: linear-gradient(135deg,#34c759,#30d158); color: white; box-shadow: 0 6px 20px rgba(52,199,89,0.4);}
.orange { background: linear-gradient(135deg,#ff9f0a,#ffcc00); color: white; box-shadow: 0 6px 20px rgba(255,159,10,0.4);}
.gray { background: linear-gradient(135deg,#8e8e93,#c7c7cc); color: white; box-shadow: 0 6px 20px rgba(142,142,147,0.4);}
.stat-value { font-size: 26px; font-weight: 700; margin-top: 2px;}
.stat-label { color: #6e6e73; font-size: 13px; }

/* Content: 左右布局 */
.content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

/* 卡片 */
.card {
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(20px);
  border-radius: 22px;
  padding: 22px;
  border: 1px solid rgba(255,255,255,0.4);
  box-shadow: 0 10px 35px rgba(0,0,0,0.06);
}

/* 按钮 */
.apple-btn {
  background: linear-gradient(135deg,#0071e3,#5e5ce6);
  border: none;
  color: white;
  padding: 7px 16px;
  border-radius: 999px;
  cursor: pointer;
  font-size: 14px;
  transition: .2s;
}
.apple-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(0,113,227,0.4);
}

/* 文档 */
.doc-item {
  padding: 14px;
  border-radius: 14px;
  transition: .2s;
}
.doc-item:hover {
  background: rgba(0,113,227,0.06);
  transform: translateX(4px);
}

/* 快捷按钮 - 竖向排列 */
.actions {
  display: flex;
  flex-direction: column; /* 保证竖向 */
  gap: 12px; /* 按钮间距 */
}

/* 快捷按钮样式 */
.action-btn {
  border: none;
  background: linear-gradient(135deg,#f2f2f7,#ffffff);
  padding: 13px;
  border-radius: 14px;
  cursor: pointer;
  font-weight: 500;
  transition: .2s;
}
.action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
}

/* Empty */
.empty {
  padding: 40px;
  color: #8e8e93;
}
</style>