package com.guanyanliang.persona5.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;      // 用户名
    private String role;          // ADMIN / USER
    private String action;        // 操作描述
    private String type;          // LOGIN / OPERATION
    private LocalDateTime time;   // 时间

    public Log() {}

    public Log(String username, String role, String action, String type, LocalDateTime time) {
        this.username = username;
        this.role = role;
        this.action = action;
        this.type = type;
        this.time = time;
    }

    // getter & setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
}
