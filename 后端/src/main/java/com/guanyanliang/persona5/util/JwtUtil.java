package com.guanyanliang.persona5.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    // 固定密钥（与生成 token 时一致）
    private static final String SECRET = "your_super_secret_key_which_should_be_long_enough";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION = 24 * 60 * 60 * 1000; // 1天

    // 生成 token（用户名 + 角色）
    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    // 解析 token 获取 Claims
    private static Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 获取用户名
    public static String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // 获取角色
    public static String getRole(String token) {
        Object role = parseClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

}
