package com.guanyanliang.persona5.service;

import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 注册方法
    public String register(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "用户名已存在";
        }

        // 设置默认角色为 USER
        user.setRole("USER");

        // 保存用户
        userRepository.save(user);
        return "注册成功";
    }

    // 登录方法
    public User login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(password)) {
            return optionalUser.get();
        }
        return null;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 修改密码
    public boolean changePassword(String username, String newPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 登录管理员
    public User adminLogin(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("ADMIN".equals(user.getRole()) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean changeRole(String username, String newRole) {
        return userRepository.findByUsername(username).map(user -> {
            user.setRole(newRole.toUpperCase());
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    public boolean deleteUser(String username) {
        return userRepository.findByUsername(username).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }


}
