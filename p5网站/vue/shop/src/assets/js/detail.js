import { addToCartBackend } from './cart.js'; // 确保路径正确

let qty = 1;

// 渲染商品详情
export function renderProductDetail(container, product) {
  container.innerHTML = `
    <div class="product-main">
      <img src="${product.img}" alt="${product.name}" class="product-image">
      <div class="product-info">
          <h2>${product.name}</h2>
          <p>${product.description}</p>
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

// 数量选择
export function setupQuantity(container) {
  qty = 1;
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

// setupAddCart(container, product, productId)
export function setupAddCart(container, product, productId) {
  const addBtn = container.querySelector('.btn-add');
  addBtn.addEventListener('click', async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('请先登录');
      window.location.href = '/login';
      return;
    }

    try {
      await addToCartBackend(productId, token, qty);
      showToast(`${product.name} 已加入购物车`);
    } catch (err) {
      console.error(err);
      alert('加入购物车失败');
    }
  });
}

// toast 提示
export function showToast(msg) {
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

// 随机背景
export function setRandomBackground(container) {
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