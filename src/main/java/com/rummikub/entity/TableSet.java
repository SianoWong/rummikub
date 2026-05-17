package com.rummikub.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableSet {

    private Integer setId;

    private List<Short> tiles = new ArrayList<>();

    /**
     * Grid position in [col, row] format. col range: 0-15, row range: 0-7.
     */
    private List<Integer> pos = new ArrayList<>(2);

    public static TableSet getInstance(Integer setId, List<Short> tiles, List<Integer> pos) {
        TableSet instance = new TableSet();
        instance.setSetId(setId);
        instance.setTiles(tiles);
        instance.setPos(pos);
        return instance;
    }
}
