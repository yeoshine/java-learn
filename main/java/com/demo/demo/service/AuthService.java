package com.demo.demo.service;

import com.demo.demo.dto.request.LoginRequest;
import com.demo.demo.dto.request.RegisterRequest;
import com.demo.demo.dto.response.AuthResponse;
import com.demo.demo.entity.User;
import com.demo.demo.exception.BusinessException;
import com.demo.demo.repository.UserRepository;
import com.demo.demo.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务类。
 * 负责注册、登录、密码加密以及 JWT 生成等核心认证业务。
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 注册用户并直接签发 JWT。
     *
     * @param request 注册请求
     * @return 注册成功后的响应
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();

        String token = jwtTokenProvider.generateToken(userDetails);
        return new AuthResponse(token, "Bearer", user.getUsername(), user.getEmail());
    }

    /**
     * 校验用户身份并签发 JWT。
     *
     * @param request 登录请求
     * @return 登录成功后的响应
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("用户不存在"));

        String token = jwtTokenProvider.generateToken(userDetails);
        return new AuthResponse(token, "Bearer", user.getUsername(), user.getEmail());
    }
}
