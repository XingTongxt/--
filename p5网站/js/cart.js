const cartKey = 'shopCart';
let cart = JSON.parse(localStorage.getItem(cartKey)) || [];

// 更新购物车数量
function updateCartCount() {
  const cartCountEl = document.querySelector('.cart-count');
  if (!cartCountEl) return;
  const count = cart.reduce((s, i) => s + (i.quantity || 0), 0);
  cartCountEl.textContent = count;
}

// 保存购物车
function saveCart() {
  localStorage.setItem(cartKey, JSON.stringify(cart));
  updateCartCount();
}

// 加入购物车（供 main.js 使用）
function addToCart(product) {
  const existing = cart.find(item => item.name === product.name);
  if (existing) {
    existing.quantity += 1;
  } else {
    cart.push(product);
  }
  saveCart();
}

// 删除商品
function removeItem(name) {
  cart = cart.filter(item => item.name !== name);
  saveCart();
  renderCart();
}

// 修改商品数量
function updateQuantity(name, qty) {
  const item = cart.find(i => i.name === name);
  if (item) {
    item.quantity = Math.max(1, qty);
    saveCart();
    renderCart();
  }
}

// 计算总价
function getTotal() {
  return cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
}

// 渲染购物车页面
function renderCart() {
  const container = document.querySelector('.cart-items');
  if (!container) return;

  container.innerHTML = '';

  if (cart.length === 0) {
    container.innerHTML = '<p>购物车为空</p>';
    document.getElementById('cart-total').textContent = '总价: ¥0';
    return;
  }

cart.forEach(item => {
  const div = document.createElement('div');
  div.className = 'cart-item';
  div.innerHTML = `
    <img src="${item.img}" alt="${item.name}">
    <div class="item-info">
      <h3>${item.name}</h3>
      <p>¥${item.price}</p>
      <div class="qty-controls">
        <button class="btn-decrease">-</button>
        <span class="item-qty">${item.quantity}</span>
        <button class="btn-increase">+</button>
      </div>
      <button class="btn-remove">删除</button>
    </div>
  `;
  container.appendChild(div);

  // 增加数量
  div.querySelector('.btn-increase').addEventListener('click', () => {
    updateQuantity(item.name, item.quantity + 1);
  });

  // 减少数量
  div.querySelector('.btn-decrease').addEventListener('click', () => {
    updateQuantity(item.name, item.quantity - 1);
  });

  // 删除按钮
  div.querySelector('.btn-remove').addEventListener('click', () => {
    removeItem(item.name);
  });
});


  // 更新总价
  const totalEl = document.getElementById('cart-total');
  totalEl.textContent = `总价: ¥${getTotal()}`;
}

// 初始化
document.addEventListener('DOMContentLoaded', () => {
  updateCartCount();
  renderCart();
});

// 让 main.js 可以调用
window.Cart = { addToCart };
