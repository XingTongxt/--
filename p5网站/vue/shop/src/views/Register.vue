<template>
  <div>

    <header class="register-header">
      <h1 class="register-title">注册</h1>
      <nav class="register-nav">
        <router-link to="/">首页</router-link>
      </nav>
    </header>

    <div class="overlay"></div>

    <div class="register-container">
      <div class="register-box">

        <h1>注册 Phantom Shop</h1>

        <input v-model="username" type="text" placeholder="用户名" />
        <input v-model="password" type="password" placeholder="密码" />
        <input v-model="password2" type="password" placeholder="确认密码" />
        <input v-model="email" type="email" placeholder="邮箱" />

        <button @click="register">
          注册
        </button>

        <span class="login-link">
          <router-link to="/login">返回登录</router-link>
        </span>

      </div>
    </div>

  </div>
</template>

<script>

import { registerUser } from "../assets/js/register.js"

export default {

  name: "Register",

  data() {
    return {
      username: "",
      password: "",
      password2: "",
      email: ""
    }
  },

  methods: {

    register() {
      registerUser(
        this.username,
        this.password,
        this.password2,
        this.email,
        this.$router
      )
    }

  }

}

</script>

<style scoped>
@import "../assets/css/common.css";
@import "../assets/css/register.css";
</style>