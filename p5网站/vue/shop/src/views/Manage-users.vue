<template>
  <div class="manage-users-page">
    <h2>用户管理</h2>

    <div class="table-container">
      <table id="user-table">
        <thead>
          <tr>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.username">
            <td>{{ user.username }}</td>
            <td>{{ user.email || '未绑定' }}</td>
            <td>
              <select v-model="user.role">
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </td>
            <td>
              <button @click="changeRoleHandler(user)">修改角色</button>
              <button @click="deleteUserHandler(user)">删除</button>
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
import { loadUsers, changeRole, deleteUser } from '../assets/js/manage-users.js'

const users = ref([])
const token = localStorage.getItem('token')

// 渲染函数
function renderUsersCallback(fetchedUsers) {
  users.value = fetchedUsers
}

// 修改角色
function changeRoleHandler(user) {
  changeRole(token, user.username, user.role, () => loadUsers(token, renderUsersCallback))
}

// 删除用户
function deleteUserHandler(user) {
  deleteUser(token, user.username, () => loadUsers(token, renderUsersCallback))
}

// 页面加载时获取用户列表
onMounted(() => {
  loadUsers(token, renderUsersCallback)
})
</script>

<style>
@import "/src/assets/css/manage-users.css";
</style>