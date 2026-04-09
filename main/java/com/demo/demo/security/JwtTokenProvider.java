package com.demo.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类。
 * 负责 Token 的生成、解析和校验。
 */
@Component
public class JwtTokenProvider {

    /**
     * 配置文件中的 JWT 密钥。
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token 有效期，单位毫秒。
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 运行时使用的签名密钥。
     */
    private SecretKey secretKey;

    /**
     * 初始化签名密钥。
     * HMAC 算法要求密钥长度足够，因此在启动时统一转换。
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 为当前用户生成 JWT。
     *
     * @param userDetails 当前登录用户
     * @return JWT 字符串
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从 Token 中提取用户名。
     *
     * @param token JWT 字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 校验 Token 是否合法且未过期。
     *
     * @param token JWT 字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 统一解析 JWT 中的 Claims。
     *
     * @param token JWT 字符串
     * @return Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
