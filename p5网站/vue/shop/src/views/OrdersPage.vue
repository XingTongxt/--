<template>
  <div class="orders-page">
    <div class="header">
      <h2>我的订单</h2>
      <router-link to="/shop" class="back-btn">返回商城</router-link>
    </div>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="orders.length === 0" class="empty-order">暂无订单</div>

    <div v-else class="orders-container">
      <div v-for="orderWrapper in orders" :key="orderWrapper?.order?.id" class="order-card">
        <template v-if="orderWrapper?.order">
          <div class="order-header">
            <span>订单ID: {{ orderWrapper.order.id }}</span>
            <span>用户: {{ orderWrapper.user.username }}</span>
            <span>总价: ¥{{ orderWrapper.order.totalPrice.toFixed(2) }}</span>
            <span :class="getStatusClass(orderWrapper.order.status)">
              状态: {{ getStatusText(orderWrapper.order.status) }}
            </span>
            <span>创建时间: {{ formatDate(orderWrapper.order.createTime) }}</span>
            <span v-if="orderWrapper.order.shipTime">
              发货时间: {{ formatDate(orderWrapper.order.shipTime) }}
            </span>
            <button v-if="isAdmin && orderWrapper.order.status === 'PAID'" @click="shipOrder(orderWrapper.order.id)"
              :disabled="shippingOrderId === orderWrapper.order.id">
              {{ shippingOrderId === orderWrapper.order.id ? '发货中...' : '发货' }}
            </button>
          </div>

          <div class="order-actions" v-if="!isAdmin">
            <template v-if="orderWrapper.order.status === 'PAID'">
              <span v-if="orderWrapper.refundStatus && orderWrapper.refundStatus !== 'NONE'">
                退款状态: {{ getRequestStatusText(orderWrapper.refundStatus) }}
              </span>
              <button v-if="!orderWrapper.refundStatus || orderWrapper.refundStatus === 'NONE'"
                @click="applyRefund(orderWrapper.order.id)">
                申请退款
              </button>
            </template>

            <template v-else-if="orderWrapper.order.status === 'SHIPPED'">
              <span v-if="orderWrapper.returnStatus && orderWrapper.returnStatus !== 'NONE'">
                退货状态: {{ getRequestStatusText(orderWrapper.returnStatus) }}
              </span>
              <button v-if="!orderWrapper.returnStatus || orderWrapper.returnStatus === 'NONE'"
                @click="applyReturn(orderWrapper.order.id)">
                申请退货
              </button>

              <span v-if="orderWrapper.refundStatus && orderWrapper.refundStatus !== 'NONE'">
                退款状态: {{ getRequestStatusText(orderWrapper.refundStatus) }}
              </span>
              <button v-if="!orderWrapper.refundStatus || orderWrapper.refundStatus === 'NONE'"
                @click="applyRefund(orderWrapper.order.id)">
                申请退款
              </button>
            </template>
          </div>

          <div class="order-actions" v-if="isAdmin">
            <span v-if="orderWrapper.returnStatus && orderWrapper.returnStatus !== 'NONE'">
              退货状态: {{ getRequestStatusText(orderWrapper.returnStatus) }}
            </span>
            <span v-if="orderWrapper.refundStatus && orderWrapper.refundStatus !== 'NONE'">
              退款状态: {{ getRequestStatusText(orderWrapper.refundStatus) }}
            </span>
            <button v-if="orderWrapper.returnStatus === 'PENDING'" @click="approveReturn(orderWrapper.order.id)">
              批准退货
            </button>
            <button v-if="orderWrapper.refundStatus === 'PENDING'" @click="approveRefund(orderWrapper.order.id)">
              批准退款
            </button>
          </div>

          <table class="order-items" v-if="orderWrapper.items?.length">
            <thead>
              <tr>
                <th>商品名</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in orderWrapper.items" :key="item.id">
                <td>{{ item.productName }}</td>
                <td>¥{{ item.price.toFixed(2) }}</td>
                <td>{{ item.quantity }}</td>
                <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
              </tr>
            </tbody>
          </table>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const orders = ref([]);
const isAdmin = ref(false);
const loading = ref(true);
const shippingOrderId = ref(null);
const token = localStorage.getItem("token") || "";

function formatDate(dateStr) {
  if (!dateStr) return "";
  try {
    return new Date(dateStr).toLocaleString('zh-CN', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit', second: '2-digit'
    });
  } catch { return dateStr; }
}

function getStatusText(status) {
  const statusMap = {
    'PAID': '已支付',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELED': '已取消',
    'PENDING': '待支付'
  };
  return statusMap[status] || status;
}

function getStatusClass(status) {
  const classMap = {
    'PAID': 'status-paid',
    'SHIPPED': 'status-shipped',
    'COMPLETED': 'status-completed',
    'CANCELED': 'status-canceled',
    'PENDING': 'status-pending'
  };
  return classMap[status] || '';
}

