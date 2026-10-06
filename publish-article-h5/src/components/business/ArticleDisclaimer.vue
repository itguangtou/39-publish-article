<script setup lang="ts">
import ArticleStickyTip from './ArticleStickyTip.vue'

defineProps<{
  slug: string
  updatedAt?: string
}>()
</script>

<template>
  <footer class="border-t border-gray-100 bg-[#f7f7f7] px-4 pt-5 pb-4">
    <p v-if="updatedAt" class="mb-3 text-center text-xs text-gray-400">更新于{{ updatedAt }}</p>

    <p class="text-justify text-xs leading-6 text-gray-500">
      免责声明：文章内容仅代表作者个人观点，仅供参考。平台仅提供信息存储服务，不对图片与文字的及时性、准确性、完整性做任何保证。用户应独立决策并自行承担风险。
    </p>

    <ArticleStickyTip :slug="slug" />

    <p class="mt-3 text-center text-sm font-medium text-[#e54d42]">
      作者已将本文章设置免费，您无需付费！
    </p>
  </footer>
</template>
