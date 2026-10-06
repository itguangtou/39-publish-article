<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import { useAuth } from '@/composables/business/useAuth'
import { SITE_NAME } from '@/config/site'

const route = useRoute()
const { logout } = useAuth()

const pageTitle = computed(() => (route.meta.title as string) || '管理后台')
</script>

<template>
  <div class="flex h-full items-center justify-between px-4">
    <div class="min-w-0 flex-1">
      <p class="truncate text-base font-semibold text-gray-900">{{ pageTitle }}</p>
      <p class="truncate text-xs text-gray-400">{{ SITE_NAME }}</p>
    </div>

    <el-dropdown trigger="click">
      <el-button circle>
        <span class="text-base">☰</span>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>
