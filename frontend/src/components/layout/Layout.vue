<!-- src/components/layout/Layout.vue -->

<template>
  <div class="layout">

    <!-- Sidebar -->
    <aside
      class="sidebar"
      :class="{ collapsed: isCollapsed }"
    >

      <div class="sidebar-header">

        <div class="logo">
          <el-icon :size="26">
            <Reading />
          </el-icon>
        </div>

        <div
          class="title"
          v-if="!isCollapsed"
        >
          Knowledge
        </div>

      </div>


      <nav class="menu">

        <div
          class="menu-item"
          :class="{ active: activeMenu === '/dashboard' }"
          @click="router.push('/dashboard')"
        >
          <el-icon><House /></el-icon>
          <span v-if="!isCollapsed">仪表盘</span>
        </div>


        <div
          class="menu-item"
          :class="{ active: activeMenu === '/document' }"
          @click="router.push('/document')"
        >
          <el-icon><Document /></el-icon>
          <span v-if="!isCollapsed">文档</span>
        </div>


        <div
          class="menu-item"
          :class="{ active: activeMenu === '/category' }"
          @click="router.push('/category')"
        >
          <el-icon><Folder /></el-icon>
          <span v-if="!isCollapsed">分类</span>
        </div>


        <div
          class="menu-item"
          :class="{ active: activeMenu === '/tag' }"
          @click="router.push('/tag')"
        >
          <el-icon><PriceTag /></el-icon>
          <span v-if="!isCollapsed">标签</span>
        </div>


        <div
          class="menu-item"
          :class="{ active: activeMenu === '/profile' }"
          @click="router.push('/profile')"
        >
          <el-icon><User /></el-icon>
          <span v-if="!isCollapsed">个人中心</span>
        </div>

      </nav>


      <!-- collapse button -->

      <div
        class="collapse-btn"
        @click="toggleSidebar"
      >

        <el-icon>
          <Fold v-if="!isCollapsed"/>
          <Expand v-else/>
        </el-icon>

      </div>

    </aside>



    <!-- Main -->

    <main
      class="main"
      :class="{ collapsed: isCollapsed }"
    >

      <!-- Navbar -->

      <header class="navbar">

        <div class="left">

          <el-icon
            class="toggle"
            @click="toggleSidebar"
          >
            <Fold v-if="!isCollapsed"/>
            <Expand v-else/>
          </el-icon>

          <div class="page-title">
            {{ pageTitle }}
          </div>

        </div>


        <div class="right">

          <el-dropdown @command="handleCommand">

            <div class="user">

              <el-avatar
                :size="34"
                :src="userAvatar"
              >
                {{ userNickname.charAt(0) }}
              </el-avatar>

              <span
                v-if="!isCollapsed"
                class="name"
              >
                {{ userNickname }}
              </span>

            </div>

            <template #dropdown>

              <el-dropdown-menu>

                <el-dropdown-item command="profile">
                  个人中心
                </el-dropdown-item>

                <el-dropdown-item command="logout">
                  退出登录
                </el-dropdown-item>

              </el-dropdown-menu>

            </template>

          </el-dropdown>

        </div>

      </header>



      <!-- content -->

      <section class="content">

        <router-view v-slot="{ Component }">

          <transition name="apple-fade" mode="out-in">

            <component
              :is="Component"
              :key="$route.fullPath"
            />

          </transition>

        </router-view>

      </section>

    </main>

  </div>
</template>



<script setup lang="ts">

import { ref, computed, onMounted } from "vue"
import { useRouter, useRoute } from "vue-router"

import {
  Reading,
  House,
  Document,
  Folder,
  PriceTag,
  User,
  Fold,
  Expand
} from "@element-plus/icons-vue"

import { useUserStore } from "@/store/modules/user"
import { ElMessage } from "element-plus"


const router = useRouter()

const route = useRoute()

const userStore = useUserStore()

const isCollapsed = ref(false)


const userAvatar = computed(
  () => userStore.userInfo?.avatar || ""
)

const userNickname = computed(
  () =>
    userStore.userInfo?.nickname ||
    userStore.userInfo?.username ||
    "用户"
)


const activeMenu = computed(() => {

  const path = route.path

  if (path.startsWith("/document")) return "/document"

  if (path.startsWith("/category")) return "/category"

  if (path.startsWith("/tag")) return "/tag"

  if (path.startsWith("/profile")) return "/profile"

  return "/dashboard"

})


const pageTitle = computed(
  () => route.meta.title || "Dashboard"
)


const toggleSidebar = () => {

  isCollapsed.value = !isCollapsed.value

}


const handleCommand = (cmd: string) => {

  if (cmd === "profile")
    router.push("/profile")

  if (cmd === "logout") {

    userStore.logout()

    router.push("/login")

    ElMessage.success("已退出登录")

  }

}


onMounted(async () => {

  if (userStore.token) {

    try {

      await userStore.getUserInfo()

    }

    catch {

      router.push("/login")

    }

  }

})

</script>



<style scoped>

/* layout */

.layout {

  display: flex;

  background:
    #f5f5f7;

  min-height: 100vh;

}


/* sidebar */

.sidebar {

  width: 240px;

  background:
    rgba(255,255,255,0.7);

  backdrop-filter:
    blur(20px);

  border-right:
    1px solid rgba(0,0,0,0.06);

  transition: 0.3s;

  position: fixed;

  height: 100vh;

  z-index: 1000;

}

.sidebar.collapsed {

  width: 72px;

}


.sidebar-header {

  height: 64px;

  display: flex;

  align-items: center;

  padding-left: 18px;

  font-weight: 600;

  font-size: 18px;

}


.logo {

  color: #0071e3;

}

.title {

  margin-left: 12px;

}


/* menu */

.menu {

  padding: 10px;

}

.menu-item {

  display: flex;

  align-items: center;

  gap: 14px;

  padding: 12px;

  border-radius: 12px;

  cursor: pointer;

  color: #1d1d1f;

  margin-bottom: 4px;

  transition: 0.2s;

}

.menu-item:hover {

  background: rgba(0,0,0,0.05);

}

.menu-item.active {

  background:
    #0071e3;

  color: white;

}


/* collapse button */

.collapse-btn {

  position: absolute;

  bottom: 20px;

  width: 100%;

  display: flex;

  justify-content: center;

  cursor: pointer;

}


/* main */

.main {

  margin-left: 240px;

  width: 100%;

  transition: 0.3s;

}

.main.collapsed {

  margin-left: 72px;

}


/* navbar */

.navbar {

  height: 64px;

  background:
    rgba(255,255,255,0.7);

  backdrop-filter:
    blur(20px);

  border-bottom:
    1px solid rgba(0,0,0,0.06);

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding: 0 24px;

}


.left {

  display: flex;

  align-items: center;

  gap: 16px;

}

.page-title {

  font-size: 20px;

  font-weight: 600;

}


.toggle {

  cursor: pointer;

}


/* user */

.user {

  display: flex;

  align-items: center;

  gap: 10px;

  cursor: pointer;

}

.name {

  font-weight: 500;

}


/* content */

.content {

  padding: 30px;

}


/* animation */

.apple-fade-enter-active {

  transition: all .25s;

}

.apple-fade-leave-active {

  transition: all .2s;

}

.apple-fade-enter-from {

  opacity: 0;

  transform: translateY(10px);

}

.apple-fade-leave-to {

  opacity: 0;

  transform: translateY(-10px);

}

</style>