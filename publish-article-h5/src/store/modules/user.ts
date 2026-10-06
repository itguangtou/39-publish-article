import { defineStore } from 'pinia'
import { ref } from 'vue'

import { TOKEN_KEY } from '@/enums/cacheEnum'

export const useUserStore = defineStore(
  'user',
  () => {
    const token = ref<string>('')

    function setToken(value: string) {
      token.value = value
    }

    function clearToken() {
      token.value = ''
    }

    return { token, setToken, clearToken }
  },
  {
    persist: {
      key: TOKEN_KEY,
      pick: ['token'],
    },
  },
)
