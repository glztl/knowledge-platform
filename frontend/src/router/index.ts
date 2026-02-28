// src/router/index.ts
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { requiresAuth: false } 
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/Register.vue'),
    meta: { requiresAuth: false }  
  },
  {
    path: '/',
    component: () => import('@/components/layout/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true }, 
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '仪表盘', requiresAuth: true } 
      },
      {
        path: 'document',
        name: 'Document',
        component: () => import('@/views/document/DocumentList.vue'),
        meta: { title: '文档列表', requiresAuth: true } 
      },
      {
        path: 'document/create',
        name: 'DocumentCreate',
        component: () => import('@/views/document/DocumentEdit.vue'),
        meta: { title: '创建文档', requiresAuth: true }
      },
      {
        path: 'document/:id',
        name: 'DocumentDetail',
        component: () => import('@/views/document/DocumentDetail.vue'),
        meta: { title: '文档详情', requiresAuth: true } 
      },
      {
        path: 'document/edit/:id',
        name: 'DocumentEdit',
        component: () => import('@/views/document/DocumentEdit.vue'),
        meta: { title: '编辑文档', requiresAuth: true } 
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/CategoryManage.vue'),
        meta: { title: '分类管理', requiresAuth: true } 
      },
      {
        path: 'tag',
        name: 'Tag',
        component: () => import('@/views/tag/TagManage.vue'),
        meta: { title: '标签管理', requiresAuth: true } 
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true } 
      }
    ]
  },
  {
    path: '/share/:token',
    name: 'ShareDocument',
    component: () => import('@/views/document/ShareDocument.vue'),
    meta: { title: '分享文档', requiresAuth: false } 
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { requiresAuth: false } 
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})


router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const isLogin = !!token
  const requiresAuth = to.meta.requiresAuth !== false  // undefined 或 true 都视为需要认证

  // 1. 不需要认证的页面：直接放行
  if (!requiresAuth) {
    next()
    return
  }

  // 2. 需要认证的页面：检查登录状态
  if (requiresAuth && !isLogin) {
    ElMessage.warning('请先登录')
    // 跳转到登录页，并记录目标地址（可选）
    next({
      path: '/login',
      query: { redirect: to.fullPath }  // 可选：登录后跳回原页面
    })
    return
  }

  // 3. 已登录用户访问登录/注册页：跳转首页
  if (isLogin && (to.path === '/login' || to.path === '/register')) {
    next('/dashboard')
    return
  }

  // 4. 其他情况：正常放行
  next()
})

export default router