import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  createArticleApi,
  deleteArticleApi,
  getArticleListApi,
  updateArticleApi,
} from '@/api/article'
import type { ArticleItem, ArticleSubmitPayload } from '@/api/article/types'
import { buildPromotionLink } from '@/utils/promotionLink'

function normalizeArticle(item: ArticleItem): ArticleItem {
  return {
    ...item,
    promotionLink: buildPromotionLink(item.slug),
    images: item.images ?? (item.coverUrl ? [item.coverUrl] : []),
  }
}

function buildArticleFormData(payload: ArticleSubmitPayload, isEdit: boolean): FormData {
  const formData = new FormData()
  formData.append('title', payload.title)
  if (isEdit) {
    formData.append('imageMeta', JSON.stringify(payload.imageMeta))
    payload.newFiles.forEach((file) => formData.append('images', file))
  } else {
    payload.newFiles.forEach((file) => formData.append('images', file))
  }
  return formData
}

export const useArticleAdminStore = defineStore('articleAdmin', () => {
  const articles = ref<ArticleItem[]>([])
  const total = ref(0)
  const loading = ref(false)

  async function fetchArticles(params: { current: number; size: number; keyword?: string }) {
    loading.value = true
    try {
      const res = await getArticleListApi({
        current: params.current,
        size: params.size,
        keyword: params.keyword || undefined,
      })
      articles.value = res.data.records.map(normalizeArticle)
      total.value = res.data.total
    } finally {
      loading.value = false
    }
  }

  async function addArticle(payload: ArticleSubmitPayload) {
    const formData = buildArticleFormData(payload, false)
    const res = await createArticleApi(formData)
    return normalizeArticle(res.data)
  }

  async function updateArticle(id: number, payload: ArticleSubmitPayload) {
    const formData = buildArticleFormData(payload, true)
    const res = await updateArticleApi(id, formData)
    return normalizeArticle(res.data)
  }

  async function removeArticle(id: number) {
    await deleteArticleApi(id)
  }

  return {
    articles,
    total,
    loading,
    fetchArticles,
    addArticle,
    updateArticle,
    removeArticle,
  }
})
