<script setup lang="ts">
import { storeToRefs } from 'pinia'

import { useAppStore } from '@/store/modules/app'

const appStore = useAppStore()
const { pageLoading } = storeToRefs(appStore)
</script>

<template>
  <div v-if="pageLoading" class="fixed inset-0 z-50 flex items-center justify-center bg-white/60">
    <span class="text-gray-500">加载中...</span>
  </div>
</template>
