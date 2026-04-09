package com.demo.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 用户实体。
 * Entity 属于 MVC 中 Model 的数据核心部分。
 */
@Entity
@Table(name = "sys_user")
public class User {

    /**
     * 主键 ID。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名，要求唯一。
     */
    @Column(nullable = false, unique = true, length = 20)
    private String username;

    /**
     * 邮箱，要求唯一。
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 加密后的密码摘要。
     */
    @Column(nullable = false)
    private String password;

    /**
     * 记录创建时间。
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * 记录最后更新时间。
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 实体入库前自动补齐创建时间。
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    /**
     * 实体更新前自动刷新更新时间。
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
