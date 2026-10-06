<script setup lang="ts">
import { ElMessageBox } from 'element-plus'

import { notify } from '@/utils/feedback'
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from 'vue'

import { useArticleAdminStore } from '@/store/modules/articleAdmin'
import { copyText } from '@/utils/copyText'
import { formatDate } from '@/utils/date'
import { isUnauthorizedError } from '@/utils/http/requestError'

import type { ArticleItem, ArticleSubmitPayload } from '@/api/article/types'

import ArticleFormDialog from './components/ArticleFormDialog.vue'

const articleStore = useArticleAdminStore()

const dialogVisible = ref(false)
const editingArticle = ref<ArticleItem | null>(null)
const searchExpanded = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const tableMaxHeight = ref(280)

const listSectionRef = ref<HTMLElement>()
const searchSectionRef = ref<HTMLElement>()

const searchForm = reactive({
  keyword: '',
})

const total = computed(() => articleStore.total)
const pagedArticles = computed(() => articleStore.articles)
const submitting = ref(false)

async function loadArticles() {
  try {
    await articleStore.fetchArticles({
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.keyword.trim(),
    })
  } catch (error) {
    if (!isUnauthorizedError(error)) {
      notify.error(error instanceof Error ? error.message : '加载文章列表失败')
    }
  }
}

watch([currentPage, pageSize], () => {
  loadArticles()
})

watch(searchExpanded, () => {
  nextTick(updateTableHeight)
})

function updateTableHeight() {
  const listEl = listSectionRef.value
  if (!listEl) return

  const listHeader = listEl.querySelector('.section-header') as HTMLElement | null
  const pagination = listEl.querySelector('.table-pagination') as HTMLElement | null
  const headerHeight = listHeader?.offsetHeight ?? 56
  const paginationHeight = pagination?.offsetHeight ?? 52
  const listHeight = listEl.clientHeight
  const nextHeight = listHeight - headerHeight - paginationHeight

  tableMaxHeight.value = Math.max(160, nextHeight)
}

onMounted(() => {
  loadArticles()
  nextTick(updateTableHeight)
  window.addEventListener('resize', updateTableHeight)
})

onUnmounted(() => {
  window.removeEventListener('resize', updateTableHeight)
})

function toggleSearch() {
  searchExpanded.value = !searchExpanded.value
}

function handleSearch() {
  currentPage.value = 1
  loadArticles()
}

function handleResetSearch() {
  searchForm.keyword = ''
  currentPage.value = 1
  loadArticles()
}

function openCreate() {
  editingArticle.value = null
  dialogVisible.value = true
}

function openEdit(row: ArticleItem) {
  editingArticle.value = row
  dialogVisible.value = true
}

function handleRowCopy(row: ArticleItem) {
  handleCopy(row.promotionLink)
}

function handleRowPreview(row: ArticleItem) {
  handlePreview(row.slug)
}

function handleRowEdit(row: ArticleItem) {
  openEdit(row)
}

function handleRowDelete(row: ArticleItem) {
  handleDelete(row)
}

async function handleSubmit(payload: ArticleSubmitPayload) {
  submitting.value = true
  try {
    if (editingArticle.value) {
      await articleStore.updateArticle(editingArticle.value.id, payload)
      notify.success('文章已更新')
    } else {
      await articleStore.addArticle(payload)
      currentPage.value = 1
      notify.success('文章已创建，推广链接已自动生成')
    }
    dialogVisible.value = false
    await loadArticles()
  } catch (error) {
    if (!isUnauthorizedError(error)) {
      notify.error(error instanceof Error ? error.message : '保存失败')
    }
  } finally {
    submitting.value = false
  }
}

async function handleCopy(link: string) {
  const ok = await copyText(link)
  notify[ok ? 'success' : 'error'](ok ? '推广链接已复制' : '复制失败，请手动复制')
}

function handlePreview(slug: string) {
  window.open(`/article/${slug}`, '_blank')
}

function handleDelete(row: ArticleItem) {
  ElMessageBox.confirm(`确定删除文章「${row.title}」吗？`, '提示', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    customClass: 'admin-message-box',
  })
    .then(async () => {
      try {
        await articleStore.removeArticle(row.id)
        notify.success('已删除')
        await loadArticles()
        nextTick(updateTableHeight)
      } catch (error) {
        if (!isUnauthorizedError(error)) {
          notify.error(error instanceof Error ? error.message : '删除失败')
        }
      }
    })
    .catch(() => undefined)
}

