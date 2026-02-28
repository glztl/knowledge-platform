<!-- src/views/document/DocumentDetail.vue -->
<template>
  <div class="document-detail-container">
    <el-card class="document-card">
      <template #header>
        <div class="card-header">
          <el-button 
            type="text" 
            icon="ArrowLeft" 
            @click="handleBack"
          >
            返回
          </el-button>
          <div class="title-section">
            <h1 class="document-title">{{ documentData.title }}</h1>
            <div class="meta-info">
              <span class="meta-item">
                <el-icon><Folder /></el-icon>
                {{ documentData.categoryName || '未分类' }}
              </span>
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ documentData.viewCount || 0 }} 浏览
              </span>
              <span class="meta-item">
                <el-icon><Timer /></el-icon>
                {{ formatDate(documentData.updatedAt) }}
              </span>
              <span 
                v-if="documentData.isPublic || documentData.shareToken" 
                class="meta-item public-tag"
              >
                <el-icon><Unlock /></el-icon>
                {{ documentData.shareToken ? '已分享' : '公开' }}
              </span>
            </div>
          </div>
          <div class="action-buttons">
            <el-button 
              v-if="isOwner"
              type="success" 
              @click="handleShare"
            >
              <el-icon><Share /></el-icon>
              {{ documentData.shareToken ? '分享设置' : '分享' }}
            </el-button>
            <el-button 
              v-if="isOwner"
              type="primary" 
              @click="handleEdit"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="document-content" v-if="documentData.content">
        <div 
          class="markdown-content" 
          v-html="renderedContent"
        ></div>
      </div>
      
      <el-empty 
        v-else 
        description="文档内容为空"
      />
      
      <!-- 标签区域 -->
      <div v-if="documentData.tagNames?.length" class="tag-section">
        <el-tag 
          v-for="(tag, index) in documentData.tagNames" 
          :key="index" 
          type="info" 
          size="small"
          class="tag-item"
        >
          {{ tag }}
        </el-tag>
      </div>
    </el-card>
    
    <!-- 分享设置对话框 -->
      <el-dialog
      v-model="shareDialogVisible"
      :title="documentData.shareToken ? '分享设置' : '生成分享链接'"
      width="500px"
      @close="resetShareForm"
    >
      <el-form label-width="100px" :model="shareForm">
        <el-form-item label="分享方式">
          <el-radio-group v-model="shareForm.publicAccess">
            <el-radio :label="false">私密分享（需链接+密码）</el-radio>
            <el-radio :label="true">公开访问（无需密码）</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item 
          label="访问密码" 
          v-if="!shareForm.publicAccess"
        >
          <el-input 
            v-model="shareForm.password" 
            placeholder="留空表示无需密码"
            show-password
            clearable
          />
          <div class="password-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>建议设置6位以上密码增强安全性</span>
          </div>
        </el-form-item>
        
        <el-form-item label="有效期">
          <el-select v-model="shareForm.expireHours" placeholder="请选择">
            <el-option :value="1" label="1小时" />
            <el-option :value="24" label="24小时" />
            <el-option :value="168" label="7天" />
            <el-option :value="0" label="永久有效" />
          </el-select>
        </el-form-item>
        
        <el-form-item v-if="documentData.shareToken" label="当前链接">
          <el-input 
            v-model="currentShareUrl" 
            readonly
            @click="handleCopyClick"
          >
            <template #append>
              <el-button @click="handleCopyClick">复制</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button 
          v-if="documentData.shareToken"
          type="danger"
          @click="handleCancelShare"
        >
          取消分享
        </el-button>
        <el-button @click="shareDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleSubmitShare"
          :loading="sharing"
        >
          {{ sharing ? '处理中...' : (documentData.shareToken ? '更新设置' : '生成链接') }}
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
        <p class="share-tip">可通过链接直接访问文档</p>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Folder, View, Timer, Unlock, Edit, Share, 
  InfoFilled, SuccessFilled 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { getDocument } from '@/api/document'
import { 
  generateShareLink, 
  cancelShare, 
  updateShareSettings 
} from '@/api/document'
import DOMPurify from 'dompurify'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 文档数据
const documentData = ref<any>({})
const loading = ref(false)
const isOwner = ref(false)

// 分享相关
const shareDialogVisible = ref(false)
const copySuccessVisible = ref(false)
const sharing = ref(false)
const shareForm = ref({
  requirePassword: false,
  password: '',
  expireHours: 24,
  publicAccess: false
})
const currentShareUrl = ref('')

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
  if (!documentData.value.content) return ''
  
  let html = documentData.value.content
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
})

