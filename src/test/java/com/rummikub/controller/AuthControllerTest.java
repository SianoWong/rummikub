package com.rummikub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rummikub.entity.dto.LoginUserSession;
import com.rummikub.entity.request.LoginRequest;
import com.rummikub.entity.request.RegisterRequest;
import com.rummikub.entity.response.LoginResponse;
import com.rummikub.service.AuthService;
import com.rummikub.service.AvatarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private FakeAuthService authService;

    private FakeAvatarService avatarService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        authService = new FakeAuthService();
        avatarService = new FakeAvatarService();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService, avatarService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerReturnsLoginResponse() throws Exception {
        authService.registerResponse = LoginResponse.getInstance("register-token", 604800L, buildSession());

        mockMvc.perform(multipart("/api/auth/register")
                        .param("username", "alice")
                        .param("password", "123456")
                        .param("nickname", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.token").value("register-token"))
                .andExpect(jsonPath("$.data.expiresInSeconds").value(604800))
                .andExpect(jsonPath("$.data.user.userId").value(1))
                .andExpect(jsonPath("$.data.user.username").value("alice"));
    }

    @Test
    void loginReturnsLoginResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("123456");
        authService.loginResponse = LoginResponse.getInstance("login-token", 604800L, buildSession());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.token").value("login-token"))
                .andExpect(jsonPath("$.data.user.userId").value(1))
                .andExpect(jsonPath("$.data.user.nickname").value("Alice"));
    }

    @Test
    void meReturnsCurrentUserSession() throws Exception {
        authService.session = buildSession();

        mockMvc.perform(get("/api/auth/me")
                        .header("X-Auth-Token", "login-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.nickname").value("Alice"));
    }

    @Test
    void avatarReturnsUserAvatarFile() throws Exception {
        avatarService.avatar = new ByteArrayResource("avatar".getBytes()) {
            @Override
            public String getFilename() {
                return "avatar.png";
            }
        };

        mockMvc.perform(get("/api/auth/avatar/1")
                        .header("X-Auth-Token", "login-token"))
                .andExpect(status().isOk());
    }

    @Test
    void avatarReturnsEmptyBodyWhenMissing() throws Exception {
        avatarService.avatar = null;

        mockMvc.perform(get("/api/auth/avatar/1")
                        .header("X-Auth-Token", "login-token"))
                .andExpect(status().isOk());
    }

    private LoginUserSession buildSession() {
        LoginUserSession session = new LoginUserSession();
        session.setUserId(1L);
        session.setUsername("alice");
        session.setNickname("Alice");
        session.setStatus(1);
        return session;
    }

    private static class FakeAuthService extends AuthService {
        private LoginResponse registerResponse;
        private LoginResponse loginResponse;
        private LoginUserSession session;

        private FakeAuthService() {
            super(null, null, null, null, null);
        }

        @Override
        public LoginResponse register(RegisterRequest request) {
            return registerResponse;
        }

        @Override
        public LoginResponse login(LoginRequest request) {
            return loginResponse;
        }

        @Override
        public LoginUserSession getSession(String token) {
            return session;
        }
    }

    private static class FakeAvatarService extends AvatarService {
        private Resource avatar;

        @Override
        public Resource getAvatar(Long userId) {
            return avatar;
        }
    }
}
