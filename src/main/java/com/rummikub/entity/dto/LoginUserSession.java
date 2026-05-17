package com.rummikub.entity.dto;

import com.rummikub.entity.po.UserPO;
import lombok.Data;

@Data
public class LoginUserSession {

    private Long userId;

    private String username;

    private String nickname;

    private Integer status;

    public static LoginUserSession getInstance(UserPO user) {
        LoginUserSession instance = new LoginUserSession();
        instance.setUserId(user.getId());
        instance.setUsername(user.getUsername());
        instance.setNickname(user.getNickname());
        instance.setStatus(user.getStatus());
        return instance;
    }
}
