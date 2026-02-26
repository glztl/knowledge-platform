<!-- src/views/document/ShareDocument.vue -->
<template>
  <div class="share-document-container">
    <!-- 顶部提示栏 -->
    <div class="share-header">
      <div class="header-content">
        <el-icon><Link /></el-icon>
        <span>分享的文档</span>
        <el-button 
          type="primary" 
          size="small"
          @click="handleLogin"
          v-if="!isLoggedIn"
        >
          <el-icon><User /></el-icon>
          登录后可评论/收藏
        </el-button>
      </div>
    </div>
    
    <el-card class="document-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <h1 class="document-title">{{ document.title }}</h1>
          <div class="meta-info">
            <span class="meta-item">
              <el-icon><View /></el-icon>
              {{ document.viewCount || 0 }} 浏览
            </span>
            <span class="meta-item">
              <el-icon><Timer /></el-icon>
              {{ formatDate(document.updatedAt) }}
            </span>
            <span class="meta-item author">
              <el-icon><User /></el-icon>
              {{ document.author?.nickname || '匿名用户' }}
            </span>
          </div>
        </div>
      </template>
      
      <el-empty 
        v-if="!document.content" 
        description="文档内容为空"
      />
      
      <div 
        v-else 
        class="document-content"
      >
        <div 
          class="markdown-content" 
          v-html="renderedContent"
        ></div>
      </div>
      
      <!-- 标签区域 -->
      <div v-if="document.tagNames?.length" class="tag-section">
        <el-tag 
          v-for="(tag, index) in document.tagNames" 
          :key="index" 
          type="info" 
          size="small"
          class="tag-item"
        >
          {{ tag }}
        </el-tag>
      </div>
      
      <!-- 操作区域 -->
      <div class="action-section">
        <el-button 
          type="primary" 
          @click="handleCopyLink"
        >
          <el-icon><Link /></el-icon>
          复制链接
        </el-button>
        <el-button 
          type="success" 
          @click="handleDownload"
        >
          <el-icon><Download /></el-icon>
          下载为 Markdown
        </el-button>
      </div>
    </el-card>
    
    <!-- 密码验证对话框（首次访问时） -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="请输入访问密码"
      width="400px"
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form :model="passwordForm">
        <el-form-item label="密码">
          <el-input 
            v-model="passwordForm.password" 
            type="password"
            placeholder="请输入分享密码"
            @keyup.enter="verifyPassword"
            autofocus
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button 
          type="primary" 
          @click="verifyPassword"
          :loading="verifying"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 复制成功提示 -->
    <el-dialog
      v-model="copySuccessVisible"
      title="链接已复制"
      width="300px"
      :show-close="false"
    >
      <div class="copy-success-content">
        <el-icon class="success-icon" color="#67c23a" size="40"><SuccessFilled /></el-icon>
        <p>分享链接已复制到剪贴板</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="copySuccessVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Link, User, View, Timer, Download, SuccessFilled 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { accessSharedDocument } from '@/api/document'
import DOMPurify from 'dompurify'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 状态
const loading = ref(false)
const passwordDialogVisible = ref(false)
const copySuccessVisible = ref(false)
const verifying = ref(false)

// 数据
const document = ref<any>({})
const passwordForm = ref({
  password: ''
})
const shareToken = ref<string>(route.params.token as string)

// 计算属性
const isLoggedIn = computed(() => !!userStore.token)

// 格式化日期
const formatDate = (date: string | Date | undefined) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 渲染 Markdown 内容
const renderedContent = computed(() => {
  if (!document.value.content) return ''
  
  let html = document.value.content
    .replace(/^### (.*$)/gim, '<h3>$1</h3>')
    .replace(/^## (.*$)/gim, '<h2>$1</h2>')
    .replace(/^# (.*$)/gim, '<h1>$1</h1>')
    .replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/gim, '<em>$1</em>')
    .replace(/`([^`]+)`/gim, '<code>$1</code>')
    .replace(/^- (.*$)/gim, '<ul><li>$1</li></ul>')
    .replace(/^(?![#\*-]).+$/gim, '<p>$&</p>')
    .replace(/<ul><li>/g, '<ul class="markdown-list"><li>')
    .replace(/<\/li><\/ul>/g, '</li></ul>')
  
  return DOMPurify.sanitize(html)
});

// 验证密码并获取文档
const verifyPassword = async () => {
  if (!passwordForm.value.password.trim()) {
    ElMessage.warning('请输入密码')
    return
  }
  
  verifying.value = true
  try {
    const res = await accessSharedDocument({
      token: shareToken.value,
      password: passwordForm.value.password
    })
    document.value = res.data
    passwordDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '密码错误或链接已失效')
    passwordForm.value.password = ''
  } finally {
    verifying.value = false
  }
}

// 获取分享文档（无密码情况）
const fetchSharedDocument = async () => {
  loading.value = true
  try {
    // 尝试无密码访问
    const res = await accessSharedDocument({
      token: shareToken.value
    })
    document.value = res.data
  } catch (error: any) {
    // 如果需要密码，显示密码对话框
    if (error.response?.data?.code === 401 && 
        error.response?.data?.message?.includes('密码')) {
      passwordDialogVisible.value = true
    } else {
      ElMessage.error(error.response?.data?.message || '获取文档失败')
      console.error('获取文档失败:', error)
      router.push('/document')
    }
  } finally {
    loading.value = false
  }
}

// 复制分享链接
const handleCopyLink = async () => {
  try {
    const url = `${window.location.origin}/share/${shareToken.value}`
    await navigator.clipboard.writeText(url)
    copySuccessVisible.value = true
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 下载为 Markdown
const handleDownload = () => {
  const content = document.value.content || ''
  const title = document.value.title || '未命名文档'
  const blob = new Blob([content], { type: 'text/markdown;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${title}.md`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  ElMessage.success('下载成功')
}

// 登录
const handleLogin = () => {
  router.push('/login')
}

// 初始化
onMounted(() => {
  fetchSharedDocument()
})
</script>

<style scoped>
.share-document-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.share-header {
  background-color: #e6f7ff;
  border-radius: 8px;
  padding: 12px 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.share-header .header-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.share-header i {
  color: #409EFF;
  font-size: 20px;
}

.share-header span {
  font-weight: bold;
  color: #1890ff;
}

.document-card {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.document-title {
  font-size: 28px;
  color: #303133;
  margin: 0;
  line-height: 1.4;
}

.meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  color: #909399;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-item.author {
  color: #409EFF;
}

.document-content {
  padding: 20px 0;
  line-height: 1.8;
  color: #303133;
}

.markdown-content {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}

.markdown-content h1 {
  font-size: 24px;
  margin: 24px 0 16px;
  color: #303133;
}

.markdown-content h2 {
  font-size: 20px;
  margin: 20px 0 12px;
  color: #303133;
}

.markdown-content h3 {
  font-size: 18px;
  margin: 18px 0 10px;
  color: #303133;
}

.markdown-content p {
  margin: 12px 0;
  line-height: 1.8;
}

.markdown-content ul {
  padding-left: 20px;
  margin: 15px 0;
}

.markdown-content li {
  margin-bottom: 8px;
  line-height: 1.8;
}

.markdown-content code {
  background-color: #f5f7fa;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: Consolas, Monaco, 'Andale Mono', monospace;
  font-size: 0.9em;
}

.tag-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #e4e7ed;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
}

.action-section {
  margin-top: 30px;
  display: flex;
  gap: 15px;
  justify-content: center;
}

.copy-success-content {
  text-align: center;
  padding: 20px;
}

.success-icon {
  margin-bottom: 15px;
}
</style>