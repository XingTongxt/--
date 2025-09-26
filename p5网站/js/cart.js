// cart.js
const token = localStorage.getItem('token');
if (token) {
  import('../js/cart.js').then(module => {
    module.loadCart(token);
  });
} else {
  alert('请先登录');
  window.location.href = 'login.html';
}
// ===== 加载购物车 =====
export async function loadCart(token) {
  if (!token) return;

  try {
    const res = await fetch('http://localhost:8080/api/cart', {
      headers: { 'Authorization': 'Bearer ' + token }
    });

    if (!res.ok) {
      const text = await res.text();
      console.error('加载购物车失败, status:', res.status, text);
      return;
    }

    const cartList = await res.json();
    if (!cartList || !Array.isArray(cartList)) return;

    updateCartCount(cartList);
    renderCart(cartList);
  } catch (err) {
    console.error('加载购物车失败', err);
  }
}

// ===== 更新购物车数量显示 =====
export function updateCartCount(cartList) {
  const cartIcon = document.querySelector('.cart-icon .cart-count');
  if (!cartIcon || !Array.isArray(cartList)) return;

  const total = cartList.reduce((sum, item) => sum + item.quantity, 0);
  cartIcon.textContent = total;
}

// ===== 渲染购物车列表 =====
export function renderCart(cartList) {
  const container = document.querySelector('.cart-container');
  if (!container || !Array.isArray(cartList)) return;

  container.innerHTML = '';

  if (cartList.length === 0) {
    container.innerHTML = '<p>购物车为空</p>';
    return;
  }

  const itemsWrapper = document.createElement('div');
  itemsWrapper.className = 'cart-items';
  container.appendChild(itemsWrapper);

  cartList.forEach(item => {
    const div = document.createElement('div');
    div.className = 'cart-item';
    div.innerHTML = `
            <img src="${item.img || ''}" alt="${item.name}" class="cart-img">
            <div class="cart-info">
                <span class="cart-name">${item.name}</span>
                <span class="cart-price">¥${item.price}</span>
                <div class="qty-controls">
                  <button class="btn-decrease">-</button>
                  <span class="item-qty">${item.quantity}</span>
                  <button class="btn-increase">+</button>
                </div>
                <button class="btn-remove" data-id="${item.productId}">删除</button>
            </div>
        `;
    itemsWrapper.appendChild(div);

    const decreaseBtn = div.querySelector('.btn-decrease');
    const increaseBtn = div.querySelector('.btn-increase');
    const qtySpan = div.querySelector('.item-qty');

    decreaseBtn.addEventListener('click', async () => {
      const newQty = Math.max(1, item.quantity - 1);
      await updateCartQuantity(item.productId, newQty, localStorage.getItem('token'));
    });

    increaseBtn.addEventListener('click', async () => {
      const newQty = item.quantity + 1;
      await updateCartQuantity(item.productId, newQty, localStorage.getItem('token'));
    });

    const removeBtn = div.querySelector('.btn-remove');
    removeBtn.addEventListener('click', async () => {
      await removeCartItem(item.productId, localStorage.getItem('token'));
    });
  });
 // ===== 总价显示 =====
    let totalDiv = container.querySelector('.cart-total');
    if (!totalDiv) {
        totalDiv = document.createElement('div');
        totalDiv.className = 'cart-total';
        container.appendChild(totalDiv);
    }

    const total = cartList.reduce((sum, item) => sum + item.price * item.quantity, 0);
    totalDiv.textContent = `总价: ¥${total.toFixed(2)}`;




  // 绑定数量修改事件
  container.querySelectorAll('.cart-quantity').forEach(input => {
    input.addEventListener('change', async e => {
      const productId = e.target.dataset.id;
      const quantity = parseInt(e.target.value);
      await updateCartQuantity(productId, quantity, localStorage.getItem('token'));
    });
  });

  // 绑定删除事件
  container.querySelectorAll('.btn-remove').forEach(btn => {
    btn.addEventListener('click', async e => {
      const productId = e.target.dataset.id;
      await removeCartItem(productId, localStorage.getItem('token'));
    });
  });
}

// ===== 添加商品到购物车 =====
export async function addToCartBackend(productId, token) {
  if (!token) return alert('请先登录');

  try {
    const res = await fetch('http://localhost:8080/api/cart', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      },
      body: JSON.stringify({ productId })
    });

    if (!res.ok) {
      const text = await res.text();
      console.error('加入购物车失败:', text);
      return;
    }

    const cartList = await res.json(); // 后端返回完整列表
    updateCartCount(cartList);
    renderCart(cartList);
  } catch (err) {
    console.error('加入购物车失败', err);
  }
}

// ===== 修改购物车商品数量 =====
export async function updateCartQuantity(productId, quantity, token) {
  if (!token) return alert('请先登录');

  try {
    const res = await fetch(`http://localhost:8080/api/cart/${productId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      },
      body: JSON.stringify({ quantity })
    });

    if (!res.ok) {
      const text = await res.text();
      console.error('更新购物车数量失败:', text);
      return;
    }

    const cartList = await res.json(); // 后端返回完整列表
    updateCartCount(cartList);
    renderCart(cartList);
  } catch (err) {
    console.error('更新购物车数量失败', err);
  }
}

// ===== 删除购物车商品 =====
export async function removeCartItem(productId, token) {
  if (!token) return alert('请先登录');

  try {
    const res = await fetch(`http://localhost:8080/api/cart/${productId}`, {
      method: 'DELETE',
      headers: { 'Authorization': 'Bearer ' + token }
    });

    if (!res.ok) {
      const text = await res.text();
      console.error('删除购物车商品失败:', text);
      return;
    }

    const cartList = await res.json(); // 后端返回完整列表
    updateCartCount(cartList);
    renderCart(cartList);
  } catch (err) {
    console.error('删除购物车商品失败', err);
  }
}




