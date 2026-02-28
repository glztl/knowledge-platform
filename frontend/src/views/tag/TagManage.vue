<!-- src/views/tag/TagManage.vue -->
<template>
  <div class="tag-manage-container">
    <el-card class="tag-card">
      <template #header>
        <div class="card-header">
          <span>🏷️ 标签管理</span>
          <el-button 
            type="primary" 
            size="small" 
            @click="handleAdd"
          >
            <el-icon><Plus /></el-icon>
            新建标签
          </el-button>
        </div>
      </template>
      
      <el-empty 
        v-if="loading" 
        description="加载中..."
      />
      
      <el-empty 
        v-else-if="tags.length === 0" 
        description="暂无标签"
      >
        <el-button type="primary" @click="handleAdd">创建第一个标签</el-button>
      </el-empty>
      
      <div v-else class="tag-list">
        <div 
          v-for="tag in tags" 
          :key="tag.id" 
          class="tag-item"
        >
          <el-tag 
            type="info" 
            size="large"
            class="tag-name"
          >
            {{ tag.name }}
          </el-tag>
          
          <div class="tag-actions">
            <el-button 
              type="danger" 
              size="small" 
              link 
              @click="handleDelete(tag)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 新建标签对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新建标签"
      width="400px"
      @close="resetForm"
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="80px"
      >
        <el-form-item label="标签名称" prop="name">
          <el-input 
            v-model="form.name" 
            placeholder="请输入标签名称（如：Java、笔记）" 
            clearable
            maxlength="20"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ submitting ? '提交中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getTagList, createTag, deleteTag } from '@/api/tag'

// 状态
const loading = ref(false)
const submitting = ref(false)
const tags = ref<any[]>([])
const dialogVisible = ref(false)

// 表单
const formRef = ref()
const form = reactive({
  name: ''
})
const rules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 1, max: 20, message: '标签名称长度为1-20个字符', trigger: 'blur' },
    { 
      pattern: /^[a-zA-Z0-9\u4e00-\u9fa5_-]+$/, 
      message: '只能包含字母、数字、中文、下划线和短横线', 
      trigger: 'blur' 
    }
  ]
}

// 获取标签列表
const fetchTags = async () => {
  loading.value = true
  try {
    const res = await getTagList()
    tags.value = res.data || []
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '获取标签失败')
    console.error('获取标签失败:', error)
  } finally {
    loading.value = false
  }
}

// 新建标签
const handleAdd = () => {
  form.name = ''
  dialogVisible.value = true
}

// 删除标签
const handleDelete = (tag: { id: number; name: string }) => {
  ElMessageBox.confirm(
    `确定要删除标签 "${tag.name}" 吗？删除后该标签将从所有文档中移除。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteTag(tag.id)
      ElMessage.success('标签删除成功')
      fetchTags()
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '删除标签失败')
      console.error('删除标签失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await createTag({
        name: form.name.trim()
      })
      ElMessage.success('标签创建成功')
      dialogVisible.value = false
      fetchTags()
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '创建标签失败')
      console.error('创建标签失败:', error)
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  form.name = ''
}

// 初始化
onMounted(() => {
  fetchTags()
})
</script>

<style scoped>
.tag-manage-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.tag-card {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tag-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  padding: 15px 0;
}

.tag-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 15px;
  background-color: #f9fafc;
  border-radius: 6px;
  transition: all 0.3s;
}

.tag-item:hover {
  background-color: #f0f2f5;
  transform: translateX(5px);
}

.tag-name {
  font-size: 16px;
  font-weight: 500;
  padding: 6px 12px;
}

.tag-actions {
  display: flex;
  gap: 10px;
}

:deep(.el-tag) {
  border: none;
}
</style>