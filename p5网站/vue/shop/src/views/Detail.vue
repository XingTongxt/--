<template>
  <div class="detail-page">

    <header>
      Persona 5 商店 - 商品详情
    </header>

    <div class="detail-container" ref="container">
    </div>
    <div class="comment-section">
      <h2>商品评论</h2>
      <div class="comment-list" ref="commentList"></div>
      <div class="comment-form">
        <textarea v-model="commentContent" placeholder="写下你的评价..."></textarea>
        <select v-model="rating">
          <option value="5">⭐⭐⭐⭐⭐</option>
          <option value="4">⭐⭐⭐⭐</option>
          <option value="3">⭐⭐⭐</option>
          <option value="2">⭐⭐</option>
          <option value="1">⭐</option>
        </select>
        <button @click="submitComment">发表评论</button>
      </div>
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
const commentList = ref(null)
const commentContent = ref("")
const rating = ref(5)
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
  const product = await fetchProduct(productId)
  if (!product) {
    container.value.innerHTML = `
      <p>未找到该商品。
        <button id="back-to-shop">返回商城</button>
      </p>
    `
    const btn = container.value.querySelector('#back-to-shop')
    btn.addEventListener('click', () => router.push('/shop'))

  } else {
    renderProductDetail(container.value, product)
    setupQuantity(container.value)
    setupAddCart(container.value, product, product.id)
    loadComments()
  }

})
async function loadComments() {
  try {
    const res = await fetch(`http://localhost:8080/api/comments/${productId}`);
    if (!res.ok) throw new Error("评论获取失败");
    const comments = await res.json();

    renderComments(comments);
    if (comments.length > 0) {
      const avg = comments.reduce((sum, c) => sum + c.rating, 0) / comments.length;
      renderAverageRating(avg);
    } else {
      renderAverageRating(0);
    }

  } catch (err) {
    console.error(err);
    showToast("加载评论失败");
  }
}

function renderComments(comments) {
  if (!commentList.value) return;
  const currentUsername = localStorage.getItem("username");
  commentList.value.innerHTML = "";
  comments.forEach(c => {
    const div = document.createElement("div");
    div.className = "comment-item";
    const currentUsername = localStorage.getItem("username");
    const isMine = currentUsername && c.username === currentUsername;
    div.innerHTML = `
    <p class="comment-username">${c.username || "匿名"}</p>
    <p class="comment-rating">${"⭐".repeat(c.rating)}</p>
    <p class="comment-content">${c.content}</p>
    <p class="comment-time">${c.createTime ? new Date(c.createTime).toLocaleString() : ""}</p>
    ${isMine ? '<button class="delete-comment-btn">删除</button>' : ''}
  `;

    if (isMine) {
      const btn = div.querySelector(".delete-comment-btn");
      btn.addEventListener("click", async () => {
        const res = await fetch(`http://localhost:8080/api/comments/${c.id}`, {
          method: "DELETE",
          headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
        });
        if (res.ok) {
          showToast("删除成功");
          loadComments();
        } else {
          showToast("删除失败");
        }
      });
    }

    commentList.value.appendChild(div);
  });
}
function renderAverageRating(avg) {
  let avgContainer = document.querySelector('.average-rating');
  if (!avgContainer) {
    avgContainer = document.createElement('div');
    avgContainer.className = 'average-rating';
    const infoEl = document.querySelector('.product-info');
    if (infoEl) infoEl.appendChild(avgContainer);
  }

  const fullStars = Math.floor(avg);
  const halfStar = avg - fullStars >= 0.5 ? 1 : 0;
  const emptyStars = 5 - fullStars - halfStar;

  avgContainer.innerHTML =
    "平均评分：" +
    "⭐".repeat(fullStars) +
    (halfStar ? "✰" : "") +
    "☆".repeat(emptyStars) +
    ` (${avg.toFixed(1)})`;
}
async function submitComment() {

  const token = localStorage.getItem("token")

  if (!token) {
    showToast("请先登录")
    return
  }

  if (!commentContent.value) {
    showToast("评论不能为空")
    return
  }

  const res = await fetch("http://localhost:8080/api/comments", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + token
    },
    body: JSON.stringify({
      productId: productId,
      content: commentContent.value,
      rating: rating.value
    })
  })

  if (res.ok) {
    showToast("评论成功")
    commentContent.value = ""
    loadComments()
  } else {
    showToast("评论失败")
  }

}
</script>

<style>
@import "../assets/css/common.css";
@import "../assets/css/detail.css";
</style>