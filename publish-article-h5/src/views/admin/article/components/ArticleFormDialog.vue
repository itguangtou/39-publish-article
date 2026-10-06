<script setup lang="ts">
import type { UploadFile, UploadUserFile } from 'element-plus'

import { notify } from '@/utils/feedback'
import { computed, reactive, ref, watch } from 'vue'

import type { ArticleItem, ArticleSubmitPayload, ImageMetaItem } from '@/api/article/types'
import { RECOMMENDED_IMAGE_WIDTH } from '@/config/site'
import { toRelativeImagePath } from '@/utils/imagePath'

const props = defineProps<{
  visible: boolean
  article?: ArticleItem | null
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  submit: [payload: ArticleSubmitPayload]
}>()

const formRef = ref()
const fileList = ref<UploadUserFile[]>([])
const isEdit = computed(() => Boolean(props.article?.id))

const form = reactive({
  title: '',
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  images: [
    {
      validator: (_rule: unknown, _value: unknown, callback: (error?: Error) => void) => {
        if (fileList.value.length > 0) {
          callback()
          return
        }
        callback(new Error('请上传至少一张正文图片'))
      },
      trigger: 'change',
    },
  ],
}

watch(
  () => props.visible,
  (open) => {
    if (!open) return
    if (props.article) {
      form.title = props.article.title
      fileList.value = (props.article.images ?? []).map((url, index) => ({
        name: `image-${index + 1}`,
        url,
      }))
    } else {
      form.title = ''
      fileList.value = []
    }
  },
)

function handleImageChange(_file: UploadFile, files: UploadFile[]) {
  fileList.value = files
  formRef.value?.validateField('images')
}

function handleImageRemove(_file: UploadFile, files: UploadFile[]) {
  fileList.value = files
  formRef.value?.validateField('images')
}

function beforeUpload() {
  return false
}

function handleClose() {
  emit('update:visible', false)
}

function buildSubmitPayload(): ArticleSubmitPayload {
  const imageMeta: ImageMetaItem[] = []
  const newFiles: File[] = []

  for (const file of fileList.value) {
    if (file.raw) {
      imageMeta.push({ type: 'new', index: newFiles.length })
      newFiles.push(file.raw)
    } else if (file.url) {
      imageMeta.push({ type: 'keep', path: toRelativeImagePath(file.url) })
    }
  }

  return {
    title: form.title,
    imageMeta,
    newFiles,
  }
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    emit('submit', buildSubmitPayload())
  } catch {
    notify.warning('请完善表单信息')
  }
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="isEdit ? '编辑文章' : '新建文章'"
    width="92%"
    align-center
    destroy-on-close
    class="article-form-dialog"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="left"
      label-width="72px"
      class="article-form"
    >
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="form.title"
          type="textarea"
          :rows="4"
          placeholder="请输入文章标题"
          maxlength="100"
          show-word-limit
          resize="none"
          class="title-textarea"
        />
      </el-form-item>

      <el-form-item label="正文图片" prop="images">
        <div class="form-field-content">
          <p class="field-hint">建议正文长图宽度 {{ RECOMMENDED_IMAGE_WIDTH }}px，高度不限</p>
          <el-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            accept="image/*"
            multiple
            :auto-upload="false"
            :before-upload="beforeUpload"
            class="article-uploader"
            @change="handleImageChange"
            @remove="handleImageRemove"
          >
            <div class="upload-trigger">
              <span class="text-xl text-gray-400">+</span>
              <span class="mt-0.5 text-xs text-gray-400">上传</span>
            </div>
          </el-upload>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button size="large" class="footer-btn" @click="handleClose">取消</el-button>
        <el-button type="primary" size="large" class="footer-btn" @click="handleSubmit">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.article-form :deep(.el-form-item) {
  align-items: flex-start;
  margin-bottom: 20px;
}

.article-form :deep(.el-form-item__label) {
  height: auto;
  line-height: 1.5;
  padding-top: 8px;
  color: #606266;
  font-weight: 500;
}

.article-form :deep(.el-form-item__content) {
  flex: 1;
  min-width: 0;
}

.form-field-content {
  width: 100%;
}

.field-hint {
  margin-bottom: 10px;
  font-size: 12px;
  line-height: 1.5;
  color: #909399;
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.article-uploader :deep(.el-upload-list--picture-card) {
  --el-upload-list-picture-card-size: 80px;
}

.article-uploader :deep(.el-upload--picture-card) {
  width: 80px;
  height: 80px;
}

.title-textarea :deep(.el-textarea__inner) {
  min-height: 100px;
  padding: 10px 12px;
  font-size: 15px;
  line-height: 1.6;
}

.dialog-footer {
  display: flex;
  gap: 12px;
}

.footer-btn {
  flex: 1;
  margin: 0;
}
</style>

<style>
.article-form-dialog {
  max-width: 420px;
  margin: 0 auto;
  border-radius: 12px;
}

.article-form-dialog .el-dialog__header {
  text-align: center;
  margin-right: 0;
  padding-bottom: 8px;
}

.article-form-dialog .el-dialog__title {
  font-weight: 600;
}

.article-form-dialog .el-dialog__body {
  padding: 12px 16px 8px;
}

.article-form-dialog .el-dialog__footer {
  padding: 12px 16px 16px;
}
</style>
