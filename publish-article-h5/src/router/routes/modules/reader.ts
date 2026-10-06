import type { RouteRecordRaw } from 'vue-router'

export const readerRoutes: RouteRecordRaw[] = [
  {
    path: '/article/:slug',
    name: 'ReaderArticle',
    component: () => import('@/views/reader/article/index.vue'),
    meta: { title: '文章详情' },
  },
]
