package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.service.UserService;
import com.guanyanliang.persona5.util.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")  // 前端请求路径: /api/user/xxx
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
        System.out.println("login 请求 body: " + user.getUsername() + " / " + user.getPassword());
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            String token = JwtUtil.generateToken(loginUser.getUsername());
            System.out.println("生成 token: " + token);

            Map<String, Object> data = new HashMap<>();
            data.put("message", "登录成功");
            data.put("token", token);
            data.put("username", loginUser.getUsername());
            data.put("email", loginUser.getEmail());
            return ResponseEntity.ok(data);
        } else {
            System.out.println("用户名或密码错误");
            return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
        }
    }



    // 获取用户信息
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = JwtUtil.parseToken(token);

        // userService.findByUsername 返回 Optional<User>
        Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }

        User user = optionalUser.get(); // 取出 User 对象
        Map<String, Object> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("email", user.getEmail() != null ? user.getEmail() : "未绑定");

        return ResponseEntity.ok(result);
    }




    // 修改密码
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body("未登录");
        }
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("新密码不能为空");
        }
        boolean success = userService.changePassword(username, newPassword);
        return success ? ResponseEntity.ok("修改成功") : ResponseEntity.status(500).body("修改失败");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); // 销毁 session
        return ResponseEntity.ok("已退出登录");
    }

}
