<!-- src/views/document/DocumentEdit.vue -->
<template>
  <div class="document-edit-container">
    <el-card class="document-edit-card">
      <template #header>
        <div class="card-header">
          <el-button 
            type="text" 
            icon="ArrowLeft" 
            @click="handleCancel"
          >
            返回
          </el-button>
          <span>{{ isEdit ? '编辑文档' : '新建文档' }}</span>
          <div class="header-actions">
            <el-button @click="handlePreview">预览</el-button>
            <el-button type="primary" @click="handleSave" :loading="saving">
              {{ saving ? '保存中...' : '保存' }}
            </el-button>
          </div>
        </div>
      </template>
      
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="80px"
        class="document-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input 
            v-model="form.title" 
            placeholder="请输入文档标题" 
            clearable
            maxlength="200"
          />
        </el-form-item>
        
        <el-form-item label="分类" prop="categoryId">
          <el-cascader
            v-model="form.categoryId"
            :options="categoryOptions"
            :props="{ checkStrictly: true, emitPath: false, label: 'name', value: 'id' }"
            placeholder="请选择分类"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="标签" prop="tagIds">
          <el-select
            v-model="form.tagIds"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或创建标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in tagOptions"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="公开" prop="isPublic">
          <el-switch
            v-model="form.isPublic"
            active-text="公开"
            inactive-text="私有"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="15"
            placeholder="请输入文档内容（支持 Markdown）"
          />
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="文档预览"
      width="80%"
      :close-on-click-modal="false"
    >
      <div class="preview-content" v-html="previewHtml"></div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import DOMPurify from 'dompurify'
import { getCategoryTree } from '@/api/category'
import { getTagList } from '@/api/tag'
import { getDocument, createDocument, updateDocument } from '@/api/document'

const router = useRouter()
const route = useRoute()

const formRef = ref() 

// 表单数据
const form = reactive({
  id: undefined as number | undefined,
  title: '',
  content: '',
  categoryId: 0,
  tagIds: [] as number[],
  isPublic: 0
})

// 验证规则
const rules = {
  title: [
    { required: true, message: '请输入文档标题', trigger: 'blur' },
    { max: 200, message: '标题不能超过200个字符', trigger: 'blur' }
  ]
}

// 分类和标签选项
const categoryOptions = ref<any[]>([])
const tagOptions = ref<any[]>([])

// 状态
const isEdit = computed(() => !!route.params.id)
const saving = ref(false)
const previewVisible = ref(false)
const previewHtml = ref('')

// 获取分类树
const fetchCategories = async () => {
  try {
    const res = await getCategoryTree()
    categoryOptions.value = buildCategoryTree(res.data || [])
  } catch (error: any) {
    console.error('获取分类失败:', error)
    ElMessage.warning('获取分类失败，使用默认值')
  }
}

// 构建树形结构（用于级联选择器）
const buildCategoryTree = (categories: any[]): any[] => {
  const map = new Map()
  const result: any[] = []
  
  // 先构建映射
  categories.forEach(category => {
    map.set(category.id, { ...category, children: [] })
  })
  
  // 再构建树
  categories.forEach(category => {
    const node = map.get(category.id)
    if (category.parentId === 0) {
      result.push(node)
    } else {
      const parent = map.get(category.parentId)
      if (parent) {
        parent.children.push(node)
      }
    }
  })
  
  return result
}

// 获取标签列表
const fetchTags = async () => {
  try {
    const res = await getTagList()
    tagOptions.value = res.data || []
  } catch (error: any) {
    console.error('获取标签失败:', error)
    ElMessage.warning('获取标签失败，使用默认值')
  }
}

// 获取文档详情（编辑模式）
const fetchDocument = async () => {
  if (!isEdit.value) return
  
  try {
    const id = Number(route.params.id)
    const res = await getDocument(id)
    
    form.id = res.data.id
    form.title = res.data.title || ''
    form.content = res.data.content || ''
    form.categoryId = res.data.categoryId || 0
    form.tagIds = res.data.tagNames?.map((name: string) => 
      tagOptions.value.find(tag => tag.name === name)?.id || 0
    ).filter(Boolean) || []
    form.isPublic = res.data.isPublic || 0
  } catch (error: any) {
    ElMessage.error('获取文档失败')
    console.error(error)
  }
}

// 保存文档
const handleSave = async () => {
  if (saving.value) return
  
  try {
    // 表单验证
    // @ts-ignore
    const valid = await formRef.value?.validate()
    if (!valid) return
    
    saving.value = true
    
    if (isEdit.value && form.id) {
      // 更新文档
      await updateDocument(form.id, {
        id: form.id,
        title: form.title,
        content: form.content,
        categoryId: form.categoryId,
        tagIds: form.tagIds,
        isPublic: form.isPublic
      })
      ElMessage.success('文档更新成功')
    } else {
      // 创建文档
      const res = await createDocument({
        title: form.title,
        content: form.content,
        categoryId: form.categoryId,
        tagIds: form.tagIds,
        isPublic: form.isPublic
      })
      ElMessage.success('文档创建成功')
      form.id = res.data.id
    }
    
    // 跳转到文档详情页
    router.push(`/document/${form.id}`)
    
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '保存失败')
    console.error(error)
  } finally {
    saving.value = false
  }
}

// 预览文档（简单 Markdown 渲染）
const handlePreview = () => {
  if (!form.content.trim()) {
    ElMessage.warning('没有可预览的内容')
    return
  }
  
  // 简单的 Markdown -> HTML 转换
  let html = form.content
    .replace(/^### (.*$)/gim, '<h3>$1</h3>')
    .replace(/^## (.*$)/gim, '<h2>$1</h2>')
    .replace(/^# (.*$)/gim, '<h1>$1</h1>')
    .replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/gim, '<em>$1</em>')
    .replace(/`([^`]+)`/gim, '<code>$1</code>')
    .replace(/^- (.*$)/gim, '<li>$1</li>')
    .replace(/^(?![#\*-]).+$/gim, '<p>$&</p>')
    .replace(/<li>/g, '<ul><li>')
    .replace(/<\/li>(?![\s\S]*<\/li>)/g, '</li></ul>')
  
  previewHtml.value = DOMPurify.sanitize(html)
  previewVisible.value = true
}

// 取消编辑
const handleCancel = () => {
  ElMessageBox.confirm('确定要放弃当前编辑吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.back()
  })
}

// 初始化
onMounted(async () => {
  await Promise.all([
    fetchCategories(),
    fetchTags()
  ])
  
  if (isEdit.value) {
    await fetchDocument()
  }
})
</script>

<style scoped>
.document-edit-container {
  padding: 20px;
}

.document-edit-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.document-form {
  padding-top: 20px;
}

.preview-content {
  max-height: 600px;
  overflow-y: auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  line-height: 1.8;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}

.preview-content img {
  max-width: 100%;
  height: auto;
}

.preview-content pre {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 15px 0;
}

.preview-content code {
  background-color: #f5f7fa;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: Consolas, Monaco, 'Andale Mono', monospace;
}

.preview-content ul {
  padding-left: 20px;
  margin: 15px 0;
}

.preview-content li {
  margin-bottom: 5px;
}
</style>