package com.rummikub.entity.request;

import com.rummikub.exception.CustomException;
import com.rummikub.enums.ExceptionEnum;
import com.rummikub.util.CheckUtil;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterRequest {

    private String username;

    private String password;

    private String nickname;

    private MultipartFile avatarImage;

    public void check() {
        username = CheckUtil.formatNonNull(username, ExceptionEnum.USERNAME_EMPTY);
        password = CheckUtil.formatNonNull(password, ExceptionEnum.PASSWORD_EMPTY);
        nickname = StringUtils.hasText(nickname) ? nickname.trim() : username;

        if (!username.matches("^[A-Za-z0-9_]{3,20}$")) {
            throw new CustomException(ExceptionEnum.USERNAME_FORMAT_ERROR);
        }
        if (password.length() < 6 || password.length() > 64) {
            throw new CustomException(ExceptionEnum.PASSWORD_LENGTH_ERROR);
        }
        if (nickname.length() > 20) {
            throw new CustomException(ExceptionEnum.NICKNAME_LENGTH_ERROR);
        }
    }
}
