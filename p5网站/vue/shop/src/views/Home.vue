<template>
  <div class="homepage">

    

    <div class="container">
      <h1 class="title">PHANTOM SHOP</h1>

      <nav class="menu">
        <router-link to="/login" class="menu-item">登录</router-link>
        <router-link to="/shop" class="menu-item">进入商店</router-link>
        <router-link to="/about" class="menu-item">关于</router-link>
      </nav>
    </div>

  </div>
</template>

<script>
import "../assets/js/animation.js"
import "../assets/js/background.js"

export default {
  name: "Home"
}
</script>

<style >
@import "../assets/css/index.css";
</style>