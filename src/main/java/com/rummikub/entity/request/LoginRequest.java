package com.rummikub.entity.request;

import com.rummikub.enums.ExceptionEnum;
import com.rummikub.util.CheckUtil;
import lombok.Data;

@Data
public class LoginRequest {

    private String username;

    private String password;

    public void check() {
        username = CheckUtil.formatNonNull(username, ExceptionEnum.USERNAME_EMPTY);
        password = CheckUtil.formatNonNull(password, ExceptionEnum.PASSWORD_EMPTY);
    }
}