// 🔧 通用剪贴板复制函数（带降级方案，兼容 HTTP/HTTPS）
const copyToClipboard = async (text: string): Promise<boolean> => {
  // 优先使用现代 Clipboard API（需 HTTPS 或 localhost）
  if (navigator.clipboard && window.isSecureContext) {
    try {
      await navigator.clipboard.writeText(text)
      return true
    } catch (e) {
      console.warn('🔐 navigator.clipboard 失败，尝试降级方案', e)
    }
  }
  
  // 降级方案：使用 document.execCommand('copy')
  try {
    const textarea = document.createElement('textarea')
    textarea.value = text
    // 隐藏 textarea，避免页面跳动
    textarea.style.position = 'fixed'
    textarea.style.left = '-9999px'
    textarea.style.top = '-9999px'
    textarea.style.opacity = '0'
    textarea.setAttribute('readonly', '')
    textarea.setAttribute('aria-hidden', 'true')
    
    document.body.appendChild(textarea)
    textarea.focus()
    textarea.select()
    
    const success = document.execCommand('copy')
    document.body.removeChild(textarea)
    return success
  } catch (e) {
    console.error('✂️ execCommand 复制失败', e)
    return false
  }
}

// 获取文档详情
const fetchDocument = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const res = await getDocument(id)
    documentData.value = res.data || {}
    
    const currentUserId = userStore.userInfo?.id
    const documentUserId = documentData.value.userId
    
    isOwner.value = 
      !!currentUserId && 
      !!documentUserId &&
      Number(currentUserId) === Number(documentUserId)
    
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '获取文档失败')
    console.error('获取文档失败:', error)
    router.push('/document')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const handleBack = () => {
  router.back()
}

// 编辑文档
const handleEdit = () => {
  router.push(`/document/edit/${documentData.value.id}`)
}

// 打开分享对话框
const handleShare = () => {
  shareDialogVisible.value = true
}

// 重置分享表单
const resetShareForm = () => {
  shareForm.value = {
    requirePassword: false,
    password: '',
    expireHours: 24,
    publicAccess: false
  }
}

// 提交分享设置
const handleSubmitShare = async () => {
  sharing.value = true
  try {
    const dto = {
      id: documentData.value.id,
      requirePassword: !shareForm.value.publicAccess && !!shareForm.value.password,
      password: shareForm.value.password || undefined,
      expireHours: shareForm.value.expireHours,
      publicAccess: shareForm.value.publicAccess
    }
    
    let res
    if (documentData.value.shareToken) {
      res = await updateShareSettings(dto)
    } else {
      res = await generateShareLink(dto)
    }
    
    // 更新文档分享信息
    documentData.value.shareToken = res.data.shareToken
    documentData.value.isPublic = shareForm.value.publicAccess ? 1 : 0
    currentShareUrl.value = res.data.shareUrl
    
    ElMessage.success(documentData.value.shareToken ? '分享设置已更新' : '分享链接已生成')
    
    // ✅ 重要：不在此处自动复制（异步后可能失去用户交互上下文）
    // 改为提示用户点击输入框旁的「复制」按钮
    ElMessage.info('请点击链接旁的「复制」按钮获取分享链接')
    
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '分享操作失败')
    console.error('分享操作失败:', error)
  } finally {
    sharing.value = false
  }
}

// 取消分享
const handleCancelShare = () => {
  ElMessageBox.confirm(
    '确定要取消分享吗？取消后分享链接将失效。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await cancelShare(documentData.value.id)
      documentData.value.shareToken = null
      documentData.value.isPublic = 0
      shareDialogVisible.value = false
      ElMessage.success('分享已取消')
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '取消分享失败')
      console.error('取消分享失败:', error)
    }
  })
}

// 🔧 复制分享链接（绑定用户点击事件，确保在交互上下文中执行）
const handleCopyClick = async () => {
  if (!currentShareUrl.value) {
    ElMessage.warning('暂无可复制的链接')
    return
  }
  
  // 调试日志（生产环境可移除）
  console.log('🔐 安全上下文:', window.isSecureContext)
  console.log('📋 clipboard API 可用:', !!navigator.clipboard)
  
  const success = await copyToClipboard(currentShareUrl.value)
  
  if (success) {
    copySuccessVisible.value = true
  } else {
    // 复制失败时，提供备选方案：选中输入框内容方便手动复制
    ElMessage.warning('自动复制失败，已选中链接，请按 Ctrl+C 手动复制')
    
    // 尝试选中输入框中的文本（Element Plus 的 input 需要特殊处理）
    setTimeout(() => {
      const inputEl = document.querySelector('.el-input__inner') as HTMLInputElement
      if (inputEl) {
        inputEl.focus()
        inputEl.select()
      }
    }, 100)
  }
}

// 初始化
onMounted(() => {
  fetchDocument()
})
</script>

<style scoped>
.document-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.document-card {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.title-section {
  flex: 1;
}

.document-title {
  font-size: 28px;
  color: #303133;
  margin: 0 0 10px 0;
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

.public-tag {
  color: #67c23a;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 10px;
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

.markdown-content strong {
  font-weight: bold;
}

.markdown-content em {
  font-style: italic;
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

.password-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 13px;
  margin-top: 8px;
}

.copy-success-content {
  text-align: center;
  padding: 20px;
}

.success-icon {
  margin-bottom: 15px;
}

.share-tip {
  color: #909399;
  font-size: 14px;
  margin-top: 10px;
}

/* 输入框点击时的高亮效果 */
.el-input__inner {
  cursor: pointer;
}
.el-input__inner:hover {
  border-color: #409eff;
}
</style>