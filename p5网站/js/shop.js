// shop.js
import { addToCartBackend, loadCart, updateCartCount } from './cart.js';

document.addEventListener('DOMContentLoaded', async () => {
  const shopContainer = document.querySelector('.shop-container');
  const cartIcon = document.querySelector('.cart-icon');
  const token = localStorage.getItem('token');

  // ===== 1. 获取商品 =====
  try {
    const res = await fetch('http://localhost:8080/api/products');
    if (!res.ok) throw new Error('获取商品失败');
    const products = await res.json();
    renderProducts(products);
  } catch (err) {
    console.error('获取商品失败:', err);
  }

  // ===== 2. 初始化事件 =====
  initShopJsEvents(token, cartIcon);

  // ===== 3. 加载购物车数量 =====
  if (token) await loadCart(token);

  // ===== 4. 检查管理员身份 =====
  checkAdmin(token);
});

// ===== 渲染商品 =====
function renderProducts(products) {
  const shopContainer = document.querySelector('.shop-container');
  if (!shopContainer) return;

  const categories = {};
  products.forEach(product => {
    if (!categories[product.category]) categories[product.category] = [];
    categories[product.category].push(product);
  });

  shopContainer.innerHTML = '';
  for (const category in categories) {
    const categoryDiv = document.createElement('div');
    categoryDiv.className = 'category-container';
    categoryDiv.dataset.category = category;

    const title = document.createElement('h2');
    title.className = 'category-title';
    title.textContent = category === 'recommended' ? '推荐' : category;
    categoryDiv.appendChild(title);

    const productList = document.createElement('div');
    productList.className = 'product-list';

    categories[category].forEach(product => {
      const card = document.createElement('div');
      card.className = 'product-card';
      card.dataset.category = category;
      card.innerHTML = `
                <a href="detail.html?id=${product.id}">
                    <img src="${product.img}" alt="${product.name}">
                    <h3 class="product-title">${product.name}</h3>
                </a>
                <p class="product-price">¥${product.price}</p>
                <button class="btn-add" data-id="${product.id}">加入购物车</button>
            `;
      productList.appendChild(card);
    });

    categoryDiv.appendChild(productList);
    shopContainer.appendChild(categoryDiv);
  }
}

// ===== 初始化事件 =====
function initShopJsEvents(token, cartIcon) {
  const shopContainer = document.querySelector('.shop-container');
  if (!shopContainer) return;

  // 回到顶部
  const top = document.querySelector('#back-to-top');
  if (top) {
    top.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));
  }

  // 分类滚动和 active
  const List = document.querySelector('.elevate-list');
  if (List) {
    List.addEventListener('click', e => {
      const categoryContainer = document.querySelector('.category-container[data-category="' + e.target.parentElement.id + '"]');
      const activeItem = document.querySelector('.elevate-list .active');
      if (e.target.tagName === 'A' && categoryContainer) {
        if (activeItem) activeItem.classList.remove('active');
        e.target.classList.add('active');
      }
      if (categoryContainer) {
        const offset = categoryContainer.offsetTop;
        window.scrollTo({ top: offset, behavior: 'smooth' });
      }
    });

    window.addEventListener('scroll', () => {
      const scrollTop = window.scrollY || document.documentElement.scrollTop;
      document.querySelectorAll('.category-container').forEach(container => {
        if (scrollTop >= container.offsetTop - 50) {
          document.querySelectorAll('.elevate-list a').forEach(link => link.classList.remove('active'));
          const navItem = document.querySelector(`.elevate-list #${container.dataset.category} a`);
          if (navItem) navItem.classList.add('active');
        }
      });
    });
  }

  // 商品加入购物车 + 飞入动画 + toast
  shopContainer.addEventListener('click', e => {
    const btn = e.target.closest('.btn-add');
    if (!btn) return;

    const card = btn.closest('.product-card');
    const productId = btn.dataset.id;

    // 调用后端购物车接口
    addToCartBackend(productId, token);

    // 飞入购物车动画
    if (cartIcon) {
      const imgElement = card.querySelector('img');
      flyToCart(imgElement, cartIcon);
    }

    // toast 提示
    const productName = card.querySelector('.product-title').textContent;
    showToast(`${productName} 已加入购物车`);
  });
}

// ===== 飞入购物车动画 =====
function flyToCart(imgElement, cartIcon) {
  const imgRect = imgElement.getBoundingClientRect();
  const flyImg = imgElement.cloneNode(true);
  flyImg.classList.add('fly-img');
  Object.assign(flyImg.style, {
    position: 'fixed',
    left: imgRect.left + 'px',
    top: imgRect.top + 'px',
    width: imgRect.width + 'px',
    height: imgRect.height + 'px',
    transform: 'translate(0,0) scale(1)',
    opacity: '1',
    transition: 'transform 0.6s ease, opacity 0.6s ease'
  });
  document.body.appendChild(flyImg);

  const cartRect = cartIcon.getBoundingClientRect();
  const dx = cartRect.left + cartRect.width / 2 - (imgRect.left + imgRect.width / 2);
  const dy = cartRect.top + cartRect.height / 2 - (imgRect.top + imgRect.height / 2);

  requestAnimationFrame(() => {
    flyImg.style.transform = `translate(${dx}px, ${dy}px) scale(0.2)`;
    flyImg.style.opacity = '0.25';
  });

  flyImg.addEventListener('transitionend', () => {
    flyImg.remove();
    cartIcon.classList.add('hit');
    setTimeout(() => cartIcon.classList.remove('hit'), 600);
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

// ===== 检查管理员按钮 =====
async function checkAdmin(token) {
  if (!token) return;
  try {
    let res = await fetch("http://localhost:8080/api/user/info", {
      headers: { "Authorization": "Bearer " + token }
    });
    if (!res.ok) {
      res = await fetch("http://localhost:8080/admin/info", {
        headers: { "Authorization": "Bearer " + token }
      });
    }

    if (res.ok) {
      const data = await res.json();
      if (data.role && data.role.toUpperCase() === "ADMIN") {
        const adminBtn = document.createElement('a');
        adminBtn.href = "admin-dashboard.html";
        adminBtn.id = "admin-btn";
        adminBtn.textContent = "后台管理";
        adminBtn.style.display = "inline-block";
        document.querySelector('.shop-nav').appendChild(adminBtn);
      }

    }
  } catch (err) {
    console.error("获取用户/管理员信息失败", err);
  }
}
