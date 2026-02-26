<!-- src/components/layout/Layout.vue -->
<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
      <div class="sidebar-header">
        <el-icon class="sidebar-logo" :size="isCollapsed ? 24 : 32">
          <Reading />
        </el-icon>
        <span class="sidebar-title" v-if="!isCollapsed">知识管理</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        
        <el-menu-item index="/document">
          <el-icon><Document /></el-icon>
          <template #title>文档</template>
        </el-menu-item>
        
        <el-menu-item index="/category">
          <el-icon><Folder /></el-icon>
          <template #title>分类</template>
        </el-menu-item>
        
        <el-menu-item index="/tag">
          <el-icon><PriceTag /></el-icon>
          <template #title>标签</template>
        </el-menu-item>
        
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>
      
      <div class="sidebar-footer" @click="toggleSidebar">
        <el-icon :size="16">
          <Expand v-if="isCollapsed" />
          <Fold v-else />
        </el-icon>
      </div>
    </div>
    
    <!-- 主内容区 -->
    <div class="main-container" :class="{ 'main-container-collapsed': isCollapsed }">
      <!-- 顶部导航 -->
      <div class="navbar">
        <div class="navbar-left">
          <el-icon class="menu-toggle" @click="toggleSidebar" :size="20">
            <Expand v-if="isCollapsed" />
            <Fold v-else />
          </el-icon>
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        
        <div class="navbar-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userAvatar">
                {{ userNickname?.charAt(0) }}
              </el-avatar>
              <span class="user-nickname" v-if="!isCollapsed">{{ userNickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 内容区域 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" :key="$route.fullPath" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Reading, House, Document, Folder, PriceTag, User, 
  Expand, Fold, SwitchButton 
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapsed = ref(false)
const userAvatar = computed(() => userStore.userInfo?.avatar || '')
const userNickname = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '用户')

// 根据路由计算激活菜单
const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/document') && !path.startsWith('/document/edit')) {
    return '/document'
  }
  if (path.startsWith('/category')) return '/category'
  if (path.startsWith('/tag')) return '/tag'
  if (path.startsWith('/profile')) return '/profile'
  return '/dashboard'
})

// 根据路由计算页面标题
const pageTitle = computed(() => {
  const meta = route.meta
  return meta.title || '知识管理平台'
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 下拉菜单命令
const handleCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}

// 初始化用户信息
onMounted(async () => {
  if (userStore.token) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      console.error('获取用户信息失败:', error)
      localStorage.removeItem('token')
      router.push('/login')
    }
  }
})
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 侧边栏 */
.sidebar {
  width: 210px;
  background-color: #304156;
  transition: width 0.28s;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1001;
  overflow: hidden;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
}

.sidebar-collapsed {
  width: 64px;
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-logo {
  color: #409EFF;
  margin-right: 12px;
}

.sidebar-title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.sidebar-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.2);
  color: #bfcbd9;
  cursor: pointer;
  transition: background-color 0.3s;
}

.sidebar-footer:hover {
  background-color: rgba(0, 0, 0, 0.3);
}

/* 主内容区 */
.main-container {
  flex: 1;
  min-height: 100vh;
  transition: margin-left 0.28s;
  margin-left: 210px;
}

.main-container-collapsed {
  margin-left: 64px;
}

/* 顶部导航 */
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.menu-toggle {
  cursor: pointer;
  margin-right: 20px;
  color: #5a5e66;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-nickname {
  margin-left: 10px;
  color: #303133;
}

/* 内容区域 */
.app-main {
  padding: 20px;
  min-height: calc(100vh - 50px);
}

/* 过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease-in-out;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>