</script>

<template>
  <div class="article-page">
    <!-- 搜索区域 -->
    <section ref="searchSectionRef" class="section-block search-section">
      <div class="section-header" @click="toggleSearch">
        <div class="flex items-center gap-2">
          <span class="section-title">搜索筛选</span>
          <el-tag v-if="searchForm.keyword" size="small" type="warning">
            已筛选
          </el-tag>
        </div>
        <span class="section-action">{{ searchExpanded ? '收起' : '展开' }}</span>
      </div>

      <div v-show="searchExpanded" class="section-body">
        <el-form :model="searchForm" label-position="top">
          <el-form-item label="文章标题">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入标题关键词"
              clearable
              size="large"
            />
          </el-form-item>

          <div class="grid grid-cols-2 gap-2">
            <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
            <el-button size="large" @click="handleResetSearch">重置</el-button>
          </div>
        </el-form>
      </div>
    </section>

    <!-- 列表区域：固定贴近 tabbar -->
    <section ref="listSectionRef" class="section-block list-section">
      <div class="section-header section-header-static">
        <div>
          <span class="section-title">文章列表</span>
          <p class="mt-1 text-xs text-gray-400">共 {{ total }} 条</p>
        </div>
        <el-button type="primary" size="small" @click="openCreate">新建</el-button>
      </div>

      <div class="section-body section-body-table">
        <div class="table-scroll">
          <el-table
            v-loading="articleStore.loading"
            :data="pagedArticles"
            :height="tableMaxHeight"
            stripe
            empty-text="暂无符合条件的文章"
            class="article-table"
          >
            <el-table-column
              prop="title"
              label="标题"
              min-width="120"
              show-overflow-tooltip
              class-name="title-col"
            />

            <el-table-column label="创建时间" width="110" show-overflow-tooltip>
              <template #default="{ row }">
                {{ formatDate(row.createdAt, 'MM-DD HH:mm') }}
              </template>
            </el-table-column>

            <el-table-column label="操作" width="72" fixed="right" align="center">
              <template #default="{ row }">
                <el-dropdown trigger="click">
                  <el-button link type="primary" size="small">操作</el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="handleRowCopy(row as ArticleItem)">
                        复制链接
                      </el-dropdown-item>
                      <el-dropdown-item @click="handleRowPreview(row as ArticleItem)">
                        预览
                      </el-dropdown-item>
                      <el-dropdown-item @click="handleRowEdit(row as ArticleItem)">
                        编辑
                      </el-dropdown-item>
                      <el-dropdown-item divided @click="handleRowDelete(row as ArticleItem)">
                        <span class="text-red-500">删除</span>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="table-pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[5, 10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            small
            background
          />
        </div>
      </div>
    </section>

    <ArticleFormDialog
      v-model:visible="dialogVisible"
      :article="editingArticle"
      @submit="handleSubmit"
    />
  </div>
</template>

<style scoped>
.article-page {
  display: flex;
  flex-direction: column;
  height: calc(100dvh - 50px - 56px - 24px);
  min-height: 0;
  gap: 12px;
}

.search-section {
  flex-shrink: 0;
}

.list-section {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.section-block {
  overflow: hidden;
  border-radius: 10px;
  background: #fff;
  border: 1px solid #ebeef5;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  user-select: none;
  flex-shrink: 0;
}

.section-header-static {
  cursor: default;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.section-action {
  font-size: 13px;
  color: #909399;
}

.section-body {
  padding: 16px;
}

.section-body-table {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 0;
}

.table-scroll {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.article-table {
  width: 100%;
}

.article-table :deep(.el-table__header table),
.article-table :deep(.el-table__body table) {
  table-layout: fixed;
  width: 100%;
}

.table-pagination {
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 12px;
  border-top: 1px solid #f0f2f5;
  background: #fafafa;
}

.table-pagination :deep(.el-pagination) {
  flex-wrap: wrap;
  justify-content: center;
}

.article-table :deep(.title-col .cell) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
