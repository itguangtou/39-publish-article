<script setup lang="ts">
import { notify } from '@/utils/feedback'
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { loginApi } from '@/api/auth'
import { SITE_NAME } from '@/config/site'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'chaoge666',
})

async function handleLogin() {
  loading.value = true
  try {
    const res = await loginApi(form)
    userStore.setToken(res.data.token)
    notify.success('登录成功')
    const redirect = (route.query.redirect as string) || '/admin/article'
    router.push(redirect)
  } catch (error) {
    notify.error(error instanceof Error ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-[#eef0f3]">
    <div class="mx-auto flex min-h-screen h5-shell flex-col bg-gradient-to-b from-white to-[#f5f7fa] px-6 shadow-sm">
    <div class="flex flex-1 flex-col justify-center py-10">
      <!-- Logo & 品牌 -->
      <div class="mb-10 text-center">
        <img src="/logo.svg" alt="logo" class="mx-auto h-20 w-20" />
        <h1 class="mt-4 text-2xl font-bold text-gray-900">{{ SITE_NAME }}</h1>
        <p class="mt-2 text-sm text-gray-500">文章发布 · 管理后台</p>
      </div>

      <!-- 登录表单 -->
      <el-card shadow="never" class="login-card">
        <el-form :model="form" label-position="top" @submit.prevent="handleLogin">
          <el-form-item label="账号">
            <el-input
              v-model="form.username"
              placeholder="请输入账号"
              size="large"
              clearable
            />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item class="!mb-0">
            <el-button
              type="primary"
              size="large"
              class="w-full"
              :loading="loading"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <p class="pb-6 text-center text-xs text-gray-400">© {{ SITE_NAME }}</p>
    </div>
  </div>
</template>

<style scoped>
.login-card {
  border-radius: 12px;
}
</style>
