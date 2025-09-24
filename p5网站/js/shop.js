document.addEventListener('DOMContentLoaded', () => {
  const shopContainer = document.querySelector('.shop-container');

  // 获取商品数据
  fetch('http://localhost:8080/api/products')
    .then(res => res.json())
    .then(products => {
      // 按类别分组
      const categories = {};
      products.forEach(product => {
        if (!categories[product.category]) {
          categories[product.category] = [];
        }
        categories[product.category].push(product);
      });

      // 清空原本 HTML
      shopContainer.innerHTML = '';

      // 渲染每个类别
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
                        <button class="btn-add">加入购物车</button>
                    `;
          productList.appendChild(card);
        });

        categoryDiv.appendChild(productList);
        shopContainer.appendChild(categoryDiv);
      }

      // 初始化点击滚动效果和购物车事件
      initShopJsEvents();
    })
    .catch(err => console.error('获取商品失败:', err));
});

// 将原本 shop.js 的事件处理函数单独封装
function initShopJsEvents() {
  const shopContainer = document.querySelector('.shop-container'); // 函数内定义
  if (!shopContainer) return;

  // 回到顶部
  const top = document.querySelector('#back-to-top');
  if (top) {
    top.addEventListener('click', () => {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });
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

  // 加入购物车逻辑
  shopContainer.addEventListener('click', e => {
    const btn = e.target.closest('.btn-add');
    if (!btn) return;

    const card = btn.closest('.product-card');
    const product = {
      id: card.querySelector('a').href.split('id=')[1],
      name: card.querySelector('.product-title').textContent,
      price: parseFloat(card.querySelector('.product-price').textContent.replace(/[^\d.]/g, '')),
      img: card.querySelector('img').src,
      quantity: 1
    };
    Cart.addToCart(product); // 调用购物车方法
    showToast(`${product.name} 已加入购物车`);
  });
}

// toast 提示
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

