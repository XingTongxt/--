package com.guanyanliang.persona5.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")  // 数据库表名
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键

    @Column(nullable = false, unique = true)
    private String username; // 用户名

    @Column(nullable = false)
    private String password; // 密码

    @Column(unique = true)
    private String email; // 邮箱，可选

    @Column(nullable = false)
    private String role = "USER"; // 角色，默认普通用户

    // 构造方法
    public User() {}

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
