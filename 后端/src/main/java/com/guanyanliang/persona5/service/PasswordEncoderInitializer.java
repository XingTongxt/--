package com.guanyanliang.persona5.service;

import com.guanyanliang.persona5.entity.User;
import com.guanyanliang.persona5.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordEncoderInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            // 如果密码不是 BCrypt 格式（不以 $2a$ 开头），则加密
            if (!user.getPassword().startsWith("$2a$")) {
                user.setPassword(encoder.encode(user.getPassword()));
                userRepository.save(user);
            }
        }
    }
}
