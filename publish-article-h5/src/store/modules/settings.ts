import { defineStore } from 'pinia'
import { ref } from 'vue'

import { getSystemSettingsApi, updateSystemSettingsApi } from '@/api/settings'
import type { SystemSettings } from '@/api/settings/types'

const DEFAULT_SETTINGS: SystemSettings = {
  avatar: '/avatar-default.svg',
  nickname: '超哥评球',
  intro: '爱足球，爱生活！',
}

export const useSettingsStore = defineStore('settings', () => {
  const settings = ref<SystemSettings>({ ...DEFAULT_SETTINGS })
  const loaded = ref(false)

  async function fetchSettings() {
    const res = await getSystemSettingsApi()
    settings.value = res.data
    loaded.value = true
  }

  async function saveSettings(payload: {
    nickname: string
    intro: string
    avatarFile?: File | null
  }) {
    const formData = new FormData()
    formData.append('nickname', payload.nickname)
    formData.append('intro', payload.intro)
    if (payload.avatarFile) {
      formData.append('avatar', payload.avatarFile)
    }
    const res = await updateSystemSettingsApi(formData)
    settings.value = res.data
  }

  return { settings, loaded, fetchSettings, saveSettings }
})
