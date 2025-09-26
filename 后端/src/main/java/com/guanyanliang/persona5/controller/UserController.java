package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 注册
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.register(user);
    }

    // 登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            String role = (loginUser.getRole() != null) ? loginUser.getRole() : "USER";
            String token = JwtUtil.generateToken(loginUser.getUsername(), role);

            Map<String, Object> data = new HashMap<>();
            data.put("message", "登录成功");
            data.put("token", token);
            data.put("username", loginUser.getUsername());
            data.put("email", loginUser.getEmail());
            data.put("role", role);

            return ResponseEntity.ok(data);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
        }
    }

    // 获取用户信息
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录或 token 无效");
        }

        String token = authHeader.replace("Bearer ", "").trim();
        String username = JwtUtil.getUsername(token);
        String role = JwtUtil.getRole(token);

        Optional<User> optionalUser = userService.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }

        User user = optionalUser.get();
        Map<String, Object> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("email", user.getEmail() != null ? user.getEmail() : "未绑定");
        result.put("role", role);

        return ResponseEntity.ok(result);
    }

    // 修改密码
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody Map<String, String> request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录或 token 无效");
        }

        String token = authHeader.replace("Bearer ", "").trim();
        String username = JwtUtil.getUsername(token);
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("新密码不能为空");
        }

        boolean success = userService.changePassword(username, newPassword);
        return success ? ResponseEntity.ok("修改成功") : ResponseEntity.status(500).body("修改失败");
    }

    // 注销登录
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("已退出登录");
    }
}
