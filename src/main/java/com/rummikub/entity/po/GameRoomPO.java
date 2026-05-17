package com.rummikub.entity.po;

import com.rummikub.entity.request.CreateRoomRequest;
import com.rummikub.enums.RoomStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "game_rooms")
public class GameRoomPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "room_code", nullable = false, length = 16)
    private String roomCode;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Column(name = "initial_meld_score", nullable = false)
    private Integer initialMeldScore;

    @Column(name = "turn_time_seconds", nullable = false)
    private Integer turnTimeSeconds;

    @Column(name = "use_joker", nullable = false)
    private Boolean useJoker;

    @Column(name = "current_game_id")
    private Long currentGameId;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public static GameRoomPO getInstance(String roomCode, Long ownerUserId, CreateRoomRequest request) {
        GameRoomPO instance = new GameRoomPO();
        instance.setRoomCode(roomCode);
        instance.setOwnerUserId(ownerUserId);
        instance.setStatus(RoomStatus.WAITING.getCode());
        instance.setMaxPlayers(request.getMaxPlayers());
        instance.setInitialMeldScore(request.getInitialMeldScore());
        instance.setTurnTimeSeconds(request.getTurnTimeSeconds());
        instance.setUseJoker(request.getUseJoker());
        return instance;
    }
}
