package com.rummikub.enums;

public enum RoomStatus {

    WAITING(1),
    PLAYING(2),
    CLOSED(3);

    private final Integer code;

    RoomStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
