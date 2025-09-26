package com.guanyanliang.persona5.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    // 自动生成一个足够安全的 key
    private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final long EXPIRATION = 24 * 60 * 60 * 1000; // 1 天

    // 生成 token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    // 解析 token
    public static String parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 如果需要，可提供 key 的 Base64 字符串，用于持久化
    public static String getKeyBase64() {
        return java.util.Base64.getEncoder().encodeToString(KEY.getEncoded());
    }


}
