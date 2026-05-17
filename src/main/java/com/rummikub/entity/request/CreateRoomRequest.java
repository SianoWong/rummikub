package com.rummikub.entity.request;

import com.rummikub.enums.ExceptionEnum;
import com.rummikub.exception.CustomException;
import lombok.Data;

@Data
public class CreateRoomRequest {

    private Integer maxPlayers;

    private Integer initialMeldScore;

    private Integer turnTimeSeconds;

    private Boolean useJoker;

    public void check() {
        if (maxPlayers == null) {
            maxPlayers = 4;
        }
        if (initialMeldScore == null) {
            initialMeldScore = 30;
        }
        if (turnTimeSeconds == null) {
            turnTimeSeconds = 60;
        }
        if (useJoker == null) {
            useJoker = true;
        }

        if (maxPlayers < 2 || maxPlayers > 4) {
            throw new CustomException(ExceptionEnum.ROOM_MAX_PLAYERS_ERROR);
        }
        if (initialMeldScore < 0 || initialMeldScore > 100) {
            throw new CustomException(ExceptionEnum.ROOM_INITIAL_MELD_SCORE_ERROR);
        }
        if (turnTimeSeconds < 15 || turnTimeSeconds > 300) {
            throw new CustomException(ExceptionEnum.ROOM_TURN_TIME_ERROR);
        }
    }
}
