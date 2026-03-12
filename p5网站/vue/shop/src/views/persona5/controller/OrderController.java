package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.Order;
import com.guanyanliang.persona5.entity.OrderItem;
import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.OrderItemRepository;
import com.guanyanliang.persona5.repository.OrderRepository;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    // ===== 用户查看自己的订单（管理员可查看全部） =====
    @GetMapping("/my")
    public ResponseEntity<?> getUserOrders(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) return ResponseEntity.status(401).body("未登录或 token 无效");

        List<Map<String, Object>> result;

        if ("ADMIN".equals(user.getRole()) || "SUPERADMIN".equals(user.getRole())) {
            // 管理员：查看所有订单
            List<Order> orders = orderRepository.findAll();
            result = orders.stream().map(order -> {
                List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                User orderUser = userService.findById(order.getUserId()).orElse(null);

                Map<String, Object> map = new java.util.HashMap<>();
                map.put("order", order);
                map.put("items", items);
                map.put("user", orderUser != null ? orderUser : new User());
                map.put("returnStatus", order.getReturnStatus() != null ? order.getReturnStatus() : "NONE");
                map.put("refundStatus", order.getRefundStatus() != null ? order.getRefundStatus() : "NONE");
                return map;
            }).toList();
        } else {
            // 普通用户：只看自己的
            List<Order> orders = orderRepository.findByUserId(user.getId());
            result = orders.stream().map(order -> {
                List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

                Map<String, Object> map = new java.util.HashMap<>();
                map.put("order", order);
                map.put("items", items);
                map.put("user", user);
                map.put("returnStatus", order.getReturnStatus() != null ? order.getReturnStatus() : "NONE");
                map.put("refundStatus", order.getRefundStatus() != null ? order.getRefundStatus() : "NONE");
                return map;
            }).toList();
        }

        return ResponseEntity.ok(result);
    }

    // ===== 管理员发货 =====
    @PutMapping("/{orderId}/ship")
    public ResponseEntity<?> shipOrder(@RequestHeader("Authorization") String authHeader,
                                       @PathVariable Long orderId) {
        if (!isAdminOrSuperAdmin(authHeader)) {
            return ResponseEntity.status(403).body("权限不足");
        }

        return orderRepository.findById(orderId).map(order -> {
            order.setStatus("SHIPPED");
            order.setShipTime(LocalDateTime.now());
            orderRepository.save(order);
            return ResponseEntity.ok("订单已发货");
        }).orElse(ResponseEntity.status(404).body("订单不存在"));
    }

    // ===== 用户申请退货 =====
    @PutMapping("/{orderId}/return")
    public ResponseEntity<?> applyReturn(@PathVariable Long orderId,
                                         @RequestHeader("Authorization") String authHeader) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) return ResponseEntity.status(401).body("未登录");

        return orderRepository.findById(orderId).map(order -> {
            if (!order.getUserId().equals(user.getId()))
                return ResponseEntity.status(403).body("无权限操作");

            order.setReturnStatus("PENDING"); // 申请中
            orderRepository.save(order);
            return ResponseEntity.ok("退货申请已提交");
        }).orElse(ResponseEntity.status(404).body("订单不存在"));
    }

    // ===== 用户申请退款 =====
    @PutMapping("/{orderId}/refund")
    public ResponseEntity<?> applyRefund(@PathVariable Long orderId,
                                         @RequestHeader("Authorization") String authHeader) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) return ResponseEntity.status(401).body("未登录");

        return orderRepository.findById(orderId).map(order -> {
            if (!order.getUserId().equals(user.getId()))
                return ResponseEntity.status(403).body("无权限操作");

            order.setRefundStatus("PENDING"); // 申请中
            orderRepository.save(order);
            return ResponseEntity.ok("退款申请已提交");
        }).orElse(ResponseEntity.status(404).body("订单不存在"));
    }

    // ===== 管理员批准退货 =====
    @PutMapping("/{orderId}/approveReturn")
    public ResponseEntity<?> approveReturn(@PathVariable Long orderId,
                                           @RequestHeader("Authorization") String authHeader) {
        if (!isAdminOrSuperAdmin(authHeader)) return ResponseEntity.status(403).body("权限不足");

        return orderRepository.findById(orderId).map(order -> {
            order.setReturnStatus("APPROVED");
            orderRepository.save(order);
            return ResponseEntity.ok("退货已批准");
        }).orElse(ResponseEntity.status(404).body("订单不存在"));
    }

    // ===== 管理员批准退款 =====
    @PutMapping("/{orderId}/approveRefund")
    public ResponseEntity<?> approveRefund(@PathVariable Long orderId,
                                           @RequestHeader("Authorization") String authHeader) {
        if (!isAdminOrSuperAdmin(authHeader)) return ResponseEntity.status(403).body("权限不足");

        return orderRepository.findById(orderId).map(order -> {
            order.setRefundStatus("APPROVED");
            orderRepository.save(order);
            return ResponseEntity.ok("退款已批准");
        }).orElse(ResponseEntity.status(404).body("订单不存在"));
    }

    // ===== 工具方法：解析用户 =====
    private User getUserFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        try {
            String username = JwtUtil.getUsername(token);
            return userService.findByUsername(username).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===== 权限判断 =====
    private boolean isAdminOrSuperAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        String token = authHeader.substring(7);
        String role = JwtUtil.getRole(token);
        return "ADMIN".equals(role) || "SUPERADMIN".equals(role);
    }

}