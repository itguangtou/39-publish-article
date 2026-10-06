<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { getStorage, setStorage } from '@/utils/storage'

const props = defineProps<{
  slug: string
}>()

const visible = ref(false)

function storageKey() {
  return `article_tip_dismissed_${props.slug}`
}

onMounted(() => {
  visible.value = !getStorage<boolean>(storageKey())
})

function close() {
  visible.value = false
  setStorage(storageKey(), true)
}
</script>

<template>
  <div
    v-if="visible"
    class="mt-4 flex items-center gap-2 rounded border border-[#f0dfc0] bg-[#fff8e8] px-3 py-2.5"
  >
    <span class="shrink-0 text-sm text-[#f5a623]">⚠</span>
    <p class="flex-1 text-xs leading-5 text-[#b8860b]">
      作者解读观点仅供娱乐参考，本站非购彩平台！
    </p>
    <button
      type="button"
      class="shrink-0 px-1 text-lg leading-none text-gray-400"
      aria-label="关闭提示"
      @click="close"
    >
      ×
    </button>
  </div>
</template>
