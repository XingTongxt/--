package com.guanyanliang.persona5.service;

import com.guanyanliang.persona5.entity.Admin;
import com.guanyanliang.persona5.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // 根据用户名查找管理员
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    // 登录验证方法
    public Admin login(String username, String password) {
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        if (optionalAdmin.isPresent() && optionalAdmin.get().getPassword().equals(password)) {
            return optionalAdmin.get();
        }
        return null; // 用户名不存在或密码错误
    }
}
