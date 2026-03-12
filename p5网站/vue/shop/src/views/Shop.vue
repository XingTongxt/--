<template>
  <div class="shop-page">

    <header class="shop-header">
      <h1 class="shop-title">商城</h1>

      <div class="search-bar">
        <input type="text" v-model="keyword" placeholder="搜索商品...">
        <button @click="search">搜索</button>
        <select class="sort-select" v-model="sortType" @change="changeSort">
          <option value="default">默认排序</option>
          <option value="sales">销量排序</option>
          <option value="priceAsc">价格从低到高</option>
          <option value="priceDesc">价格从高到低</option>
          <option value="desc">评分从高到低</option>
          <option value="asc">评分从低到高</option>
        </select>
      </div>

      <nav class="shop-nav">
        <router-link to="/">首页</router-link>
        <router-link to="/cart" class="cart-icon">
          🛒
          <span class="cart-count">0</span>
        </router-link>
        <router-link to="/orders">订单</router-link>
        <router-link to="/user">我的</router-link>
      </nav>
    </header>

    <main class="shop-container"></main>

    <div class="elevate">
      <ul class="elevate-list">

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

        <li class="elevate-item" id="back-to-top">
          <a href="javascript:void(0);">回顶部</a>
        </li>

      </ul>
    </div>

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


import {
  initShop,
  loadProductsBySales,
  loadProductsByPriceAsc,
  loadProductsByPriceDesc,
  loadAllProducts,
  loadProductsByRating
} from "../assets/js/shop.js"

export default {

  name: "Shop",

  data() {
    return {
      keyword: "",
      sortType: "default"
    }
  },

  mounted() {
    initShop()
  },

  methods: {

    search() {
      searchProducts()
    },

    changeSort() {

      if (this.sortType === "default") {
        loadAllProducts()
      }

      if (this.sortType === "sales") {
        loadProductsBySales()
      }

      if (this.sortType === "priceAsc") {
        loadProductsByPriceAsc()
      }

      if (this.sortType === "priceDesc") {
        loadProductsByPriceDesc()
      }
      if (this.sortType === "desc") {
        loadProductsByRating(true)
      }
      if (this.sortType === "asc") {
        loadProductsByRating(false)
      }
    }

  }

}

</script>

<style>
@import "../assets/css/common.css";
@import "../assets/css/shop.css";
</style>