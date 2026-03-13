package com.guanyanliang.persona5.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "your_super_secret_key_which_should_be_long_enough";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;
    // 生成token
    public static String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }
    // 解析token
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // 获取用户名
    public static String getUsername(String token) {
        return parseToken(token).getSubject();
    }
    // 获取角色
    public static String getRole(String token) {
        Object role = parseToken(token).get("role");
        return role != null ? role.toString() : null;
    }
    public static Long getUserId(String token) {
        Object userId = parseToken(token).get("userId");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }
}