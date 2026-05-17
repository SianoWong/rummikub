package com.rummikub.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableState {

    private List<TableSet> sets = new ArrayList<>();

    public static TableState getInstance(List<TableSet> sets) {
        TableState instance = new TableState();
        instance.setSets(sets);
        return instance;
    }
}
