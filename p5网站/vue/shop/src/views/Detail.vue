<template>
  <div class="detail-page">
    <header class="detail-header">
      <h1 class="detail-title">商品详情</h1>
      <nav class="detail-nav">
        <router-link to="/">首页</router-link>
        <router-link to="/shop">返回商店</router-link>
      </nav>
    </header>

    <!-- 商品 -->
    <div v-if="product" class="detail-container">
      <div class="product-main">
        <img :src="product.img" class="product-image" />

        <div class="product-info">
          <h2>{{ product.name }}</h2>
          <p>{{ product.description }}</p>
          <div class="price">¥{{ product.price }}</div>

          <div class="qty-selector">
            <button @click="decreaseQty" :disabled="qty <= 1">-</button>
            <span class="qty">{{ qty }}</span>
            <button @click="increaseQty">+</button>
          </div>

          <button
            class="btn-add"
            :disabled="loading"
            @click="addCart"
          >
            {{ loading ? '加入中...' : '加入购物车' }}
          </button>

          <div class="average-rating">
            平均评分：{{ avgRating.toFixed(1) }} ⭐ ({{ comments.length }}条)
          </div>
        </div>
      </div>
    </div>

    <!-- 空 -->
    <div v-else class="empty">
      <p>未找到该商品</p>
      <button @click="router.push('/shop')">返回商城</button>
    </div>

    <!-- 评论 -->
    <div class="comment-section">
      <h2>商品评论</h2>

      <div class="comment-list">
        <div v-for="c in comments" :key="c.id" class="comment-item">

          <p class="comment-username">{{ c.username || '匿名' }}</p>

          <p class="comment-rating">
            {{ '⭐'.repeat(c.rating) }}
          </p>

          <p class="comment-content">{{ c.content }}</p>

          <p class="comment-time">
            {{ formatTime(c.createTime) }}
          </p>

          <button
            class="delete-comment-btn"
            v-if="isMine(c)"
            @click="deleteComment(c.id)"
          >
            删除
          </button>

        </div>
      </div>

      <div class="comment-form">
        <textarea v-model="commentContent" placeholder="写下你的评价..."></textarea>

        <select v-model="rating">
          <option value="5">⭐⭐⭐⭐⭐</option>
          <option value="4">⭐⭐⭐⭐</option>
          <option value="3">⭐⭐⭐</option>
          <option value="2">⭐⭐</option>
          <option value="1">⭐</option>
        </select>

        <button :disabled="submitLoading" @click="submitComment">
          {{ submitLoading ? '提交中...' : '发表评论' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useProductDetail } from '../assets/js/detail.js'

const {
  product,
  qty,
  loading,
  comments,
  commentContent,
  rating,
  submitLoading,
  avgRating,
  router,
  increaseQty,
  decreaseQty,
  addCart,
  isMine,
  deleteComment,
  formatTime,
  submitComment
} = useProductDetail()
</script>

<style>
@import "../assets/css/common.css";
@import "../assets/css/detail.css";
</style>