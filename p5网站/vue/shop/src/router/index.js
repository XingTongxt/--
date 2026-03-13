import { createRouter, createWebHistory } from "vue-router"
import Home from "../views/Home.vue"
import Detail from '../views/Detail.vue';   

const routes = [
  { path: "/", component: Home },
  { path: "/login", component: () => import("../views/Login.vue") },
  { path: "/shop", component: () => import("../views/Shop.vue") },
  { path: "/about", component: () => import("../views/About.vue") },
  { path: "/admin", component: () => import("../views/Admin.vue") },
  { path: "/register", component: () => import("../views/Register.vue") },
  { path: "/user", component: () => import("../views/User.vue") },
  { path: "/cart", component: () => import("../views/Cart.vue") },
  { path: "/orders", component: () => import("../views/OrdersPage.vue") }, 
  { path: "/admin/dashboard", component: () => import("../views/Admin-dashboard.vue") },
  { path: "/admin/manage-users", component: () => import("../views/Manage-users.vue") },
  { path: "/admin/manage-items", component: () => import("../views/Manage-items.vue") },
  { path: "/admin/manage-logs", component: () => import("../views/Manage-logs.vue") },
  { path: "/detail/:id", component: Detail }, 
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router