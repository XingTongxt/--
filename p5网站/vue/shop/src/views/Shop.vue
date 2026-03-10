<template>
  <div class="shop-page">

    <header class="shop-header">
      <h1 class="shop-title">商城</h1>
      <div class="search-bar">
        <input type="text" id="search" placeholder="搜索商品...">
        <button id="search-btn">搜索</button>
      </div>
      <nav class="shop-nav">
        <router-link to="/admin/dashboard" id="admin-btn">后台管理</router-link>
        <router-link to="/">首页</router-link>
        <router-link to="/cart" class="cart-icon">
          🛒
          <span class="cart-count">0</span>
        </router-link>
        <router-link to="/user">我的</router-link>
      </nav>
    </header>

    

    <main class="shop-container">
    </main>

    <div class="elevate">
      <ul class="elevate-list">
        <li class="elevate-item" id="shop-cart">
          <router-link to="/cart" class="cart-icon">
            🛒
            <span class="cart-count">0</span>
          </router-link>
        </li>
        <li class="elevate-item" id="recommended">
          <a href="javascript:void(0);">推荐</a>
        </li>
        <li class="elevate-item" id="tshirts">
          <a href="javascript:void(0);">T恤</a>
        </li>
        <li class="elevate-item" id="hoodies">
          <a href="javascript:void(0);">卫衣</a>
        </li>
        <li class="elevate-item" id="blankets">
          <a href="javascript:void(0);">毛毯</a>
        </li>
        <li class="elevate-item" id="pillows">
          <a href="javascript:void(0);">抱枕</a>
        </li>
        <li class="elevate-item" id="home">
          <router-link to="/user">我的</router-link>
        </li>
        <li class="elevate-item" id="back-to-top">
          <a href="javascript:void(0);" class="back-to-top">回顶部</a>
        </li>
      </ul>
    </div>

    <div style="height: 10px;"></div>

    <footer>
      <div class="footer-container">
        <router-link class="footer-item" to="/">首页</router-link>
        <router-link class="footer-item" to="/shop">商店</router-link>
        <router-link class="footer-item" to="/cart">购物车</router-link>
        <router-link class="footer-item" to="/about">关于我们</router-link>
      </div>
    </footer>

  </div>
</template>

<script>
import { initShop } from "../assets/js/shop.js"
import "../assets/js/background.js"

export default {
  name: "Shop",
  mounted() {
    initShop()
  }
}
</script>

<style >
@import "../assets/css/common.css";
@import "../assets/css/shop.css";
</style>