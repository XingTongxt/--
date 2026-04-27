import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { addToCartBackend } from './cart.js'

export function useProductDetail() {
  const route = useRoute()
  const router = useRouter()

  const product = ref(null)
  const qty = ref(1)
  const loading = ref(false)

  const comments = ref([])
  const commentContent = ref('')
  const rating = ref(5)
  const submitLoading = ref(false)

  const productId = Number(route.params.id)

  const avgRating = computed(() => {
    if (comments.value.length === 0) return 0
    return comments.value.reduce((sum, c) => sum + c.rating, 0) / comments.value.length
  })

  function increaseQty() {
    qty.value++
  }

  function decreaseQty() {
    if (qty.value > 1) qty.value--
  }

  async function fetchProduct() {
    try {
      const res = await fetch(`http://localhost:8080/api/products/${productId}`)
      if (!res.ok) throw new Error()
      product.value = await res.json()
    } catch (e) {
      showToast('获取商品失败')
    }
  }

  async function addCart() {
    const token = localStorage.getItem('token')
    if (!token) {
      showToast('请先登录')
      router.push('/login')
      return
    }

    loading.value = true
    try {
      await addToCartBackend(product.value.id, qty.value)
      showToast('加入购物车成功')
    } catch (e) {
      showToast('加入失败')
    } finally {
      loading.value = false
    }
  }

  async function loadComments() {
    try {
      const res = await fetch(`http://localhost:8080/api/comments/${productId}`)
      if (!res.ok) throw new Error()
      comments.value = await res.json()
    } catch (e) {
      showToast('评论加载失败')
    }
  }

  function isMine(c) {
    const username = localStorage.getItem('username')
    return username && c.username === username
  }

  async function deleteComment(id) {
    try {
      const res = await fetch(`http://localhost:8080/api/comments/${id}`, {
        method: 'DELETE',
        headers: { Authorization: 'Bearer ' + localStorage.getItem('token') }
      })
      if (res.ok) {
        showToast('删除成功')
        loadComments()
      } else {
        showToast('删除失败')
      }
    } catch (e) {
      showToast('网络错误')
    }
  }
function showToast(msg) {
  let toast = document.querySelector('.toast')

  if (!toast) {
    toast = document.createElement('div')
    toast.className = 'toast'
    document.body.appendChild(toast)
  }

  toast.textContent = msg
  toast.classList.add('show')

  setTimeout(() => {
    toast.classList.remove('show')
  }, 2000)
}
  function formatTime(t) {
    if (!t) return ''
    return new Date(t.replace('T', ' ')).toLocaleString()
  }

  async function submitComment() {
    const token = localStorage.getItem('token')
    if (!token) return showToast('请先登录')
    if (!commentContent.value) return showToast('评论不能为空')

    submitLoading.value = true
    try {
      const res = await fetch('http://localhost:8080/api/comments', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: 'Bearer ' + token
        },
        body: JSON.stringify({
          productId,
          content: commentContent.value,
          rating: rating.value
        })
      })

      if (res.status === 401) {
        showToast('登录过期')
        localStorage.removeItem('token')
        router.push('/login')
        return
      }

      if (res.ok) {
        showToast('评论成功')
        commentContent.value = ''
        loadComments()
      } else {
        showToast('评论失败')
      }
    } catch (e) {
      showToast('网络错误')
    } finally {
      submitLoading.value = false
    }
  }

  onMounted(() => {
    fetchProduct()
    loadComments()
  })

  return {
    product,
    qty,
    loading,
    comments,
    commentContent,
    rating,
    submitLoading,
    avgRating,
    router,
    increaseQty,
    decreaseQty,
    addCart,
    loadComments,
    isMine,
    deleteComment,
    formatTime,
    submitComment
  }
}