<template>
  <div class="admin-page">
    <header class="admin-header">
      <h1 class="admin-title">管理</h1>
      <nav class="admin-nav">
        <router-link to="/index" class="menu-item">首页</router-link>
        <router-link to="/login" class="menu-item">登录</router-link>
      </nav>
    </header>

    <div class="overlay"></div>

    <div class="admin-container">
      <div class="login-box">
        <h2>管理员登录</h2>
        <input v-model="state.username" placeholder="用户名" />
        <input v-model="state.password" type="password" placeholder="密码" />
        <button @click="login" :disabled="state.loading">
          {{ state.loading ? "登录中..." : "登录" }}
        </button>
        <p>{{ state.msg }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { useAdminLogin } from "../assets/js/admin.js";

export default {
  name: "AdminLogin",
  setup() {
    const { state, login } = useAdminLogin();
    return { state, login };
  }
};
</script>

<style>
@import "../assets/css/admin.css";
</style>