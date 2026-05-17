package com.rummikub.entity.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "game_players")
public class GamePlayerPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "seat_no", nullable = false)
    private Integer seatNo;

    @Column(name = "initial_tile_count", nullable = false)
    private Integer initialTileCount;

    @Column(name = "final_tile_count")
    private Integer finalTileCount;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "rank_no")
    private Integer rankNo;

    @Column(name = "is_winner", nullable = false)
    private Boolean winner;

    @Column(name = "has_initial_melded", nullable = false)
    private Boolean hasInitialMelded;

    public static GamePlayerPO getInstance(Long gameId, Long userId, Integer seatNo, Integer initialTileCount) {
        GamePlayerPO instance = new GamePlayerPO();
        instance.setGameId(gameId);
        instance.setUserId(userId);
        instance.setSeatNo(seatNo);
        instance.setInitialTileCount(initialTileCount);
        instance.setScore(0);
        instance.setWinner(false);
        instance.setHasInitialMelded(false);
        return instance;
    }
}
