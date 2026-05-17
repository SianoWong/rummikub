package com.rummikub.entity.response;

import com.rummikub.entity.dto.RoomPlayerDTO;
import com.rummikub.entity.dto.RoomRuntimeDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateRoomResponse {

    private Long roomId;

    private String roomCode;

    private Long ownerUserId;

    private Integer status;

    private Integer maxPlayers;

    private Integer initialMeldScore;

    private Integer turnTimeSeconds;

    private Boolean useJoker;

    private List<RoomPlayerDTO> players;

    public static CreateRoomResponse getInstance(RoomRuntimeDTO room) {
        CreateRoomResponse instance = new CreateRoomResponse();
        instance.setRoomId(room.getRoomId());
        instance.setRoomCode(room.getRoomCode());
        instance.setOwnerUserId(room.getOwnerUserId());
        instance.setStatus(room.getStatus());
        instance.setMaxPlayers(room.getMaxPlayers());
        instance.setInitialMeldScore(room.getInitialMeldScore());
        instance.setTurnTimeSeconds(room.getTurnTimeSeconds());
        instance.setUseJoker(room.getUseJoker());
        instance.setPlayers(room.getPlayers());
        return instance;
    }
}
