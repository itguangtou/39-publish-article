import { ElMessageBox } from 'element-plus'

import { notify } from '@/utils/feedback'
import { useRouter } from 'vue-router'

import { useUserStore } from '@/store/modules/user'

export function useAuth() {
  const userStore = useUserStore()
  const router = useRouter()

  function logout() {
    ElMessageBox.confirm('确定退出登录吗？', '提示', {
      type: 'warning',
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      customClass: 'admin-message-box',
    })
      .then(() => {
        userStore.clearToken()
        notify.success('已退出登录')
        router.push('/admin/login')
      })
      .catch(() => undefined)
  }

  return {
    token: userStore.token,
    logout,
  }
}
