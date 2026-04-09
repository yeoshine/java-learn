package com.demo.demo.dto.response;

/**
 * 登录/注册成功后的返回 DTO。
 */
public class AuthResponse {

    /**
     * JWT Token，前端后续请求放入 Authorization 头中。
     */
    private String token;

    /**
     * Token 类型，通常是 Bearer。
     */
    private String tokenType;

    /**
     * 当前登录用户的用户名。
     */
    private String username;

    /**
     * 当前登录用户的邮箱。
     */
    private String email;

    public AuthResponse() {
    }

    public AuthResponse(String token, String tokenType, String username, String email) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
