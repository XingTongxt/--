package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.service.AdminService;
import com.guanyanliang.persona5.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 管理员登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        Map<String, Object> result = new HashMap<>();
        if ("admin".equals(username) && "123456".equals(password)) {
            String token = JwtUtil.generateToken(username);
            result.put("code", 200);
            result.put("msg", "登录成功");
            result.put("token", token);
        } else {
            result.put("code", 401);
            result.put("msg", "用户名或密码错误");
        }
        return result;
    }

    @GetMapping("/info")
    public Map<String, Object> getAdminInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = JwtUtil.parseToken(token); // 解析 token 得到用户名

        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        return result;
    }


}
