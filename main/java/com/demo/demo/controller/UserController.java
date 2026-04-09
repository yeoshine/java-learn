package com.demo.demo.controller;

import com.demo.demo.dto.response.ApiResponse;
import com.demo.demo.dto.response.UserProfileResponse;
import com.demo.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器。
 * 这里演示登录后才能访问的受保护资源接口。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前登录用户信息。
     *
     * @return 当前用户资料
     */
    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getCurrentUser() {
        return ApiResponse.success("获取当前用户成功", userService.getCurrentUserProfile());
    }
}
