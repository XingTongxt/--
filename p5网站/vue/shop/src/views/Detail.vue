<template>
  <div class="detail-page">
    <header>
      Persona 5 商店 - 商品详情
    </header>
    <div class="detail-container" ref="container">
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { renderProductDetail, setupQuantity, setupAddCart, setRandomBackground, showToast } from '../assets/js/detail.js';

const container = ref(null);
const route = useRoute();
const router = useRouter();

const productId = Number(route.params.id);
async function fetchProduct(id) {
  try {
    const res = await fetch(`http://localhost:8080/api/products/${id}`);
    if (!res.ok) throw new Error('商品获取失败');
    const product = await res.json();
    return product;
  } catch (err) {
    console.error(err);
    showToast('获取商品失败');
    return null;
  }
}

onMounted(async () => {
  const product = await fetchProduct(productId);

  if (!product) {
    container.value.innerHTML = `
      <p>未找到该商品。
        <button id="back-to-shop">返回商城</button>
      </p>
    `;
    const btn = container.value.querySelector('#back-to-shop');
    btn.addEventListener('click', () => router.push('/shop'));
  } else {
    renderProductDetail(container.value, product);
    setupQuantity(container.value);
    setupAddCart(container.value, product, product.id);
    setRandomBackground(container.value);
  }
});
</script>

<style>
@import "../assets/css/common.css";
@import "../assets/css/detail.css";
</style>