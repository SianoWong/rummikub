package com.rummikub.enums;

public enum ExceptionEnum {

    REGISTER_PARAM_EMPTY(10001, "注册参数不能为空"),
    USERNAME_EMPTY(10002, "用户名不能为空"),
    PASSWORD_EMPTY(10003, "密码不能为空"),
    USERNAME_FORMAT_ERROR(10004, "用户名只能包含字母、数字和下划线，长度3到20位"),
    PASSWORD_LENGTH_ERROR(10005, "密码长度必须在6到64位之间"),
    NICKNAME_LENGTH_ERROR(10006, "昵称不能超过20个字符"),
    AVATAR_IMAGE_SAVE_FAILED(10007, "头像保存失败"),
    USERNAME_EXISTS(10008, "用户名已存在"),
    USERNAME_OR_PASSWORD_ERROR(10009, "用户名或密码错误"),
    USER_STATUS_UNAVAILABLE(10010, "用户状态不可用"),
    LOGIN_SESSION_CREATE_FAILED(10011, "登录态创建失败"),
    UNAUTHORIZED(10012, "未登录或登录已过期"),
    ROOM_MAX_PLAYERS_ERROR(20001, "房间人数必须在2到4人之间"),
    ROOM_INITIAL_MELD_SCORE_ERROR(20002, "初始破冰分数必须在0到100之间"),
    ROOM_TURN_TIME_ERROR(20003, "回合时间必须在15到300秒之间"),
    ROOM_CODE_EXISTS(20004, "房间号已存在，请稍后重试"),
    SYSTEM_ERROR(50000, "服务器内部错误");

    private final Integer code;

    private final String message;

    ExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
