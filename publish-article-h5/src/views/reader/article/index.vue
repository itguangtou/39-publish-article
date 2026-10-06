<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

import { getArticleBySlugApi } from '@/api/article'
import type { ArticleDetail } from '@/api/article/types'
import AuthorProfile from '@/components/business/AuthorProfile.vue'
import ArticleDisclaimer from '@/components/business/ArticleDisclaimer.vue'
import ArticleImagePlaceholder from '@/components/common/ArticleImagePlaceholder.vue'
import { useSettingsStore } from '@/store/modules/settings'
import { formatDate } from '@/utils/date'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const route = useRoute()
const settingsStore = useSettingsStore()
const articleDetail = ref<ArticleDetail | null>(null)
const loading = ref(false)

const slug = computed(() => String(route.params.slug || ''))

const article = computed(() => ({
  slug: slug.value,
  title: articleDetail.value?.title ?? (loading.value ? '加载中...' : '文章不存在或尚未发布'),
  images: articleDetail.value?.images ?? [],
  updatedAt: articleDetail.value?.createdAt ?? '',
  contentUpdatedAt: articleDetail.value?.createdAt ?? '',
}))

const publishedAgo = computed(() => {
  if (!article.value.contentUpdatedAt) return ''
  return dayjs(article.value.contentUpdatedAt).fromNow()
})

const author = computed(() => {
  if (articleDetail.value?.author) {
    return articleDetail.value.author
  }
  return settingsStore.settings
})

async function loadArticle() {
  if (!slug.value) return
  loading.value = true
  try {
    const res = await getArticleBySlugApi(slug.value)
    articleDetail.value = res.data
  } catch {
    articleDetail.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  settingsStore.fetchSettings().catch(() => undefined)
})

watch(slug, () => {
  loadArticle()
}, { immediate: true })
</script>

<template>
  <div class="min-h-screen bg-[#eef0f3]">
    <div class="h5-shell min-h-screen bg-white shadow-sm">
    <AuthorProfile
      :avatar="author.avatar"
      :nickname="author.nickname"
      :intro="author.intro"
    />

    <section class="border-b border-gray-100 px-4 py-3">
      <h1 class="text-lg font-bold leading-snug text-gray-900">{{ article.title }}</h1>
      <p v-if="publishedAgo && article.updatedAt" class="mt-2 text-xs text-gray-400">
        {{ publishedAgo }} · 更新于{{ formatDate(article.contentUpdatedAt) }}
      </p>
    </section>

    <section class="border-b border-gray-100 px-4 py-3">
      <h2 class="text-lg font-bold text-gray-900">正文</h2>
    </section>

    <section class="mt-3">
      <template v-if="article.images.length">
        <img
          v-for="(img, index) in article.images"
          :key="index"
          :src="img"
          :alt="`${article.title} - 图片${index + 1}`"
          class="block h-auto w-full max-w-full"
        />
      </template>
      <ArticleImagePlaceholder v-else-if="!loading" />
    </section>

    <ArticleDisclaimer
      v-if="article.updatedAt"
      :slug="article.slug"
      :updated-at="formatDate(article.updatedAt)"
    />
    </div>
  </div>
</template>

<style scoped>
:global(body) {
  overflow-x: hidden;
  background-color: #eef0f3;
}
</style>
