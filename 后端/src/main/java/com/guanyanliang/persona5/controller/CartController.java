package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.dto.CartDTO;
import com.guanyanliang.persona5.entity.*;
import com.guanyanliang.persona5.repository.CartRepository;
import com.guanyanliang.persona5.repository.OrderItemRepository;
import com.guanyanliang.persona5.repository.OrderRepository;
import com.guanyanliang.persona5.repository.ProductRepository;
import com.guanyanliang.persona5.service.CartService;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.JwtUtil;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public CartController(CartService cartService,
                          UserService userService,
                          CartRepository cartRepository,
                          OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository,
                          ProductRepository productRepository) {

        this.cartService = cartService;
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    // ===== 获取购物车 =====
    @GetMapping
    public ResponseEntity<?> getCart(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        User user = getUserFromAuthHeader(authHeader);

        if (user == null) {
            return ResponseEntity.status(401).body("未登录或 token 无效");
        }

        try {
            List<CartDTO> cartList = cartService.getCartWithProductInfo(user);
            return ResponseEntity.ok(cartList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("获取购物车失败");
        }
    }

    // ===== 添加商品到购物车 =====
    @PostMapping
    public ResponseEntity<?> addToCart(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Long> body) {

        User user = getUserFromAuthHeader(authHeader);

        if (user == null) {
            return ResponseEntity.status(401).body("未登录或 token 无效");
        }

        try {

            Long productId = ((Number) body.get("productId")).longValue();
            Integer quantity = ((Number) body.get("quantity")).intValue();
            cartService.addToCart(user, productId, quantity);

            List<CartDTO> cartList = cartService.getCartWithProductInfo(user);

            return ResponseEntity.ok(cartList);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(500).body("加入购物车失败");
        }
    }

    // ===== 修改购物车数量 =====
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateQuantity(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> body) {

        User user = getUserFromAuthHeader(authHeader);

        if (user == null) {
            return ResponseEntity.status(401).body("未登录或 token 无效");
        }

        try {

            Integer quantity = body.get("quantity");

            cartService.updateQuantity(user, productId, quantity);

            List<CartDTO> cartList = cartService.getCartWithProductInfo(user);

            return ResponseEntity.ok(cartList);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(500).body("更新购物车失败");
        }
    }

    // ===== 删除购物车商品 =====
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeItem(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long productId) {

        User user = getUserFromAuthHeader(authHeader);

        if (user == null) {
            return ResponseEntity.status(401).body("未登录或 token 无效");
        }

        try {

            cartService.removeItem(user, productId);

            List<CartDTO> cartList = cartService.getCartWithProductInfo(user);

            return ResponseEntity.ok(cartList);

        } catch (RuntimeException e) {

            return ResponseEntity.status(409).body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(500).body("删除购物车商品失败");
        }
    }

    // ===== 购物车结算 =====
    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<?> checkout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) {
            return ResponseEntity.status(401).body("未登录");
        }
        List<Cart> cartList = cartRepository.findByUser(user);
        if (cartList.isEmpty()) {
            return ResponseEntity.badRequest().body("购物车为空");
        }
        double total = 0;
        for (Cart c : cartList) {
            Product product = productRepository.findById(c.getProductId()).orElse(null);
            if (product != null) {
                total += product.getPrice() * c.getQuantity();
            }
        }
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalPrice(total);
        order.setStatus("PAID");
        order.setCreateTime(LocalDateTime.now());
        orderRepository.save(order);
        for (Cart c : cartList) {
            Product product = productRepository.findById(c.getProductId()).orElse(null);
            if (product == null) continue;
            int quantity = c.getQuantity();
            if (product.getStock() < quantity) {
                return ResponseEntity.badRequest().body(product.getName() + " 库存不足");
            }
            product.setStock(product.getStock() - quantity);
            product.setSales(product.getSales() + quantity);
            productRepository.save(product);
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);
            orderItemRepository.save(item);
            cartRepository.deleteByUserAndProductId(user, product.getId());
        }

        return ResponseEntity.ok("支付成功");
    }
    @PostMapping("/direct-order")
    @Transactional
    public ResponseEntity<?> directOrder(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> body) {

        // 解析用户
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) {
            return ResponseEntity.status(401).body("未登录或 token 无效");
        }

        // 获取前端传来的 productId 和数量
        Long productId = ((Number) body.get("productId")).longValue();
        Integer quantity = ((Number) body.get("quantity")).intValue();

        if (productId == null || quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().body("商品ID或数量错误");
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body("商品不存在");
        }

        if (product.getStock() < quantity) {
            return ResponseEntity.badRequest().body(product.getName() + " 库存不足");
        }

        // 计算总价
        double total = product.getPrice() * quantity;

        // 创建订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalPrice(total);
        order.setStatus("PAID"); // 如果要模拟支付，可以先写 PAID
        order.setCreateTime(LocalDateTime.now());
        orderRepository.save(order);

        // 创建订单项
        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setPrice(product.getPrice());
        item.setQuantity(quantity);
        orderItemRepository.save(item);

        // 更新库存和销量
        product.setStock(product.getStock() - quantity);
        product.setSales(product.getSales() + quantity);
        productRepository.save(product);

        return ResponseEntity.ok("下单成功");
    }
    // ===== 工具方法：解析用户 =====
    private User getUserFromAuthHeader(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Authorization header 无效：" + authHeader);
            return null;
        }

        String token = authHeader.substring(7);

        // 检查 token 是否为无效字符串
        if (token == null || token.isEmpty() || "undefined".equals(token)) {
            System.out.println("Token 无效或为 undefined");
            return null;
        }

        try {

            String username = JwtUtil.getUsername(token);

            if (username == null) {
                System.out.println("Token 解析失败，username 为 null");
                return null;
            }

            return userService.findByUsername(username).orElse(null);

        } catch (Exception e) {

            System.err.println("Token 解析异常：" + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }
}