package com.guanyanliang.persona5.controller;

import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")  // 前端请求路径: /api/auth/register
@CrossOrigin(origins = "*")    // 跨域
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
    public User login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }
}
