<template>
  <div class="cart-page">
    <header class="cart-header">
      <h1 class="cart-title">购物车</h1>
      <nav class="cart-nav">
        <router-link to="/">首页</router-link>
        <router-link to="/shop">返回商店</router-link>
      </nav>
    </header>

    <main class="cart-container">
      <div class="cart-items" ref="cartItems"></div>
      <div class="cart-total" ref="cartTotal"></div>
    </main>

    <div class="cart-footer">
      <button class="btn-checkout" @click="checkout">去结算</button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { initCartJS, checkoutCart } from '../assets/js/cart.js'

const router = useRouter()

const cartItems = ref(null)
const cartTotal = ref(null)

onMounted(() => {
  initCartJS(cartItems.value, cartTotal.value, router)
})

const checkout = async () => {

  if (!confirm("确认支付吗？")) return

  await checkoutCart(router)

}
</script>

<style>
@import "../assets/css/common.css";
@import "../assets/css/cart.css";
</style>