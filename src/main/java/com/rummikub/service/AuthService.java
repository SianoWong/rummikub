package com.rummikub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rummikub.entity.dto.LoginUserSession;
import com.rummikub.entity.po.UserPO;
import com.rummikub.entity.request.LoginRequest;
import com.rummikub.entity.request.RegisterRequest;
import com.rummikub.entity.response.LoginResponse;
import com.rummikub.exception.CustomException;
import com.rummikub.enums.ExceptionEnum;
import com.rummikub.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    public static final String TOKEN_PREFIX = "login:token:";
    public static final long TOKEN_TTL_SECONDS = 7 * 24 * 60 * 60L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final AvatarService avatarService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper,
                       AvatarService avatarService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.avatarService = avatarService;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        request.check();
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ExceptionEnum.USERNAME_EXISTS);
        }
        UserPO user = UserPO.getInstance(request, passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        avatarService.saveAvatar(user.getId(), request.getAvatarImage());
        return issueToken(user);
    }

    public LoginResponse login(LoginRequest request) {
        request.check();
        UserPO user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CustomException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        user.checkStatus();
        user.refreshLastLoginAt();
        userRepository.save(user);
        return issueToken(user);
    }

    public LoginUserSession getSession(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return null;
        }
        String sessionJson = stringRedisTemplate.opsForValue().get(tokenKey(token));
        if (sessionJson == null) {
            return null;
        }
        try {
            return objectMapper.readValue(sessionJson, LoginUserSession.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void logout(String token) {
        if (ObjectUtils.isEmpty(token)) {
            return;
        }
        stringRedisTemplate.delete(tokenKey(token));
    }

    private LoginResponse issueToken(UserPO user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        LoginUserSession session = LoginUserSession.getInstance(user);
        try {
            stringRedisTemplate.opsForValue().set(
                    tokenKey(token),
                    objectMapper.writeValueAsString(session),
                    TOKEN_TTL_SECONDS,
                    TimeUnit.SECONDS
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ExceptionEnum.LOGIN_SESSION_CREATE_FAILED);
        }
        return LoginResponse.getInstance(token, TOKEN_TTL_SECONDS, session);
    }

    private String tokenKey(String token) {
        return TOKEN_PREFIX + token;
    }
}
