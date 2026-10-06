import type { Router } from 'vue-router'

import { useAppStore } from '@/store/modules/app'

export function setupPageLoadingGuard(router: Router) {
  router.beforeEach(() => {
    const appStore = useAppStore()
    appStore.setPageLoading(true)
    return true
  })

  router.afterEach(() => {
    const appStore = useAppStore()
    appStore.setPageLoading(false)
  })
}
