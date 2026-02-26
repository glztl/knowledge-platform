<!-- src/views/document/DocumentList.vue -->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue' // ✅ 确保导入 ref/onMounted/computed
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus' // ✅ 关键修复：添加 ElMessage 导入
import { Search, Plus } from '@element-plus/icons-vue'
import { listDocuments } from '@/api/document'
import DocumentTable from './components/DocumentTable.vue'

const router = useRouter()
const activeTab = ref('all')
const searchKeyword = ref('')
const loading = ref(false)
const documents = ref<any[]>([]) // ✅ 确保初始值为空数组
const currentPage = ref(1)
const pageSize = ref(20)

// 分类过滤（简化版，后续完善）
const techDocuments = computed(() => 
  documents.value.filter(doc => 
    doc.categoryName?.includes('技术') || 
    doc.categoryName?.includes('Java') ||
    doc.categoryName?.includes('Python') ||
    doc.categoryName?.includes('前端')
  )
)
const studyDocuments = computed(() => 
  documents.value.filter(doc => 
    doc.categoryName?.includes('学习') || 
    doc.categoryName?.includes('笔记') ||
    doc.categoryName?.includes('读书')
  )
)
const lifeDocuments = computed(() => 
  documents.value.filter(doc => 
    doc.categoryName?.includes('生活') ||
    doc.categoryName?.includes('日记') ||
    doc.categoryName?.includes('随笔')
  )
)

// 获取文档列表（关键修复：适配两种返回格式）
const fetchDocuments = async (page = 1, size = 20) => {
  loading.value = true
  currentPage.value = page
  pageSize.value = size
  
  try {
    const res = await listDocuments(page, size)
    
    // ✅ 关键修复：适配两种返回格式
    let docList: any[] = []
    
    // 格式1: 直接返回数组 [ {}, {} ]
    if (Array.isArray(res.data)) {
      docList = res.data
    } 
    // 格式2: 返回 { list: [], total: number }
    else if (res.data && Array.isArray(res.data.list)) {
      docList = res.data.list
    }
    // 格式3: 后端可能直接返回 data 字段（无嵌套）
    else if (res.data) {
      docList = Array.isArray(res.data) ? res.data : [res.data]
    }
    
    documents.value = docList
    
    // 调试：打印实际返回数据
    console.log('文档列表返回:', res)
    console.log('解析后的文档:', docList)
    
  } catch (error: any) {
    // ✅ 关键修复：使用已导入的 ElMessage
    ElMessage.error(error.response?.data?.message || '获取文档列表失败')
    console.error('获取文档列表失败:', error)
    
    // 安全兜底：出错时设置为空数组，避免 undefined
    documents.value = []
  } finally {
    loading.value = false
  }
}

// 搜索（简化版）
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    ElMessage.info('搜索功能将在后续版本实现')
  }
}

// 切换标签
const handleTabClick = (tab: any) => {
  activeTab.value = tab.name
}

// 编辑文档
const handleEdit = (row: any) => {
  router.push(`/document/edit/${row.id}`)
}

// 查看文档
const handleView = (row: any) => {
  router.push(`/document/${row.id}`)
}

// 删除文档
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除文档 "${row.title}" 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('删除功能将在后续版本实现')
    fetchDocuments()
  })
}

// 初始化
onMounted(() => {
  fetchDocuments()
})
</script>

<!-- 模板部分保持不变 -->
<template>
  <div class="document-list-container">
    <div class="list-header">
      <h1>📄 我的文档</h1>
      
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文档..."
          :prefix-icon="Search"
          clearable
          style="width: 240px; margin-right: 15px;"
          @keyup.enter="handleSearch"
        />
        
        <el-button 
          type="primary" 
          @click="$router.push('/document/create')"
        >
          <el-icon><Plus /></el-icon>
          新建文档
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="全部文档" name="all">
        <DocumentTable 
          :documents="documents" 
          :loading="loading"
          @refresh="fetchDocuments"
          @edit="handleEdit"
          @delete="handleDelete"
          @view="handleView"
        />
      </el-tab-pane>
      
      <el-tab-pane label="技术笔记" name="tech">
        <DocumentTable 
          :documents="techDocuments" 
          :loading="loading"
          @refresh="fetchDocuments"
          @edit="handleEdit"
          @delete="handleDelete"
          @view="handleView"
        />
      </el-tab-pane>
      
      <el-tab-pane label="学习笔记" name="study">
        <DocumentTable 
          :documents="studyDocuments" 
          :loading="loading"
          @refresh="fetchDocuments"
          @edit="handleEdit"
          @delete="handleDelete"
          @view="handleView"
        />
      </el-tab-pane>
      
      <el-tab-pane label="生活记录" name="life">
        <DocumentTable 
          :documents="lifeDocuments" 
          :loading="loading"
          @refresh="fetchDocuments"
          @edit="handleEdit"
          @delete="handleDelete"
          @view="handleView"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<!-- 样式保持不变 -->
<style scoped>
.document-list-container {
  padding: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.list-header h1 {
  font-size: 24px;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>