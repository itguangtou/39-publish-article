<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'

import { useSettingsStore } from '@/store/modules/settings'
import { notify } from '@/utils/feedback'
import { isUnauthorizedError } from '@/utils/http/requestError'

const settingsStore = useSettingsStore()
const saving = ref(false)
const avatarFile = ref<File | null>(null)

const form = reactive({
  avatar: settingsStore.settings.avatar,
  nickname: settingsStore.settings.nickname,
  intro: settingsStore.settings.intro,
})

watch(
  () => settingsStore.settings,
  (value) => {
    form.avatar = value.avatar
    form.nickname = value.nickname
    form.intro = value.intro
  },
  { deep: true },
)

onMounted(async () => {
  try {
    await settingsStore.fetchSettings()
    form.avatar = settingsStore.settings.avatar
    form.nickname = settingsStore.settings.nickname
    form.intro = settingsStore.settings.intro
  } catch (error) {
    if (!isUnauthorizedError(error)) {
      notify.error(error instanceof Error ? error.message : '加载设置失败')
    }
  }
})

const ALLOWED_AVATAR_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
const ALLOWED_AVATAR_EXT = ['jpg', 'jpeg', 'png', 'gif', 'webp']

function handleAvatarChange(file: File) {
  const ext = file.name.split('.').pop()?.toLowerCase() ?? ''
  const typeOk = !file.type || ALLOWED_AVATAR_TYPES.includes(file.type)
  const extOk = ALLOWED_AVATAR_EXT.includes(ext)
  if (!typeOk && !extOk) {
    notify.warning('仅支持 JPG、PNG、GIF、WebP 格式')
    return false
  }
  if (file.size > 20 * 1024 * 1024) {
    notify.warning('头像大小不能超过 20MB')
    return false
  }
  avatarFile.value = file
  form.avatar = URL.createObjectURL(file)
  return false
}

async function handleSubmit() {
  if (!form.nickname.trim()) {
    notify.warning('请输入昵称')
    return
  }

  saving.value = true
  const uploadingAvatar = avatarFile.value !== null

  try {
    await settingsStore.saveSettings({
      nickname: form.nickname.trim(),
      intro: form.intro.trim(),
      avatarFile: avatarFile.value,
    })
    avatarFile.value = null
    form.avatar = settingsStore.settings.avatar
    notify.success(uploadingAvatar ? '头像和系统设置已保存' : '系统设置已保存')
  } catch (error) {
    if (!isUnauthorizedError(error)) {
      notify.error(error instanceof Error ? error.message : '保存失败，请稍后重试')
    }
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="space-y-3">
    <el-card shadow="never">
      <el-form label-position="top">
        <el-form-item label="头像">
          <div class="flex items-center gap-4">
            <el-avatar :size="64" :src="form.avatar" />
            <el-upload :show-file-list="false" accept="image/*" :before-upload="handleAvatarChange">
              <el-button size="small">选择头像</el-button>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="昵称">
          <el-input
            v-model="form.nickname"
            placeholder="请输入昵称"
            maxlength="30"
            show-word-limit
            size="large"
          />
        </el-form-item>

        <el-form-item label="简介">
          <el-input
            v-model="form.intro"
            type="textarea"
            :rows="3"
            placeholder="请输入简介"
            maxlength="100"
            show-word-limit
            size="large"
          />
        </el-form-item>

        <div class="flex flex-col gap-2">
          <el-button
            type="primary"
            size="large"
            class="w-full"
            :loading="saving"
            @click="handleSubmit"
          >
            保存设置
          </el-button>
        </div>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <span class="text-sm font-medium">读者端预览</span>
      </template>
      <div class="flex items-start gap-3 rounded-lg bg-gray-50 p-3">
        <el-avatar :size="44" :src="form.avatar" />
        <div>
          <p class="font-semibold text-gray-900">{{ form.nickname || '昵称' }}</p>
          <p class="mt-0.5 text-xs text-gray-500">简介：{{ form.intro || '暂无简介' }}</p>
        </div>
      </div>
    </el-card>
  </div>
</template>
