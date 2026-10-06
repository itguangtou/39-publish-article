import { notify } from '@/utils/feedback'

import router from '@/router'
import { useUserStore } from '@/store/modules/user'

let isHandlingUnauthorized = false

export function handleUnauthorized() {
  if (isHandlingUnauthorized) return
  isHandlingUnauthorized = true

  const userStore = useUserStore()
  userStore.clearToken()

  notify.warning('登录已过期，请重新登录')

  const currentPath = router.currentRoute.value.fullPath
  if (!currentPath.startsWith('/admin/login')) {
    router.push({
      path: '/admin/login',
      query: { redirect: currentPath },
    })
  }

  window.setTimeout(() => {
    isHandlingUnauthorized = false
  }, 1000)
}
