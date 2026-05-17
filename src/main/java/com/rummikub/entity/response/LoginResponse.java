package com.rummikub.entity.response;

import com.rummikub.entity.dto.LoginUserSession;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private Long expiresInSeconds;

    private LoginUserSession user;

    public static LoginResponse getInstance(String token, Long expiresInSeconds, LoginUserSession user) {
        LoginResponse instance = new LoginResponse();
        instance.setToken(token);
        instance.setExpiresInSeconds(expiresInSeconds);
        instance.setUser(user);
        return instance;
    }
}
