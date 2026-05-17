package com.rummikub.entity.dto;

import com.rummikub.entity.po.GameRoomPO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoomRuntimeDTO {

    private Long roomId;

    private String roomCode;

    private Long ownerUserId;

    private Integer status;

    private Integer maxPlayers;

    private Integer initialMeldScore;

    private Integer turnTimeSeconds;

    private Boolean useJoker;

    private List<RoomPlayerDTO> players;

    public static RoomRuntimeDTO getInstance(GameRoomPO room, LoginUserSession ownerSession) {
        RoomRuntimeDTO instance = new RoomRuntimeDTO();
        instance.setRoomId(room.getId());
        instance.setRoomCode(room.getRoomCode());
        instance.setOwnerUserId(room.getOwnerUserId());
        instance.setStatus(room.getStatus());
        instance.setMaxPlayers(room.getMaxPlayers());
        instance.setInitialMeldScore(room.getInitialMeldScore());
        instance.setTurnTimeSeconds(room.getTurnTimeSeconds());
        instance.setUseJoker(room.getUseJoker());
        List<RoomPlayerDTO> players = new ArrayList<>();
        players.add(RoomPlayerDTO.getInstance(ownerSession, 1));
        instance.setPlayers(players);
        return instance;
    }
}
