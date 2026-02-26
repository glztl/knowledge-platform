<!-- src/views/register/Register.vue -->
<template>
  <div class="register-container">
    <div class="background-blur"></div>

    <el-card class="register-card" shadow="never">
      <div class="logo-area">
        <el-icon class="logo-icon"><UserFilled /></el-icon>
        <h1>创建账号</h1>
        <p>欢迎加入知识平台</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名"
            size="large"
            class="apple-input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="邮箱"
            size="large"
            class="apple-input"
          >
            <template #prefix>
              <el-icon><Mail /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            size="large"
            show-password
            class="apple-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            size="large"
            show-password
            class="apple-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-button
          class="register-button"
          :loading="loading"
          @click="handleRegister"
        >
          {{ loading ? "注册中..." : "注册" }}
        </el-button>
      </el-form>

      <div class="footer">
        已有账号？
        <router-link to="/login">立即登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import { User, Lock, Message, UserFilled } from "@element-plus/icons-vue"

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: "",
  email: "",
  password: "",
  confirmPassword: "",
})

const registerRules = reactive({
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度为3-20个字符", trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "邮箱格式错误", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码长度为6-20个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== registerForm.password)
          callback(new Error("两次密码不一致"))
        else callback()
      },
      trigger: "blur",
    },
  ],
})

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    loading.value = true

    try {
      ElMessage.success("注册成功")
      router.push("/login")
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>

/* ================= 背景 ================= */

.register-container {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  background:
    radial-gradient(circle at 20% 30%, #5b8cff 0%, transparent 40%),
    radial-gradient(circle at 80% 70%, #9b6bff 0%, transparent 40%),
    #f5f7fa;
}

.background-blur {
  position: absolute;
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #5b8cff, #9b6bff);
  filter: blur(120px);
  opacity: 0.3;
}


/* ================= 卡片 ================= */

.register-card {
  width: 420px;

  padding: 40px 32px;

  border-radius: 20px;

  background: rgba(255, 255, 255, 0.7);

  backdrop-filter: blur(20px);

  border: 1px solid rgba(255, 255, 255, 0.5);

  box-shadow:
    0 10px 40px rgba(0,0,0,0.08),
    0 2px 8px rgba(0,0,0,0.04);

  transition: all 0.3s ease;
}

.register-card:hover {
  transform: translateY(-3px);
  box-shadow:
    0 20px 60px rgba(0,0,0,0.12);
}


/* ================= Logo ================= */

.logo-area {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 42px;
  color: #409eff;
  margin-bottom: 10px;
}

.logo-area h1 {
  font-size: 26px;
  font-weight: 600;
  margin: 0;
}

.logo-area p {
  margin-top: 6px;
  color: #888;
  font-size: 14px;
}


/* ================= 输入框 ================= */

.apple-input :deep(.el-input__wrapper) {

  border-radius: 12px;

  background: rgba(255,255,255,0.8);

  box-shadow: none;

  border: 1px solid rgba(0,0,0,0.05);

  transition: all 0.2s;
}

.apple-input :deep(.el-input__wrapper:hover) {

  border: 1px solid rgba(0,0,0,0.15);
}

.apple-input :deep(.is-focus) {

  border: 1px solid #409eff;

  box-shadow:
    0 0 0 4px rgba(64,158,255,0.15);
}


/* ================= 按钮 ================= */

.register-button {

  width: 100%;

  height: 46px;

  border-radius: 12px;

  font-size: 16px;

  font-weight: 500;

  margin-top: 10px;

  border: none;

  background: linear-gradient(135deg,#5b8cff,#409eff);

  color: white;

  transition: all 0.2s;
}

.register-button:hover {

  transform: translateY(-1px);

  box-shadow:
    0 6px 20px rgba(64,158,255,0.4);
}


/* ================= footer ================= */

.footer {

  text-align: center;

  margin-top: 25px;

  font-size: 14px;

  color: #666;
}

.footer a {

  color: #409eff;

  text-decoration: none;

  font-weight: 500;
}

.footer a:hover {

  opacity: 0.7;
}

</style>