<template>
  <div>
    <header class="register-header">
      <h1 class="register-title">注册</h1>
      <nav class="register-nav">
        <a href="/index.html">首页</a>
      </nav>
    </header>

    <div class="overlay"></div>

    <div class="register-container">
      <div class="register-box">
        <h1>注册 Phantom Shop</h1>
        <input type="text" id="reg-username" placeholder="用户名">
        <input type="password" id="reg-password" placeholder="密码">
        <input type="password" id="reg-password2" placeholder="确认密码">

        <button id="register-btn">注册</button>

        <span class="login-link">
          <router-link to="/login">返回登录</router-link>
        </span>

      </div>
    </div>
  </div>
</template>

<script>
import "../assets/js/register.js"

export default {
  name: "Register"
}
</script>

<style scoped>
@import "../assets/css/common.css";
@import "../assets/css/register.css";
</style>