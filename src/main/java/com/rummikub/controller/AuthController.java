package com.rummikub.controller;

import com.rummikub.entity.dto.LoginUserSession;
import com.rummikub.entity.request.LoginRequest;
import com.rummikub.entity.request.RegisterRequest;
import com.rummikub.entity.response.ApiResponse;
import com.rummikub.entity.response.LoginResponse;
import com.rummikub.exception.CustomException;
import com.rummikub.enums.ExceptionEnum;
import com.rummikub.service.AvatarService;
import com.rummikub.service.AuthService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AvatarService avatarService;

    public AuthController(AuthService authService, AvatarService avatarService) {
        this.authService = authService;
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<LoginResponse> register(@ModelAttribute RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Boolean> logout(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        authService.logout(token);
        return ApiResponse.success(true);
    }

    @GetMapping("/me")
    public ApiResponse<LoginUserSession> me(@RequestHeader(value = "X-Auth-Token", required = false) String token) {
        LoginUserSession session = authService.getSession(token);
        if (session == null) {
            throw new CustomException(ExceptionEnum.UNAUTHORIZED);
        }
        return ApiResponse.success(session);
    }

    @GetMapping("/avatar/{userId}")
    public ResponseEntity<Resource> avatar(@PathVariable Long userId) {
        Resource avatar = avatarService.getAvatar(userId);
        if (avatar == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(avatar);
    }
}
