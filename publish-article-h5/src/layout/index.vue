<script setup lang="ts">
import AdminNavbar from './components/AdminNavbar.vue'
import AdminTabBar from './components/AdminTabBar.vue'
</script>

<template>
  <div class="min-h-screen bg-[#eef0f3]">
    <el-container class="admin-layout h5-shell min-h-screen bg-[#f5f7fa] shadow-sm">
      <el-header height="50px" class="admin-header">
        <AdminNavbar />
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>

      <el-footer height="56px" class="admin-footer">
        <AdminTabBar />
      </el-footer>
    </el-container>
  </div>
</template>

<style scoped>
.admin-layout {
  flex-direction: column;
}

.admin-header {
  padding: 0;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.admin-main {
  padding: 12px;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.admin-footer {
  padding: 0;
  background: #fff;
  border-top: 1px solid #ebeef5;
}
</style>