function getRequestStatusText(status) {
  const map = {
    'NONE': '无申请',
    'PENDING': '待审批',
    'APPROVED': '已批准'
  };
  return map[status] || status;
}

async function fetchOrders() {
  if (!token) { loading.value = false; return; }
  try {
    loading.value = true;
    const res = await axios.get("http://localhost:8080/api/orders/my", {
      headers: { Authorization: "Bearer " + token }
    });
    orders.value = Array.isArray(res.data) ? res.data : [];

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      isAdmin.value = ['ADMIN', 'SUPERADMIN'].includes(payload.role);
    } catch { isAdmin.value = false; }
  } catch (err) {
    console.error("获取订单失败", err);
    alert("获取订单失败：" + (err.response?.data?.message || err.message));
    orders.value = [];
  } finally {
    loading.value = false;
  }
}

async function shipOrder(orderId) {
  if (!confirm("确定发货吗？") || shippingOrderId.value) return;
  try {
    shippingOrderId.value = orderId;
    await axios.put(`http://localhost:8080/api/orders/${orderId}/ship`, {}, {
      headers: { Authorization: "Bearer " + token }
    });
    alert("发货成功");
    await fetchOrders();
  } catch (err) {
    console.error("发货失败", err);
    alert("发货失败：" + (err.response?.data?.message || err.message));
  } finally {
    shippingOrderId.value = null;
  }
}

async function applyReturn(orderId) {
  if (!confirm("确定申请退货吗？")) return;
  try {
    await axios.put(`http://localhost:8080/api/orders/${orderId}/return`, {}, {
      headers: { Authorization: "Bearer " + token }
    });
    alert("退货申请已提交");
    await fetchOrders();
  } catch (err) {
    console.error("退货申请失败", err);
    alert("退货申请失败：" + (err.response?.data?.message || err.message));
  }
}

async function applyRefund(orderId) {
  if (!confirm("确定申请退款吗？")) return;
  try {
    await axios.put(`http://localhost:8080/api/orders/${orderId}/refund`, {}, {
      headers: { Authorization: "Bearer " + token }
    });
    alert("退款申请已提交");
    await fetchOrders();
  } catch (err) {
    console.error("退款申请失败", err);
    alert("退款申请失败：" + (err.response?.data?.message || err.message));
  }
}

async function approveReturn(orderId) {
  if (!confirm("确定批准该退货申请吗？")) return;
  try {
    await axios.put(`http://localhost:8080/api/orders/${orderId}/approveReturn`, {}, {
      headers: { Authorization: "Bearer " + token }
    });
    alert("退货已批准");
    await fetchOrders();
  } catch (err) {
    console.error("批准退货失败", err);
    alert("批准退货失败：" + (err.response?.data?.message || err.message));
  }
}

async function approveRefund(orderId) {
  if (!confirm("确定批准该退款申请吗？")) return;
  try {
    await axios.put(`http://localhost:8080/api/orders/${orderId}/approveRefund`, {}, {
      headers: { Authorization: "Bearer " + token }
    });
    alert("退款已批准");
    await fetchOrders();
  } catch (err) {
    console.error("批准退款失败", err);
    alert("批准退款失败：" + (err.response?.data?.message || err.message));
  }
}

onMounted(fetchOrders);
</script>

<style scoped>
.orders-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.back-btn {
  padding: 5px 12px;
  background-color: #1976d2;
  color: white;
  border-radius: 4px;
  text-decoration: none;
}

.back-btn:hover {
  background-color: #1565c0;
}

.orders-container {
  flex: 1;
  overflow-y: auto;
  padding-right: 10px;
}

.empty-order,
.loading {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 16px;
}

.order-card {
  border: 1px solid #ddd;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 6px;
  background: #dbb7b7;
}

.order-header {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  margin-bottom: 10px;
  align-items: center;
}

.order-header button {
  background-color: #4caf50;
  color: rgb(243, 185, 185);
  border: none;
  padding: 5px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.order-header button:hover {
  background-color: #45a049;
}

.order-header button:disabled {
  background-color: #f4c8c8;
  cursor: not-allowed;
}

.order-actions {
  margin-top: 10px;
}

.order-actions button {
  background-color: #ff9800;
  color: white;
  border: none;
  padding: 5px 12px;
  margin-right: 10px;
  border-radius: 4px;
  cursor: pointer;
}

.order-actions button:hover {
  background-color: #f57c00;
}

.order-items {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.order-items th,
.order-items td {
  border: 1px solid #a17f7f;
  padding: 8px;
  text-align: left;
  color: #000;
}

.order-items th {
  background-color: #e0b4b4;
}

.status-paid {
  color: #ff9800;
  font-weight: 500;
}

.status-shipped {
  color: #2196f3;
  font-weight: 500;
}

.status-completed {
  color: #4caf50;
  font-weight: 500;
}

.status-canceled {
  color: #f44336;
  font-weight: 500;
}

.status-pending {
  color: #9c27b0;
  font-weight: 500;
}
</style>