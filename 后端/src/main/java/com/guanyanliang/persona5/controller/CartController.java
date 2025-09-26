package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.dto.CartDTO;
import com.guanyanliang.persona5.entity.Cart;
import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.service.CartService;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    // ===== 获取购物车列表 =====
    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) return ResponseEntity.status(401).body("未登录或 token 无效");

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
    public ResponseEntity<?> addToCart(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                       @RequestBody Map<String, Long> body) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) return ResponseEntity.status(401).body("未登录或 token 无效");

        try {
            Long productId = body.get("productId");
            cartService.addToCart(user, productId);

            // 返回最新购物车列表
            List<CartDTO> cartList = cartService.getCartWithProductInfo(user);
            return ResponseEntity.ok(cartList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("加入购物车失败");
        }
    }

    // ===== 修改购物车商品数量 =====
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateQuantity(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                            @PathVariable Long productId,
                                            @RequestBody Map<String, Integer> body) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) return ResponseEntity.status(401).body("未登录或 token 无效");

        try {
            Integer quantity = body.get("quantity");
            cartService.updateQuantity(user, productId, quantity);

            // 返回最新购物车列表
            List<CartDTO> cartList = cartService.getCartWithProductInfo(user);
            return ResponseEntity.ok(cartList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("更新购物车失败");
        }
    }

    // ===== 删除购物车商品 =====
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeItem(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                        @PathVariable Long productId) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) return ResponseEntity.status(401).body("未登录或 token 无效");

        try {
            cartService.removeItem(user, productId);

            // 返回最新购物车列表
            List<CartDTO> cartList = cartService.getCartWithProductInfo(user);
            return ResponseEntity.ok(cartList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("删除购物车商品失败");
        }
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
}
