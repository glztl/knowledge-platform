<!-- src/views/profile/Profile.vue -->
<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <span>👤 个人中心</span>
      </template>
      
      <div class="profile-content">
        <div class="avatar-section">
          <el-avatar :size="100" :src="userAvatar">
            {{ userNickname?.charAt(0) || 'U' }}
          </el-avatar>
          <h2 class="nickname">{{ userNickname }}</h2>
          <p class="username">@{{ userStore.userInfo?.username }}</p>
        </div>
        
        <el-divider />
        
        <div class="info-section">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="邮箱">
              {{ userStore.userInfo?.email || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ formatDate(userStore.userInfo?.createdAt) }}
            </el-descriptions-item>
            <el-descriptions-item label="文档总数">
              {{ stats.totalDocuments }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <el-divider />
        
        <div class="action-section">
          <el-button type="danger" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'  
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { listDocuments } from '@/api/document' 

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userAvatar = computed(() => userStore.userInfo?.avatar || '')
const userNickname = computed(() => 
  userStore.userInfo?.nickname || userStore.userInfo?.username || '用户'
)

// 统计数据
const stats = ref({
  totalDocuments: 0
})

// 格式化日期
const formatDate = (date: string | Date | undefined) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString()
}

// 获取统计数据
const fetchStats = async () => {
  try {
    // 获取文档总数（只取第1页1条数据，通过total字段获取总数）
    const res = await listDocuments(1, 1)
    
    // ✅ 关键修复：根据实际返回格式获取总数
    if (res.data && typeof res.data === 'object' && 'total' in res.data) {
      // 格式: { list: [], total: 10 }
      stats.value.totalDocuments = res.data.total
    } else if (Array.isArray(res.data)) {
      // 格式: 直接返回数组 [ {}, {} ]
      stats.value.totalDocuments = res.data.length
    } else {
      // 安全兜底
      stats.value.totalDocuments = 0
    }
    
    console.log('✅ 文档总数:', stats.value.totalDocuments)
    
  } catch (error: any) {
    console.error('❌ 获取统计数据失败:', error)
    ElMessage.warning('获取文档统计失败，使用默认值')
    // 出错时保持为0或使用缓存值
    stats.value.totalDocuments = 0
  }
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

// 初始化
onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.profile-content {
  padding: 30px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
}

.avatar-section .el-avatar {
  background-color: #409EFF;
  color: white;
  font-size: 40px;
  font-weight: bold;
}

.nickname {
  margin: 15px 0 5px;
  font-size: 24px;
  color: #303133;
}

.username {
  color: #909399;
  font-size: 16px;
}

.info-section {
  margin: 20px 0;
}

.action-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>