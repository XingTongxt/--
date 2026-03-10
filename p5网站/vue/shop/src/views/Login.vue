<template>
  <div class="login-page">
    <header class="login-header">
      <h1 class="login-title">登录</h1>
      <nav class="login-nav">
        <router-link to="/">首页</router-link>
        <!-- <router-link to="/admin">管理</router-link> -->
      </nav>
    </header>

    <div class="overlay"></div>

    <div class="login-container">
      <div class="login-box">
        <h1>登录 Phantom Shop</h1>

        <input id="username" type="text" placeholder="用户名">
        <input id="password" type="password" placeholder="密码">

        <button id="login-btn">登录</button>

        <span class="register-link">
          <router-link to="/register">注册新账号</router-link>
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { initLogin } from '../assets/js/login.js'

export default {
  name: 'Login',
  mounted() {
    initLogin(this.$router) 
  }
}
</script>

<style>
@import "../assets/css/common.css";
@import "../assets/css/login.css";
</style>