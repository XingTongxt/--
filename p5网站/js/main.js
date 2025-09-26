document.addEventListener('DOMContentLoaded', () => {
  const container = document.querySelector('.shop-container');
  const cartIcon = document.querySelector('.cart-icon');

  if (!container) return;
  container.addEventListener('click', e => {
    const btn = e.target.closest('.btn-add');
    if (!btn) return;

    const card = btn.closest('.product-card');
    const product = {
      name: card.querySelector('.product-title').textContent,
      price: parseFloat(card.querySelector('.product-price').textContent.replace(/[^\d.]/g, '')) || 0,
      img: card.querySelector('img').src,
      quantity: 1
    };

    // 调用购物车逻辑
    Cart.addToCart(product);
    showToast(`${product.name} 已加入购物车`);

    // 飞入购物车动画
    const imgElement = card.querySelector('img');
    const imgRect = imgElement.getBoundingClientRect();

    const flyImg = imgElement.cloneNode(true);
    flyImg.classList.add('fly-img');
    flyImg.style.position = 'fixed';
    flyImg.style.left = imgRect.left + 'px';
    flyImg.style.top = imgRect.top + 'px';
    flyImg.style.width = imgRect.width + 'px';
    flyImg.style.height = imgRect.height + 'px';
    flyImg.style.transform = 'translate(0,0) scale(1)';
    flyImg.style.opacity = '1';
    document.body.appendChild(flyImg);

    flyImg.getBoundingClientRect(); // 强制回流

    const cartRect = cartIcon ? cartIcon.getBoundingClientRect() : { left: window.innerWidth - 40, top: 20, width: 24, height: 24 };
    const fromCenterX = imgRect.left + imgRect.width / 2;
    const fromCenterY = imgRect.top + imgRect.height / 2;
    const toCenterX = cartRect.left + (cartRect.width || 24) / 2;
    const toCenterY = cartRect.top + (cartRect.height || 24) / 2;
    const dx = toCenterX - fromCenterX;
    const dy = toCenterY - fromCenterY;

    requestAnimationFrame(() => {
      flyImg.style.transform = `translate(${dx}px, ${dy}px) scale(0.2)`;
      flyImg.style.opacity = '0.25';
    });

    flyImg.addEventListener('transitionend', () => {
      flyImg.remove();
      if (cartIcon) {
        cartIcon.classList.add('hit');
        setTimeout(() => cartIcon.classList.remove('hit'), 600);
      }
    });

    function showToast(msg) {
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.textContent = msg;
  document.body.appendChild(toast);

  // 出现动画
  requestAnimationFrame(() => toast.classList.add('show'));

  // 自动消失
  setTimeout(() => {
    toast.classList.remove('show');
    toast.addEventListener('transitionend', () => toast.remove());
  }, 2000);
}
  });
});
