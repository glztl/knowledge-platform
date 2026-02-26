import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/components/layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'document',
        name: 'Document',
        component: () => import('@/views/document/DocumentList.vue'),
        meta: { title: '文档列表' }
      },
      {
        path: 'document/create',
        name: 'DocumentCreate',
        component: () => import('@/views/document/DocumentEdit.vue'),
        meta: { title: '创建文档' }
      },
      {
        path: 'document/:id',
        name: 'DocumentDetail',
        component: () => import('@/views/document/DocumentDetail.vue'),
        meta: { title: '文档详情' }
      },
      {
        path: 'document/edit/:id',
        name: 'DocumentEdit',
        component: () => import('@/views/document/DocumentEdit.vue'),
        meta: { title: '编辑文档' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/CategoryManage.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'tag',
        name: 'Tag',
        component: () => import('@/views/tag/TagManage.vue'),
        meta: { title: '标签管理' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/share/:token',
    name: 'ShareDocument',
    component: () => import('@/views/document/ShareDocument.vue'),
    meta: { title: '分享文档' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 1. 404 页面直接放行
  if (to.name === 'NotFound') {
    next()
    return
  }

  // 2. 分享页面直接放行
  if (to.path.startsWith('/share/')) {
    next()
    return
  }

  // 3. 登录/注册页面：已登录则跳转首页
  if (to.path === '/login' || to.path === '/register') {
    if (token) {
      next('/')
    } else {
      next()
    }
    return
  }

  // 4. 其他页面：需要登录
  if (!token && to.meta.requiresAuth) {
    ElMessage.warning('请先登录')
    next('/login')
  } else {
    next()
  }
})

export default router