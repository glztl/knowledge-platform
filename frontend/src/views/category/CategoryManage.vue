<!-- src/views/category/CategoryManage.vue -->
<template>
  <div class="category-manage-container">
    <el-card class="category-card">
      <template #header>
        <div class="card-header">
          <span>📁 分类管理</span>
          <el-button 
            type="primary" 
            size="small" 
            @click="handleAdd"
          >
            <el-icon><Plus /></el-icon>
            新建分类
          </el-button>
        </div>
      </template>
      
      <el-empty 
        v-if="loading" 
        description="加载中..."
      />
      
      <el-empty 
        v-else-if="categories.length === 0" 
        description="暂无分类"
      >
        <el-button type="primary" @click="handleAdd">创建第一个分类</el-button>
      </el-empty>
      
      <el-tree
        v-else
        :data="categories"
        :props="treeProps"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        class="category-tree"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span class="node-label">
              <el-icon v-if="data.children && data.children.length > 0"><Folder /></el-icon>
              <el-icon v-else><FolderOpened /></el-icon>
              {{ node.label }}
            </span>
            <span class="node-operation">
              <el-button 
                type="text" 
                size="small"
                @click.stop="handleEdit(data)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                type="text" 
                size="small"
                @click.stop="handleDelete(data)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>
    
    <!-- 新建/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="400px"
      @close="resetForm"
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        
        <el-form-item label="父分类" prop="parentId">
          <el-cascader
            v-model="form.parentId"
            :options="categoryOptions"
            :props="cascaderProps"
            placeholder="请选择父分类（根分类留空）"
            clearable
            style="width: 100%"
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder, FolderOpened, Edit, Delete } from '@element-plus/icons-vue'
import { 
  getCategoryTree, 
  createCategory, 
  updateCategory, 
  deleteCategory 
} from '@/api/category'

// 状态
const loading = ref(false)
const submitting = ref(false)
const categories = ref<any[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)

// 表单
const formRef = ref()
const form = reactive({
  name: '',
  parentId: 0
})
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 20, message: '分类名称长度为1-20个字符', trigger: 'blur' }
  ]
}

// 树形配置
const treeProps = {
  label: 'name',
  children: 'children'
}

// 级联选择器配置
const cascaderProps = {
  checkStrictly: true,
  emitPath: false,
  label: 'name',
  value: 'id',
  children: 'children'
}

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑分类' : '新建分类')

// 过滤当前分类及其子分类（避免循环引用）
const filterCurrentCategory = (list: any[], currentId: number | null): any[] => {
  if (!currentId) return list
  
  const result: any[] = []
  for (const item of list) {
    if (item.id === currentId) continue
    
    const filteredChildren = item.children 
      ? filterCurrentCategory(item.children, currentId) 
      : []
    
    result.push({
      ...item,
      children: filteredChildren.length > 0 ? filteredChildren : undefined
    })
  }
  return result
}

const categoryOptions = computed(() => {
  return filterCurrentCategory(categories.value, isEdit.value ? currentId.value : null)
})

// 获取分类树
const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getCategoryTree()
    categories.value = res.data || []
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '获取分类失败')
    console.error('获取分类失败:', error)
  } finally {
    loading.value = false
  }
}

// 新建分类
const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  form.name = ''
  form.parentId = 0
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (data: any) => {
  isEdit.value = true
  currentId.value = data.id
  form.name = data.name
  form.parentId = data.parentId
  dialogVisible.value = true
}

// 删除分类
const handleDelete = (data: any) => {
  ElMessageBox.confirm(
    `确定要删除分类 "${data.name}" 吗？删除后该分类下的文档将移至未分类。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteCategory(data.id)
      ElMessage.success('分类删除成功')
      fetchCategories()
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '删除分类失败')
      console.error('删除分类失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value?.validate(async (valid: boolean) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (isEdit.value && currentId.value) {
        await updateCategory(currentId.value, {
          id: currentId.value,
          name: form.name,
          parentId: form.parentId || 0
        })
        ElMessage.success('分类更新成功')
      } else {
        await createCategory({
          name: form.name,
          parentId: form.parentId || 0
        })
        ElMessage.success('分类创建成功')
      }
      
      dialogVisible.value = false
      fetchCategories()
    } catch (error: any) {
      ElMessage.error(isEdit.value ? '更新分类失败' : '创建分类失败')
      console.error(isEdit.value ? '更新分类失败:' : '创建分类失败:', error)
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  form.name = ''
  form.parentId = 0
}

// 初始化
onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-manage-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.category-card {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-tree {
  margin-top: 20px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding: 8px 10px;
  width: 100%;
}

.node-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
}

.node-operation {
  display: flex;
  gap: 15px;
  opacity: 0;
  transition: opacity 0.3s;
}

.custom-tree-node:hover .node-operation {
  opacity: 1;
}

:deep(.el-tree-node__content) {
  height: auto;
  padding: 8px 0;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}
</style>