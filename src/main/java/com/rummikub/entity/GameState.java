package com.rummikub.entity;

import com.rummikub.enums.GameStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GameState {

    private Long gameId;

    private Long roomId;

    private GameStatus status = GameStatus.WAITING;

    private Long version = 0L;

    private Integer tileSchemaVersion = 1;

    private GameRules rules = new GameRules();

    private List<GamePlayerState> players = new ArrayList<>();

    private TurnState turn;

    private TileBag tileBag = new TileBag();

    private TableState table = new TableState();

    private TurnDraft turnDraft;

    public static GameState getInstance(Long gameId, Long roomId, GameStatus status, Long version,
                                        Integer tileSchemaVersion, GameRules rules,
                                        List<GamePlayerState> players, TurnState turn,
                                        TileBag tileBag, TableState table, TurnDraft turnDraft) {
        GameState instance = new GameState();
        instance.setGameId(gameId);
        instance.setRoomId(roomId);
        instance.setStatus(status);
        instance.setVersion(version);
        instance.setTileSchemaVersion(tileSchemaVersion);
        instance.setRules(rules);
        instance.setPlayers(players);
        instance.setTurn(turn);
        instance.setTileBag(tileBag);
        instance.setTable(table);
        instance.setTurnDraft(turnDraft);
        return instance;
    }
}
