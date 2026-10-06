import type { Router } from 'vue-router'

import { SITE_NAME } from '@/config/site'

export function setupTitleGuard(router: Router) {
  router.afterEach((to) => {
    const pageTitle = to.meta.title as string | undefined
    document.title = pageTitle ? `${pageTitle} - ${SITE_NAME}` : SITE_NAME
  })
}
