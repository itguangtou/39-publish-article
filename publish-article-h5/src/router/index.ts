import { createRouter, createWebHistory } from 'vue-router'

import { setupPageLoadingGuard } from './guard/pageLoadingGuard'
import { setupPermissionGuard } from './guard/permissionGuard'
import { setupTitleGuard } from './guard/titleGuard'
import { basicRoutes } from './routes/basic'
import { adminRoutes } from './routes/modules/admin'
import { readerRoutes } from './routes/modules/reader'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...readerRoutes, ...adminRoutes, ...basicRoutes],
})

setupPermissionGuard(router)
setupPageLoadingGuard(router)
setupTitleGuard(router)

export default router
