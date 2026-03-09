<template>
  <div class="manage-items-page">
    <h2>物品管理</h2>

    <!-- 新增物品表单 -->
    <form @submit.prevent="submitForm" class="add-item-form">
      <input v-model="newItem.name" type="text" placeholder="物品名称" required>
      <input v-model.number="newItem.price" type="number" placeholder="价格" required>
      <input v-model="newItem.category" type="text" placeholder="分类" required>
      <input v-model="newItem.img" type="text" placeholder="图片URL" required>
      <button type="submit">新增物品</button>
    </form>

    <!-- 物品表格 -->
    <div class="table-container">
      <table id="item-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>价格</th>
            <th>分类</th>
            <th>图片</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.price }}</td>
            <td>{{ item.category }}</td>
            <td>
              <img :src="item.img" alt="物品图片" class="item-img">
            </td>
            <td>
              <button @click="editItemHandler(item)">修改</button>
              <button @click="deleteItemHandler(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <router-link to="/admin/dashboard" class="back-link">返回后台主页</router-link>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { loadItems, addItem, editItem, deleteItem } from '../assets/js/manage-items.js'

const token = localStorage.getItem('token')
const items = ref([])

const newItem = ref({
  name: '',
  price: 0,
  category: '',
  img: ''
})

// 渲染回调
function renderItemsCallback(fetchedItems) {
  items.value = fetchedItems
}

// 页面加载时获取物品列表
onMounted(() => {
  loadItems(token, renderItemsCallback)
})

// 新增物品
function submitForm() {
  addItem(token, newItem.value, () => {
    loadItems(token, renderItemsCallback)
    newItem.value = { name: '', price: 0, category: '', img: '' }
  })
}

// 修改物品
function editItemHandler(item) {
  const name = prompt('请输入新名称:', item.name)
  if (name === null) return
  const price = parseFloat(prompt('请输入新价格:', item.price))
  if (isNaN(price)) return alert('价格无效')
  const category = prompt('请输入新分类:', item.category)
  if (category === null) return
  const img = prompt('请输入新图片URL:', item.img)
  if (img === null) return

  editItem(token, item.id, { name, price, category, img }, () => {
    loadItems(token, renderItemsCallback)
  })
}

// 删除物品
function deleteItemHandler(id) {
  deleteItem(token, id, () => loadItems(token, renderItemsCallback))
}
</script>

<style>
@import "/src/assets/css/manage-items.css";
</style>