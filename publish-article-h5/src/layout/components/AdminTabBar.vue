<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeTab = computed(() => route.path)

const tabs = [
  { path: '/admin/article', label: '文章', icon: '📄' },
  { path: '/admin/settings', label: '设置', icon: '⚙️' },
]
</script>

<template>
  <el-menu
    :key="activeTab"
    :default-active="activeTab"
    mode="horizontal"
    router
    class="admin-tab-bar"
    :ellipsis="false"
  >
    <el-menu-item v-for="tab in tabs" :key="tab.path" :index="tab.path">
      <span class="tab-icon">{{ tab.icon }}</span>
      <span>{{ tab.label }}</span>
    </el-menu-item>
  </el-menu>
</template>

<style scoped>
.admin-tab-bar {
  height: 56px;
  border-bottom: none;
  justify-content: space-around;
}

.admin-tab-bar :deep(.el-menu-item) {
  flex: 1;
  flex-direction: column;
  justify-content: center;
  line-height: 1.2;
  height: 56px;
  padding: 6px 0 0;
  font-size: 12px;
}

.tab-icon {
  font-size: 18px;
  margin-bottom: 2px;
}
</style>
