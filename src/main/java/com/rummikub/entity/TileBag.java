package com.rummikub.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TileBag {

    private List<Short> remainingTiles = new ArrayList<>();

    public static TileBag getInstance(List<Short> remainingTiles) {
        TileBag instance = new TileBag();
        instance.setRemainingTiles(remainingTiles);
        return instance;
    }
}
