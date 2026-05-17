package com.rummikub.entity;

import lombok.Data;

@Data
public class GameRules {

    private Integer initialMeldScore = 30;

    private Integer turnTimeSeconds;

    private Integer tilesPerPlayer = 14;

    private Boolean useJoker = true;

    private Integer tableCols = 16;

    private Integer tableRows = 8;
}
