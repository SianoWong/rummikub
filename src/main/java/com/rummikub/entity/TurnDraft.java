package com.rummikub.entity;

import lombok.Data;

@Data
public class TurnDraft {

    private Long baseVersion;

    private TableState tableSnapshot;

    public static TurnDraft getInstance(Long baseVersion, TableState tableSnapshot) {
        TurnDraft instance = new TurnDraft();
        instance.setBaseVersion(baseVersion);
        instance.setTableSnapshot(tableSnapshot);
        return instance;
    }
}
