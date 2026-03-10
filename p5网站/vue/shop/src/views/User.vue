<template>
  <div class="user-center">
    <div class="container">
      <h1 class="title">用户中心</h1>

      <div class="info">
        <p><strong>用户名：</strong><span id="username">加载中...</span></p>
        <p><strong>邮箱：</strong><span id="email">加载中...</span></p>
        <p>角色: <span id="role"></span></p>
      </div>

      <div class="actions">
        <button @click="handleChangePassword">修改密码</button>
        <button @click="handleLogout">退出登录</button>
        <button @click="goShop">返回商城</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { initUserJS, changePassword, logout } from '../assets/js/user.js'

const router = useRouter()

onMounted(() => {
  initUserJS(router) // 初始化用户信息
})

// Vue 调用 JS 文件逻辑
function handleChangePassword() {
  changePassword()
}

function handleLogout() {
  logout() 
}

function goShop() {
  router.push('/shop')
}
</script>

<style>
@import "../assets/css/user.css";
</style>