<template>
  <div class="manage-logs-page">
    <h2>管理员日志管理</h2>

    <div class="filters">
      <label>
        按类型筛选:
        <select v-model="filterType">
          <option value="">全部</option>
          <option value="LOGIN">登录</option>
          <option value="OPERATION">操作</option>
        </select>
      </label>

      <label>
        按用户名筛选:
        <input type="text" v-model="filterUser" placeholder="输入用户名">
      </label>
      <button @click="applyFilter">筛选</button>
      <button @click="loadLogs">刷新</button>
    </div>

    <div class="table-container">
      <table id="log-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>角色</th>
            <th>操作</th>
            <th>类型</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in displayedLogs" :key="log.id">
            <td>{{ log.id }}</td>
            <td>{{ log.username }}</td>
            <td>{{ log.role }}</td>
            <td>{{ log.action }}</td>
            <td>{{ log.type }}</td>
            <td>{{ log.time }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <router-link to="/admin/dashboard" class="back-link">返回后台主页</router-link>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { fetchLogs, filterLogs } from '../assets/js/manage-logs.js'

const token = localStorage.getItem("token")
const logs = ref([])
const filterType = ref('')
const filterUser = ref('')

const displayedLogs = computed(() => filterLogs(logs.value, filterType.value, filterUser.value))

function applyFilter() {
}

async function loadLogs() {
  await fetchLogs(token, fetchedLogs => {
    logs.value = fetchedLogs
  })
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
@import "/src/assets/css/manage-logs.css";
</style>