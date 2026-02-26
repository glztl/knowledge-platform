<template>
  <div class="login-page">

    <div class="login-wrapper">

      <!-- Logo + 标题 -->
      <div class="logo-section">

        <div class="logo">
          <el-icon>
            <Reading />
          </el-icon>
        </div>

        <div class="title">
          知识管理平台
        </div>

        <div class="subtitle">
          登录以继续
        </div>

      </div>


      <!-- 登录卡片 -->
      <div class="login-card">

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          size="large"
        >

          <el-form-item prop="account">

            <el-input
              v-model="loginForm.account"
              placeholder="账号"
              clearable
              class="apple-input"
            >

              <template #prefix>
                <el-icon>
                  <User />
                </el-icon>
              </template>

            </el-input>

          </el-form-item>


          <el-form-item prop="password">

            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              show-password
              class="apple-input"
            >

              <template #prefix>
                <el-icon>
                  <Lock />
                </el-icon>
              </template>

            </el-input>

          </el-form-item>


          <el-button
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? "登录中..." : "继续" }}
          </el-button>

        </el-form>

      </div>


      <!-- footer -->
      <div class="login-footer">

        还没有账号？

        <router-link to="/register">
          创建账号
        </router-link>

      </div>

    </div>

  </div>
</template>

<script setup lang="ts">

import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { User, Lock, Reading } from '@element-plus/icons-vue'

import { login } from '@/api/user'

const router = useRouter()

const loginFormRef = ref()

const loading = ref(false)

const loginForm = reactive({
  account: '',
  password: ''
})

const loginRules = {

  account: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],

  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20字符', trigger: 'blur' }
  ]

}


const handleLogin = async () => {

  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid: boolean) => {

    if (!valid) return

    loading.value = true

    try {

      const res = await login({

        account: loginForm.account,
        password: loginForm.password

      })

      localStorage.setItem('token', res.data.token)

      localStorage.setItem(
        'userInfo',
        JSON.stringify(res.data.user)
      )

      ElMessage.success('登录成功')

      router.push('/')

    }
    catch (error) {

      console.error(error)

    }
    finally {

      loading.value = false

    }

  })

}

</script>

<style scoped>

/* 整体背景 */

.login-page {

  height: 100vh;

  display: flex;
  justify-content: center;
  align-items: center;

  background:
    radial-gradient(circle at 20% 20%, #f5f5f7, #e5e5ea);

}


/* 主容器 */

.login-wrapper {

  width: 360px;

  text-align: center;

}


/* Logo */

.logo {

  width: 64px;
  height: 64px;

  margin: 0 auto 20px auto;

  border-radius: 16px;

  background: white;

  display: flex;
  justify-content: center;
  align-items: center;

  font-size: 32px;

  box-shadow:
    0 4px 20px rgba(0,0,0,0.08);

}


/* 标题 */

.title {

  font-size: 26px;

  font-weight: 600;

  color: #1d1d1f;

}


/* 副标题 */

.subtitle {

  margin-top: 5px;

  font-size: 14px;

  color: #6e6e73;

}


/* 登录卡片 */

.login-card {

  margin-top: 30px;

  padding: 28px;

  border-radius: 18px;

  background: rgba(255,255,255,0.7);

  backdrop-filter: blur(20px);

  border: 1px solid rgba(0,0,0,0.05);

}


/* 输入框 */

.apple-input :deep(.el-input__wrapper) {

  border-radius: 12px;

  border: 1px solid rgba(0,0,0,0.08);

  background: rgba(255,255,255,0.9);

  box-shadow: none;

  transition: all 0.2s ease;

}


.apple-input :deep(.el-input__wrapper:hover) {

  border-color: rgba(0,0,0,0.2);

}


.apple-input :deep(.is-focus) {

  border-color: #0071e3;

  box-shadow:
    0 0 0 3px rgba(0,113,227,0.15);

}


/* 按钮 */

.login-button {

  width: 100%;

  height: 44px;

  margin-top: 10px;

  border-radius: 12px;

  border: none;

  background: #0071e3;

  color: white;

  font-size: 16px;

  font-weight: 500;

  transition: all 0.2s ease;

}


.login-button:hover {

  background: #0077ed;

}


.login-button:active {

  transform: scale(0.98);

}


/* footer */

.login-footer {

  margin-top: 20px;

  font-size: 14px;

  color: #6e6e73;

}


.login-footer a {

  color: #0071e3;

  margin-left: 5px;

  text-decoration: none;

}

.login-footer a:hover {

  text-decoration: underline;

}

</style>