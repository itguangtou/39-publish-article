import type { Router } from 'vue-router'

import { useUserStore } from '@/store/modules/user'

export function setupPermissionGuard(router: Router) {
  router.beforeEach((to) => {
    if (!to.meta.auth) return true

    const userStore = useUserStore()
    if (!userStore.token) {
      return { path: '/admin/login', query: { redirect: to.fullPath } }
    }

    return true
  })
}
