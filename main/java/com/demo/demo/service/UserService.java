package com.demo.demo.service;

import com.demo.demo.dto.response.UserProfileResponse;
import com.demo.demo.entity.User;
import com.demo.demo.exception.BusinessException;
import com.demo.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 用户服务类。
 * 负责处理当前登录用户相关的业务。
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 读取当前安全上下文中的用户信息，并返回资料 DTO。
     *
     * @return 当前用户资料
     */
    public UserProfileResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("当前用户未登录");
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BusinessException("用户不存在"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
