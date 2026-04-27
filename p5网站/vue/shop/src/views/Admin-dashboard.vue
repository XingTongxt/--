<template>
  <div class="admin-dashboard">

    <h2>欢迎您，管理员</h2>
    <p ref="adminInfo">加载中...</p>

    <div id="menu">

      <h3>功能菜单</h3>

      <!-- 只有 SUPERADMIN 可以看到 -->
      <div
        v-if="role === 'SUPERADMIN'"
        class="menu-item"
        @click="goTo('/admin/manage-users', $event)"
      >
        用户管理
      </div>

      <div
        class="menu-item"
        @click="goTo('/admin/manage-items', $event)"
      >
        物品管理
      </div>

      <div
        v-if="role === 'SUPERADMIN'"
        class="menu-item"
        @click="goTo('/admin/manage-logs', $event)"
      >
        日志管理
      </div>

      <div
        class="menu-item"
        @click="goTo('/shop', $event)"
      >
        返回商店
      </div>

    </div>

    <button @click="logout">
      退出登录
    </button>

  </div>
</template>

<script setup>

import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"

import {
  initAdminDashboard,
  highlightMenu,
  adminLogout
} from "../assets/js/admin-dashboard.js"


const router = useRouter()

const adminInfo = ref(null)
const role = ref("")


onMounted(async () => {

  const data = await initAdminDashboard(adminInfo.value, router)

  if (data) {

    role.value = data.role

    localStorage.setItem("adminRole", data.role)

  }

})


function goTo(path, event) {

  highlightMenu(event)

  router.push(path)

}


function logout() {

  adminLogout(router)

}

</script>

<style>
@import "../assets/css/admin-dashboard.css";
</style>