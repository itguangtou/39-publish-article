<script setup lang="ts">
defineProps<{
  avatar: string
  nickname: string
  intro: string
}>()
</script>

<template>
  <section class="flex items-start gap-3 border-b border-gray-100 px-4 py-4">
    <img :src="avatar" :alt="nickname" class="h-11 w-11 shrink-0 rounded-full object-cover" />
    <div class="min-w-0 flex-1">
      <p class="text-base font-semibold text-gray-900">{{ nickname }}</p>
      <p class="mt-0.5 truncate text-xs text-gray-500">简介：{{ intro }}</p>
    </div>
  </section>
</template>
