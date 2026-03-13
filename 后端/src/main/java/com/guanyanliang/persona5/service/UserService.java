package com.guanyanliang.persona5.service;

import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.UserRepository;
import com.guanyanliang.persona5.util.RegisterValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** 注册用户 */
    @Transactional(rollbackFor = Exception.class)
    public String register(User user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return "用户名/密码/邮箱不能为空";
        }

        if (!RegisterValidateUtil.validateEmail(user.getEmail())) {
            return "邮箱格式不正确（示例：xxx@xxx.com）";
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "该邮箱已注册";
        }

        if (!RegisterValidateUtil.validatePassword(user.getPassword())) {
            return "密码不符合规则：需8-20位，包含大小写字母和数字";
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "用户名已存在";
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER"); // 默认普通用户
        userRepository.save(user);
        return "注册成功";
    }

    /** 登录用户（不限制角色） */
    public Optional<User> login(String username, String password) {
        return login(username, password, null);
    }

    /**
     * 登录用户（普通或管理员）
     * @param username 用户名
     * @param password 密码
     * @param role 角色（可为 null，表示不限制角色）
     * @return 登录成功的用户信息
     */
    public Optional<User> login(String username, String password, String role) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword()) &&
                    (role == null || role.equalsIgnoreCase(user.getRole()))) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /** 查找用户 */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /** 修改密码 */
    @Transactional
    public boolean changePassword(String username, String newPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            if (!RegisterValidateUtil.validatePassword(newPassword)) {
                return false; // 新密码不合法
            }
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /** 获取所有用户 */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** 修改角色（仅允许 USER 或 ADMIN） */
    @Transactional
    public boolean changeRole(String username, String newRole) {
        if (!"USER".equalsIgnoreCase(newRole) && !"ADMIN".equalsIgnoreCase(newRole)) {
            return false;
        }
        return userRepository.findByUsername(username).map(user -> {
            user.setRole(newRole.toUpperCase());
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    /** 删除用户 */
    @Transactional
    public boolean deleteUser(String username) {
        return userRepository.findByUsername(username).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}