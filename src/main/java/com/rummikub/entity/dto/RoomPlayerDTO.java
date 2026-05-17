package com.rummikub.entity.dto;

import lombok.Data;

@Data
public class RoomPlayerDTO {

    private Long userId;

    private String username;

    private String nickname;

    private Integer seatNo;

    private Boolean ready;

    public static RoomPlayerDTO getInstance(LoginUserSession session, Integer seatNo) {
        RoomPlayerDTO instance = new RoomPlayerDTO();
        instance.setUserId(session.getUserId());
        instance.setUsername(session.getUsername());
        instance.setNickname(session.getNickname());
        instance.setSeatNo(seatNo);
        instance.setReady(true);
        return instance;
    }
}
