import { addToCartBackend } from './cart.js'; // 确保路径正确

// 获取 URL 参数
const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get("id");

// 商品数据
const products = {
  1: { title: "墙面画", price: 300, desc: "一幅美丽的墙面画，能够提升房间的艺术气息。", img: "../img/wall-paint.jpg" },
  2: { title: "马克杯", price: 150, desc: "一个可爱的马克杯，让你享受每一口咖啡。", img: "../img/Mug.jpg" },
  3: { title: "鼠标垫", price: 50, desc: "舒适的鼠标垫，让你的办公更顺手。", img: "../img/Mouse-Pad.jpg" },
  4: { title: "Persona 5 海报", price: 48, desc: "精美的Persona 5海报，装点你的房间。", img: "../img/Persona-Poster.jpg" },
  5: { title: "背包", price: 98, desc: "可爱的SUMIRE YOSHIZAWA背包，实用又好看。", img: "../img/SUMIREYOSHIZAWA-Backpack.jpg" },
  6: { title: "Joker 模型", price: 588, desc: "精致的Joker模型，收藏爱好者必备。", img: "../img/Joker-Model.webp" },
  7: { title: "Mona Plush", price: 128, desc: "可爱的Mona毛绒玩具，陪伴你的每一天。", img: "../img/Mona-Plush.webp" },
  8: { title: "Joker Cap", price: 38, desc: "酷炫的Joker帽子，日常穿搭加分。", img: "../img/Joker-Cap.jpg" },
  9: { title: "Mona T-Shirt", price: 100, desc: "舒适的Mona T恤，让你日常穿搭更有风格。", img: "../img/mona-T-Shirt.jpg" },
  10: { title: "Jack T-Shirt", price: 100, desc: "舒适的Jack T恤，轻松搭配各种造型。", img: "../img/jack-T-Shirt.jpg" },
  11: { title: "Persona 5 Hoodie", price: 98, desc: "舒适的Persona 5连帽衫，让你酷炫又保暖。", img: "../img/Persona5-Hoodie.jpg" },
  12: { title: "Joker Hoodie", price: 98, desc: "经典Joker连帽衫，让你成为队伍焦点。", img: "../img/Joker-Hoodie.jpg" },
  13: { title: "Queen Hoodie", price: 98, desc: "Queen连帽衫，简约又时尚。", img: "../img/Queen-Hoodie.jpg" },
  14: { title: "Makoto Hoodie", price: 98, desc: "Makoto连帽衫，舒适保暖，适合日常穿搭。", img: "../img/Makoto-Hoodie.jpg" },
  15: { title: "Star Blanket", price: 128, desc: "星空主题毛毯，让房间更有艺术感。", img: "../img/Star-Blanket.jpg" },
  16: { title: "The Phantom Thieves Blanket", price: 128, desc: "怪盗团主题毛毯，温暖又酷炫。", img: "../img/ThePhantomThieves-Blanket.jpg" },
  17: { title: "Joker Blanket", price: 128, desc: "Joker主题毛毯，舒适又有特色。", img: "../img/Joker_Blanket.jpg" },
  18: { title: "Star Pillow", price: 78, desc: "星空抱枕，柔软舒适，装点房间。", img: "../img/Star-Pillow.jpg" },
  19: { title: "Joker Pillow", price: 85, desc: "Joker抱枕，造型独特，送礼或自用都合适。", img: "../img/Joker-Pillow.jpg" },
  20: { title: "Makoto Pillow", price: 68, desc: "Makoto抱枕，柔软舒适，日常休息必备。", img: "../img/Makoto-Pillow.jpg" }
};

const container = document.querySelector(".detail-container");
const product = products[productId];

if (!product) {
  container.innerHTML = `<p>未找到该商品。<a href="shop.html">返回商城</a></p>`;
} else {
  renderProductDetail(product);
  setupQuantity();
  setupAddCart(product);
  setRandomBackground();
}

// ===== 渲染商品详情 =====
function renderProductDetail(product) {
  container.innerHTML = `
    <div class="product-main">
      <img src="${product.img}" alt="${product.title}" class="product-image">
      <div class="product-info">
          <h2>${product.title}</h2>
          <p>${product.desc}</p>
          <div class="price">价格：¥${product.price}</div>
          <div class="qty-selector">
            <button class="decrease">-</button>
            <span class="qty">1</span>
            <button class="increase">+</button>
          </div>
          <button class="btn-add">加入购物车</button>
      </div>
    </div>
  `;
}

// ===== 数量选择 =====
let qty = 1;
function setupQuantity() {
  const decreaseBtn = container.querySelector('.decrease');
  const increaseBtn = container.querySelector('.increase');
  const qtySpan = container.querySelector('.qty');

  decreaseBtn.addEventListener('click', () => {
    qty = Math.max(1, qty - 1);
    qtySpan.textContent = qty;
  });

  increaseBtn.addEventListener('click', () => {
    qty++;
    qtySpan.textContent = qty;
  });
}

// ===== 加入购物车按钮 =====
function setupAddCart(product) {
  const addBtn = container.querySelector('.btn-add');
  addBtn.addEventListener('click', async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('请先登录');
      window.location.href = 'login.html';
      return;
    }

    try {
      await addToCartBackend(productId, token, qty);
      showToast(`${product.title} 已加入购物车`);
    } catch (err) {
      console.error(err);
      alert('加入购物车失败');
    }
  });
}

// ===== toast 提示 =====
function showToast(msg) {
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.textContent = msg;
  document.body.appendChild(toast);
  requestAnimationFrame(() => toast.classList.add('show'));
  setTimeout(() => {
    toast.classList.remove('show');
    toast.addEventListener('transitionend', () => toast.remove());
  }, 2000);
}

// ===== 随机背景 =====
function setRandomBackground() {
  const images = [
      '../img/1.png',
      '../img/2.jpg',
      '../img/3.jpg',
      '../img/4.jpg'
  ];
  const index = Math.floor(Math.random() * images.length);
  container.style.transition = 'background-image 0.5s ease-in-out';
  container.style.backgroundImage = `url(${images[index]})`;
  container.style.backgroundSize = 'cover';
  container.style.backgroundPosition = 'center';
}