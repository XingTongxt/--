package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.Log;
import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.LogRepository;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private LogRepository logRepository;

    // ==================== 用户注册 ====================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String result = userService.register(user);
        String status = result.equals("注册成功") ? "成功" : "失败";

        // 保存注册日志
        logRepository.save(new Log(user.getUsername(), "USER", "注册账号 " + status, "OPERATION", LocalDateTime.now()));

        return ResponseEntity.ok(Map.of(
                "message", result
        ));
    }

    // ==================== 用户登录 ====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            String role = loginUser.getRole() != null ? loginUser.getRole() : "USER";
            String token = JwtUtil.generateToken(loginUser.getUsername(), role);

            // 登录成功日志
            logRepository.save(new Log(loginUser.getUsername(), role, "登录成功", "LOGIN", LocalDateTime.now()));

            Map<String, Object> data = new HashMap<>();
            data.put("message", "登录成功");
            data.put("token", token);
            data.put("username", loginUser.getUsername());
            data.put("email", loginUser.getEmail());
            data.put("role", role);
            return ResponseEntity.ok(data);
        } else {
            // 登录失败日志
            logRepository.save(new Log(user.getUsername(), "USER", "登录失败", "LOGIN", LocalDateTime.now()));
            return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
        }
    }

    // ==================== 获取用户信息 ====================
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        if (!isLoggedIn(authHeader)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录或 token 无效");

        String token = authHeader.replace("Bearer ", "").trim();
        String username = JwtUtil.getUsername(token);
        String role = JwtUtil.getRole(token);

        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");

        User user = optionalUser.get();
        Map<String, Object> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("email", user.getEmail() != null ? user.getEmail() : "未绑定");
        result.put("role", role);

        return ResponseEntity.ok(result);
    }

    // ==================== 修改密码 ====================
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody Map<String, String> request) {
        if (!isLoggedIn(authHeader)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录或 token 无效");

        String token = authHeader.replace("Bearer ", "").trim();
        String username = JwtUtil.getUsername(token);
        String role = JwtUtil.getRole(token);
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("新密码不能为空");
        }

        boolean success = userService.changePassword(username, newPassword);
        if (success) {
            // 保存修改密码日志
            logRepository.save(new Log(username, role, "修改密码", "OPERATION", LocalDateTime.now()));
        }

        return success ? ResponseEntity.ok(Map.of("message", "修改成功")) :
                ResponseEntity.status(500).body(Map.of("message", "修改失败"));
    }

    // ==================== 用户登出 ====================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "").trim();
            String username = JwtUtil.getUsername(token);
            String role = JwtUtil.getRole(token);
            // 保存登出日志
            logRepository.save(new Log(username, role, "用户登出", "OPERATION", LocalDateTime.now()));
        }
        return ResponseEntity.ok(Map.of("message", "已退出登录"));
    }

    // ==================== 工具方法 ====================
    private boolean isLoggedIn(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ") && JwtUtil.getUsername(authHeader.replace("Bearer ", "").trim()) != null;
    }
}
