package com.guanyanliang.persona5.service;

import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 注册用户
    public String register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            return "用户名已存在";
        }
        // 保存用户
        userRepository.save(user);
        return "success";
    }

    // 登录用户
    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 根据用户名获取用户
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 修改密码
    public boolean changePassword(String username, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword); // ⚠️生产环境要加密
            userRepository.save(user);
            return true;
        }
        return false;
    }

}
