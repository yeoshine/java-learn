package com.demo.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO。
 * DTO 用于接收控制器输入参数，避免直接暴露实体对象。
 */
public class LoginRequest {

    /**
     * 登录账号，这里使用用户名。
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 登录密码。
     */
    @NotBlank(message = "密码不能为空")
    private String password;

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
}
