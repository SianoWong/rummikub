package com.rummikub.entity.po;

import com.rummikub.entity.request.RegisterRequest;
import com.rummikub.exception.CustomException;
import com.rummikub.enums.ExceptionEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class UserPO {

    public static final int STATUS_NORMAL = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, length = 32)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nickname", nullable = false, length = 32)
    private String nickname;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public static UserPO getInstance(RegisterRequest request, String passwordHash) {
        UserPO instance = new UserPO();
        instance.setUsername(request.getUsername());
        instance.setPasswordHash(passwordHash);
        instance.setNickname(request.getNickname());
        instance.setStatus(STATUS_NORMAL);
        instance.setLastLoginAt(LocalDateTime.now());
        return instance;
    }

    public void refreshLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void checkStatus() {
        if (!Integer.valueOf(STATUS_NORMAL).equals(status)) {
            throw new CustomException(ExceptionEnum.USER_STATUS_UNAVAILABLE);
        }
    }
}
