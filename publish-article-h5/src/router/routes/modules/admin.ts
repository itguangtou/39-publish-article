import type { RouteRecordRaw } from 'vue-router'

import Layout from '@/layout/index.vue'

export const adminRoutes: RouteRecordRaw[] = [
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true },
  },
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/article',
    meta: { title: '管理后台', auth: true },
    children: [
      {
        path: 'article',
        name: 'AdminArticle',
        component: () => import('@/views/admin/article/index.vue'),
        meta: { title: '文章管理', auth: true },
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/views/admin/settings/index.vue'),
        meta: { title: '系统设置', auth: true },
      },
    ],
  },
]
