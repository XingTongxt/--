import { addToCartBackend, loadCart, updateCartCount } from './cart.js'

let shopScrollContainer = null
let userRole = "USER"

export async function initShop() {
  shopScrollContainer = document.querySelector('.shop-container')
  const cartIcon = document.querySelector('.cart-icon')
  const token = localStorage.getItem('token')

  // 先获取角色
  await checkAdmin(token)

  try {
    const res = await fetch('http://localhost:8080/api/products')
    if (!res.ok) throw new Error('获取商品失败')
    const products = await res.json()
    renderProducts(products)
  } catch (err) {
    console.error('获取商品失败:', err)
  }

  initShopJsEvents(token, cartIcon)

  if (token) await loadCart(token)

  const searchBtn = document.getElementById('search-btn')
  const searchInput = document.getElementById('search')

  if (searchBtn) searchBtn.addEventListener('click', searchProducts)

  if (searchInput) {
    searchInput.addEventListener('keydown', e => {
      if (e.key === 'Enter') searchProducts()
    })
  }
}

async function renderProducts(products) {
  if (!shopScrollContainer) return
  shopScrollContainer.innerHTML = ''

  // 先获取所有商品的平均评分
  const productsWithRating = await Promise.all(products.map(async product => {
    try {
      const res = await fetch(`http://localhost:8080/api/comments/${product.id}`)
      const comments = res.ok ? await res.json() : []
      const avgRating = comments.length > 0
        ? comments.reduce((sum, c) => sum + c.rating, 0) / comments.length
        : 0
      return { ...product, avgRating }
    } catch (err) {
      console.error(err)
      return { ...product, avgRating: 0 }
    }
  }))

  // 按分类分组
  const categories = {}
  productsWithRating.forEach(product => {
    if (!categories[product.category]) categories[product.category] = []
    categories[product.category].push(product)
  })

  // 渲染
  for (const category in categories) {
    const categoryDiv = document.createElement('div')
    categoryDiv.className = 'category-container'
    categoryDiv.dataset.category = category

    const title = document.createElement('h2')
    title.className = 'category-title'
    title.textContent = category === 'recommended' ? '推荐' : category
    categoryDiv.appendChild(title)

    const productList = document.createElement('div')
    productList.className = 'product-list'

    categories[category].forEach(product => {
      const card = document.createElement('div')
      card.className = 'product-card'
      card.dataset.category = category

      let stockHtml = ""
      if (userRole === "ADMIN" || userRole === "SUPERADMIN") {
        stockHtml = `<p class="product-stock">库存：${product.stock}</p>`
      }

      // 生成星星
      const fullStars = Math.floor(product.avgRating)
      const halfStar = product.avgRating - fullStars >= 0.5 ? 1 : 0
      const emptyStars = 5 - fullStars - halfStar
      const starStr = "⭐".repeat(fullStars) + (halfStar ? "✰" : "") + "☆".repeat(emptyStars)

      card.innerHTML = `
        <a href="/detail/${product.id}">
          <img src="${product.img}" alt="${product.name}">
          <h3 class="product-title">${product.name}</h3>
        </a>
        <p class="product-price">¥${product.price}</p>
        <p class="product-sales">销量：${product.sales}</p>
        ${stockHtml}
        <div class="product-rating">${starStr} (${product.avgRating.toFixed(1)})</div>
        <button class="btn-add" data-id="${product.id}">加入购物车</button>
      `
      productList.appendChild(card)
    })

    categoryDiv.appendChild(productList)
    shopScrollContainer.appendChild(categoryDiv)
  }
}
function initShopJsEvents(token, cartIcon) {

  if (!shopScrollContainer) return

  const top = document.querySelector('#back-to-top')

  if (top) {
    top.addEventListener('click', () => {
      shopScrollContainer.scrollTo({ top: 0, behavior: 'smooth' })
    })
  }

  const List = document.querySelector('.elevate-list')

  if (List) {

    List.addEventListener('click', e => {

      if (e.target.tagName !== 'A') return

      e.preventDefault()

      const targetCategory = e.target.parentElement.id

      const categoryContainer = document.querySelector(`.category-container[data-category="${targetCategory}"]`)

      document.querySelectorAll('.elevate-list a').forEach(link => link.classList.remove('active'))

      e.target.classList.add('active')

      if (categoryContainer) {

        shopScrollContainer.scrollTo({
          top: categoryContainer.offsetTop - 50,
          behavior: 'smooth'
        })

      }

    })

    let scrollDebounceTimer = null

    shopScrollContainer.addEventListener('scroll', () => {

      clearTimeout(scrollDebounceTimer)

      scrollDebounceTimer = setTimeout(() => {

        const scrollTop = shopScrollContainer.scrollTop

        document.querySelectorAll('.category-container').forEach(container => {

          const top = container.offsetTop - 100
          const bottom = top + container.offsetHeight

          if (scrollTop >= top && scrollTop < bottom) {

            document.querySelectorAll('.elevate-list a').forEach(link => link.classList.remove('active'))

            const navItem = document.querySelector(`.elevate-list #${container.dataset.category} a`)

            if (navItem) navItem.classList.add('active')

          }

        })

      }, 50)

    })

  }

  shopScrollContainer.addEventListener('click', e => {

    const btn = e.target.closest('.btn-add')

    if (!btn) return

    if (!token) {
      showToast('请先登录才能加入购物车')
      return
    }

    const card = btn.closest('.product-card')

    const productId = btn.dataset.id

    addToCartBackend(productId, token)

    if (cartIcon) {

      const imgElement = card.querySelector('img')

      flyToCart(imgElement, cartIcon)

    }

    const productName = card.querySelector('.product-title').textContent

    showToast(`${productName} 已加入购物车`)

  })

}

