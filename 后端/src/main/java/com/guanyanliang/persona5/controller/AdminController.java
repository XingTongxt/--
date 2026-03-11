package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.Log;
import com.guanyanliang.persona5.entity.Product;
import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.LogRepository;
import com.guanyanliang.persona5.repository.ProductRepository;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LogRepository logRepository;

    // ==================== 管理员登录 ====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> params) {

        String username = params.get("username");
        String password = params.get("password");

        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isPresent()) {

            User user = userOpt.get();

            if (("ADMIN".equals(user.getRole()) || "SUPERADMIN".equals(user.getRole()))
                    && user.getPassword().equals(password)) {

                String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

                logRepository.save(new Log(username, user.getRole(),
                        "管理员登录成功", "LOGIN", LocalDateTime.now()));

                return ResponseEntity.ok(Map.of(
                        "code", 200,
                        "msg", "登录成功",
                        "token", token
                ));
            }
        }

        logRepository.save(new Log(username, "ADMIN",
                "管理员登录失败", "LOGIN", LocalDateTime.now()));

        return ResponseEntity.status(401).body(Map.of(
                "code", 401,
                "msg", "用户名或密码错误"
        ));
    }

    // ==================== 获取管理员信息 ====================
    @GetMapping("/info")
    public ResponseEntity<?> getAdminInfo(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Authorization header错误");
            return ResponseEntity.status(403).body(Map.of("msg", "权限不足"));
        }

        String token = authHeader.replace("Bearer ", "").trim();
        String role = JwtUtil.getRole(token);

        if (!isAdminOrSuperAdmin(authHeader)) {
            System.out.println("权限判断失败");
            return ResponseEntity.status(403).body(Map.of("msg", "权限不足"));
        }

        String username = JwtUtil.getUsername(token);

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty())
            return ResponseEntity.status(404).body("管理员不存在");

        return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role
        ));
    }

    // ==================== 用户管理（只有SUPERADMIN） ====================
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {

        if (!isSuperAdmin(authHeader))
            return ResponseEntity.status(403).body("只有SUPERADMIN可以管理用户");

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{username}/role")
    public ResponseEntity<?> changeUserRole(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String username,
            @RequestBody Map<String, String> body) {

        if (!isSuperAdmin(authHeader))
            return ResponseEntity.status(403).body("只有SUPERADMIN可以修改用户");

        String newRole = body.get("role");

        boolean success = userService.changeRole(username, newRole);

        if (success) {

            String adminUsername = JwtUtil.getUsername(authHeader.replace("Bearer ", "").trim());

            logRepository.save(new Log(adminUsername, "SUPERADMIN",
                    "修改用户 " + username + " 角色为 " + newRole,
                    "OPERATION", LocalDateTime.now()));

            return ResponseEntity.ok("修改成功");
        }

        return ResponseEntity.status(404).body("用户不存在");
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String username) {

        if (!isSuperAdmin(authHeader))
            return ResponseEntity.status(403).body("只有SUPERADMIN可以删除用户");

        boolean success = userService.deleteUser(username);

        if (success) {

            String adminUsername = JwtUtil.getUsername(authHeader.replace("Bearer ", "").trim());

            logRepository.save(new Log(adminUsername, "SUPERADMIN",
                    "删除用户 " + username,
                    "OPERATION", LocalDateTime.now()));

            return ResponseEntity.ok("删除成功");
        }

        return ResponseEntity.status(404).body("用户不存在");
    }

    // ==================== 商品管理（ADMIN和SUPERADMIN） ====================
    @GetMapping("/items")
    public ResponseEntity<?> getAllItems(@RequestHeader("Authorization") String authHeader) {

        if (!isAdminOrSuperAdmin(authHeader))
            return ResponseEntity.status(403).body("权限不足");

        return ResponseEntity.ok(productRepository.findAll());
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody Product product) {

        if (!isAdminOrSuperAdmin(authHeader))
            return ResponseEntity.status(403).body("权限不足");

        productRepository.save(product);

        String adminUsername = JwtUtil.getUsername(authHeader.replace("Bearer ", "").trim());

        logRepository.save(new Log(adminUsername, "ADMIN",
                "新增物品：" + product.getName(),
                "OPERATION", LocalDateTime.now()));

        return ResponseEntity.ok("新增成功");
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id,
                                        @RequestBody Product updatedProduct) {

        if (!isAdminOrSuperAdmin(authHeader))
            return ResponseEntity.status(403).body("权限不足");

        return productRepository.findById(id)
                .map(product -> {

                    product.setName(updatedProduct.getName());
                    product.setPrice(updatedProduct.getPrice());
                    product.setCategory(updatedProduct.getCategory());
                    product.setImg(updatedProduct.getImg());

                    productRepository.save(product);

                    String adminUsername = JwtUtil.getUsername(authHeader.replace("Bearer ", "").trim());

                    logRepository.save(new Log(adminUsername, "ADMIN",
                            "修改物品：" + product.getName(),
                            "OPERATION", LocalDateTime.now()));

                    return ResponseEntity.ok("修改成功");
                })
                .orElse(ResponseEntity.status(404).body("物品不存在"));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable Long id) {

        if (!isAdminOrSuperAdmin(authHeader))
            return ResponseEntity.status(403).body("权限不足");

        return productRepository.findById(id)
                .map(product -> {

                    productRepository.delete(product);

                    String adminUsername = JwtUtil.getUsername(authHeader.replace("Bearer ", "").trim());

                    logRepository.save(new Log(adminUsername, "ADMIN",
                            "删除物品：" + product.getName(),
                            "OPERATION", LocalDateTime.now()));

                    return ResponseEntity.ok("删除成功");
                })
                .orElse(ResponseEntity.status(404).body("物品不存在"));
    }

    // ==================== 日志管理（ADMIN和SUPERADMIN） ====================
    @GetMapping("/logs")
    public ResponseEntity<?> getAllLogs(@RequestHeader("Authorization") String authHeader) {

        if (!isAdminOrSuperAdmin(authHeader))
            return ResponseEntity.status(403).body("权限不足");

        return ResponseEntity.ok(logRepository.findAll());
    }

    // ==================== 权限判断 ====================
    private boolean isAdminOrSuperAdmin(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return false;

        String token = authHeader.replace("Bearer ", "").trim();
        String role = JwtUtil.getRole(token);

        return "ADMIN".equals(role) || "SUPERADMIN".equals(role);
    }

    private boolean isSuperAdmin(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return false;

        String token = authHeader.replace("Bearer ", "").trim();
        String role = JwtUtil.getRole(token);

        return "SUPERADMIN".equals(role);
    }
}