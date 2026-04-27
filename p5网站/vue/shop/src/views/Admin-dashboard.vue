<template>
  <div class="admin-dashboard">

    <h2>欢迎您，管理员</h2>
    <p ref="adminInfo">加载中...</p>

    <div id="menu">

      <h3>功能菜单</h3>

      <!-- SUPERADMIN -->
      <router-link v-if="role === 'SUPERADMIN'" to="/admin/manage-users" class="menu-item">
        用户管理
      </router-link>

      <router-link to="/admin/manage-items" class="menu-item">
        物品管理
      </router-link>

      <router-link v-if="role === 'SUPERADMIN'" to="/admin/manage-logs" class="menu-item">
        日志管理
      </router-link>

      <router-link to="/shop" class="menu-item">
        返回商店
      </router-link>

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


function logout() {
  adminLogout(router)
}

</script>

<style>
@import "../assets/css/admin-dashboard.css";
</style>