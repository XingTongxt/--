package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.Admin;
import com.guanyanliang.persona5.service.AdminService;
import com.guanyanliang.persona5.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 管理员登录接口
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        Map<String, Object> result = new HashMap<>();
        Optional<Admin> adminOpt = adminService.findByUsername(username);

        if (adminOpt.isPresent() && adminOpt.get().getPassword().equals(password)) {
            String token = JwtUtil.generateToken(username, "admin");
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("token", token);
            return ResponseEntity.ok(result); // 200
        } else {
            result.put("code", 401);
            result.put("msg", "用户名或密码错误");
            return ResponseEntity.status(401).body(result); // 401
        }
    }



    // 获取管理员信息
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAdminInfo(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                result.put("msg", "未提供 token");
                return ResponseEntity.status(401).body(result);
            }

            String token = authHeader.replace("Bearer ", "");
            String username = JwtUtil.getUsername(token);  // JwtUtil解析 token
            String role = JwtUtil.getRole(token);

            result.put("username", username);
            result.put("role", role);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("msg", "Token 无效或过期");
            return ResponseEntity.status(401).body(result);
        }
    }


}