export function searchProducts() {

  const query = document.getElementById("search").value.toLowerCase()

  const productCards = document.querySelectorAll('.product-card')

  productCards.forEach(card => {

    const title = card.querySelector('.product-title').textContent.toLowerCase()

    card.style.display = title.includes(query) ? "block" : "none"

  })

}

export async function loadProductsBySales() {

  const res = await fetch("http://localhost:8080/api/products/sales")

  const products = await res.json()

  renderProducts(products)

}
export async function loadAllProducts() {

  const res = await fetch("http://localhost:8080/api/products")

  const products = await res.json()

  renderProducts(products)

}
export async function loadProductsByPriceAsc() {

  const res = await fetch("http://localhost:8080/api/products/priceAsc")

  const products = await res.json()

  renderProducts(products)

}

export async function loadProductsByPriceDesc() {

  const res = await fetch("http://localhost:8080/api/products/priceDesc")

  const products = await res.json()

  renderProducts(products)

}
// 按评分排序
export async function loadProductsByRating(desc = true) {
  try {
    // 先获取所有商品
    const res = await fetch("http://localhost:8080/api/products")
    if (!res.ok) throw new Error("获取商品失败")
    const products = await res.json()

    // 获取每个商品的平均评分
    const productsWithRating = await Promise.all(products.map(async product => {
      try {
        const res = await fetch(`http://localhost:8080/api/comments/${product.id}`)
        const comments = res.ok ? await res.json() : []
        const avgRating = comments.length > 0
          ? comments.reduce((sum, c) => sum + c.rating, 0) / comments.length
          : 0
        return { ...product, avgRating }
      } catch (err) {
        console.error(err)
        return { ...product, avgRating: 0 }
      }
    }))

    // 排序
    productsWithRating.sort((a, b) => desc ? b.avgRating - a.avgRating : a.avgRating - b.avgRating)

    // 渲染
    renderProducts(productsWithRating)
  } catch (err) {
    console.error(err)
  }
}
function flyToCart(imgElement, cartIcon) {

  const imgRect = imgElement.getBoundingClientRect()

  const flyImg = imgElement.cloneNode(true)

  flyImg.classList.add('fly-img')

  Object.assign(flyImg.style, {
    position: 'fixed',
    left: imgRect.left + 'px',
    top: imgRect.top + 'px',
    width: imgRect.width + 'px',
    height: imgRect.height + 'px',
    transform: 'translate(0,0) scale(1)',
    opacity: '1',
    transition: 'transform 0.6s ease, opacity 0.6s ease'
  })

  document.body.appendChild(flyImg)

  const cartRect = cartIcon.getBoundingClientRect()

  const dx = cartRect.left + cartRect.width / 2 - (imgRect.left + imgRect.width / 2)
  const dy = cartRect.top + cartRect.height / 2 - (imgRect.top + imgRect.height / 2)

  requestAnimationFrame(() => {

    flyImg.style.transform = `translate(${dx}px, ${dy}px) scale(0.2)`
    flyImg.style.opacity = '0.25'

  })

  flyImg.addEventListener('transitionend', () => {

    flyImg.remove()

    cartIcon.classList.add('hit')

    setTimeout(() => cartIcon.classList.remove('hit'), 600)

  })

}

function showToast(msg) {

  const toast = document.createElement('div')

  toast.className = 'toast'

  toast.textContent = msg

  document.body.appendChild(toast)

  requestAnimationFrame(() => toast.classList.add('show'))

  setTimeout(() => {

    toast.classList.remove('show')

    toast.addEventListener('transitionend', () => toast.remove())

  }, 2000)

}

async function checkAdmin(token) {

  if (!token) return

  try {

    let res = await fetch("http://localhost:8080/api/user/info", {
      headers: { "Authorization": "Bearer " + token }
    })

    if (!res.ok) {
      res = await fetch("http://localhost:8080/admin/info", {
        headers: { "Authorization": "Bearer " + token }
      })
    }

    if (res.ok) {

      const data = await res.json()

      userRole = data.role ? data.role.toUpperCase() : "USER"

      if (userRole === "ADMIN" || userRole === "SUPERADMIN") {

        const adminBtn = document.createElement("a")

        adminBtn.href = "/admin/dashboard"
        adminBtn.id = "admin-btn"
        adminBtn.textContent = "后台管理"

        document.querySelector(".shop-nav").appendChild(adminBtn)

      }

    }

  } catch (err) {

    console.error("获取用户信息失败", err)

  }

}