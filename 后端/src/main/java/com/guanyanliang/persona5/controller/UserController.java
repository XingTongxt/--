package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.Log;
import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.LogRepository;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.ApiResponse;
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
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody User user) {
        String result = userService.register(user);
        boolean success = "注册成功".equals(result);

        // 保存日志
        String username = user.getUsername() == null ? "未知用户" : user.getUsername();
        logRepository.save(new Log(username, "USER", "注册账号 " + (success ? "成功" : "失败"), "OPERATION", LocalDateTime.now()));

        return ResponseEntity
                .status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(success ? 200 : 400, result, null));
    }

    // ==================== 用户登录 ====================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody User user) {
        Optional<User> loginUserOpt = userService.login(user.getUsername(), user.getPassword(), null);
        if (loginUserOpt.isEmpty()) {
            logRepository.save(new Log(user.getUsername(), "USER", "登录失败", "LOGIN", LocalDateTime.now()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "用户名或密码错误", null));
        }

        User loginUser = loginUserOpt.get();
        String role = loginUser.getRole() != null ? loginUser.getRole() : "USER";
        String token = JwtUtil.generateToken(loginUser.getId(), loginUser.getUsername(), role);

        logRepository.save(new Log(loginUser.getUsername(), role, "登录成功", "LOGIN", LocalDateTime.now()));

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", loginUser.getUsername());
        data.put("email", loginUser.getEmail());
        data.put("role", role);

        return ResponseEntity.ok(new ApiResponse<>(200, "登录成功", data));
    }

    // ==================== 获取用户信息 ====================
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String username = extractUsername(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "未登录或 token 无效", null));
        }

        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "用户不存在", null));
        }

        User user = optionalUser.get();
        Map<String, Object> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("email", user.getEmail() != null ? user.getEmail() : "未绑定");
        result.put("role", JwtUtil.getRole(authHeader.replace("Bearer ", "").trim()));

        return ResponseEntity.ok(new ApiResponse<>(200, "获取成功", result));
    }

    // ==================== 修改密码 ====================
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestHeader("Authorization") String authHeader,
                                                            @RequestBody Map<String, String> request) {
        String username = extractUsername(authHeader);
        String role = extractRole(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "未登录或 token 无效", null));
        }

        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "新密码不能为空", null));
        }

        boolean success = userService.changePassword(username, newPassword);
        if (success) {
            logRepository.save(new Log(username, role, "修改密码", "OPERATION", LocalDateTime.now()));
            return ResponseEntity.ok(new ApiResponse<>(200, "修改成功", null));
        } else {
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "修改失败", null));
        }
    }

    // ==================== 用户登出 ====================
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = extractUsername(authHeader);
        String role = extractRole(authHeader);

        if (username != null) {
            logRepository.save(new Log(username, role, "用户登出", "OPERATION", LocalDateTime.now()));
        }
        return ResponseEntity.ok(new ApiResponse<>(200, "已退出登录", null));
    }

    // ==================== 工具方法 ====================
    private String extractUsername(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return JwtUtil.getUsername(authHeader.replace("Bearer ", "").trim());
        }
        return null;
    }

    private String extractRole(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return JwtUtil.getRole(authHeader.replace("Bearer ", "").trim());
        }
        return "USER";
    }
}