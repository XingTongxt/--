let vueRouter = null
let cartItemsEl = null
let cartTotalEl = null

function getToken() {
  return localStorage.getItem('token')
}

export function initCartJS(itemsEl, totalEl, router) {
  vueRouter = router
  cartItemsEl = itemsEl
  cartTotalEl = totalEl

  const token = getToken()
  if (!token) {
    alert('请先登录')
    if (vueRouter) vueRouter.push('/login')
    return
  }

  loadCart()
}

export async function loadCart() {
  const token = getToken()
  if (!token) return

  try {
    const res = await fetch('http://localhost:8080/api/cart', {
      headers: { 'Authorization': 'Bearer ' + token }
    })
    if (!res.ok) {
      const text = await res.text()
      console.error('加载购物车失败:', res.status, text)
      return
    }

    const cartList = await res.json()
    if (!Array.isArray(cartList)) return

    updateCartCount(cartList)
    renderCart(cartList)

  } catch (err) {
    console.error('加载购物车失败', err)
  }
}


export function updateCartCount(cartList) {
  const cartIcon = document.querySelector('.cart-icon .cart-count')
  if (!cartIcon) return

  const total = cartList.reduce((sum, item) => sum + item.quantity, 0)
  cartIcon.textContent = total
}


export function renderCart(cartList) {
  if (!cartItemsEl) return
  cartItemsEl.innerHTML = ''

  if (cartList.length === 0) {
    cartItemsEl.innerHTML = '<p>购物车为空</p>'
    if (cartTotalEl) cartTotalEl.textContent = ''
    return
  }

  cartList.forEach(item => {
    const div = document.createElement('div')
    div.className = 'cart-item'
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
    `
    cartItemsEl.appendChild(div)

    const decreaseBtn = div.querySelector('.btn-decrease')
    const increaseBtn = div.querySelector('.btn-increase')
    const qtySpan = div.querySelector('.item-qty')
    const removeBtn = div.querySelector('.btn-remove')

    decreaseBtn.addEventListener('click', async () => {
      const newQty = Math.max(1, item.quantity - 1)
      await updateCartQuantity(item.productId, newQty)
    })

    increaseBtn.addEventListener('click', async () => {
      const newQty = item.quantity + 1
      await updateCartQuantity(item.productId, newQty)
    })

    removeBtn.addEventListener('click', async () => {
      await removeCartItem(item.productId)
    })
  })

  if (cartTotalEl) {
    const total = cartList.reduce((sum, item) => sum + item.price * item.quantity, 0)
    cartTotalEl.textContent = `总价: ¥${total.toFixed(2)}`
  }
}


export async function addToCartBackend(productId) {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    return
  }

  try {
    const res = await fetch('http://localhost:8080/api/cart', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      },
      body: JSON.stringify({ productId })
    })

    if (!res.ok) {
      const text = await res.text()
      console.error('加入购物车失败:', text)
      return
    }

    const cartList = await res.json()
    // 更新购物车显示
    updateCartCount(cartList)
    if (vueRouter) loadCart()  // 重新加载购物车
  } catch (err) {
    console.error('加入购物车失败', err)
  }
}

// ===== 修改购物车商品数量 =====
export async function updateCartQuantity(productId, quantity) {
  const token = getToken()
  if (!token) {
    alert('请先登录')
    return
  }

  try {
    const res = await fetch(`http://localhost:8080/api/cart/${productId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      },
      body: JSON.stringify({ quantity })
    })
    if (!res.ok) {
      const text = await res.text()
      alert('更新失败: ' + text)
      return
    }

    const cartList = await res.json()
    updateCartCount(cartList)
    renderCart(cartList)

  } catch (err) {
    console.error('更新购物车失败', err)
    alert('更新购物车数量失败')
  }
}

// ===== 删除购物车商品 =====
export async function removeCartItem(productId) {
  const token = getToken()
  if (!token) {
    alert('请先登录')
    return
  }

  try {
    const res = await fetch(`http://localhost:8080/api/cart/${productId}`, {
      method: 'DELETE',
      headers: { 'Authorization': 'Bearer ' + token }
    })
    if (!res.ok) {
      const text = await res.text()
      alert('删除失败: ' + text)
      return
    }

    const cartList = await res.json()
    updateCartCount(cartList)
    renderCart(cartList)
  } catch (err) {
    console.error('删除购物车失败', err)
    alert('删除购物车商品失败，请重试')
  }
}

// ===== 结算购物车 =====
export function checkoutCart(router) {
  const token = getToken()
  if (!token) {
    alert('请先登录')
    if (router) router.push('/login')
    return
  }

  alert('结算成功！')
  localStorage.removeItem('cart')
  if (router) router.push('/shop')
  else window.location.reload()
}