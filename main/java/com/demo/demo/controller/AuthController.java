package com.demo.demo.controller;

import com.demo.demo.dto.request.LoginRequest;
import com.demo.demo.dto.request.RegisterRequest;
import com.demo.demo.dto.response.ApiResponse;
import com.demo.demo.dto.response.AuthResponse;
import com.demo.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器。
 * 负责对外暴露注册和登录接口，属于 MVC 中的 Controller 层。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册接口。
     *
     * @param request 注册请求参数
     * @return 注册结果，包含签发的 JWT
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("注册成功", authService.register(request));
    }

    /**
     * 用户登录接口。
     *
     * @param request 登录请求参数
     * @return 登录结果，包含签发的 JWT
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("登录成功", authService.login(request));
    }
}
