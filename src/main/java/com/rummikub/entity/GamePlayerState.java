package com.rummikub.entity;

import com.rummikub.enums.PlayerGameStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GamePlayerState {

    private Long userId;

    private Integer seatNo;

    private PlayerGameStatus status = PlayerGameStatus.PLAYING;

    private Boolean online = true;

    private List<Short> handTiles = new ArrayList<>();

    private Boolean hasInitialMelded = false;

    public static GamePlayerState getInstance(Long userId, Integer seatNo, PlayerGameStatus status,
                                              Boolean online, List<Short> handTiles,
                                              Boolean hasInitialMelded) {
        GamePlayerState instance = new GamePlayerState();
        instance.setUserId(userId);
        instance.setSeatNo(seatNo);
        instance.setStatus(status);
        instance.setOnline(online);
        instance.setHandTiles(handTiles);
        instance.setHasInitialMelded(hasInitialMelded);
        return instance;
    }
}
