package com.rummikub.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TurnState {

    private Integer turnNo;

    private Long currentUserId;

    private OffsetDateTime startedAt;

    private OffsetDateTime deadlineAt;

    private Integer actionSeq = 0;

    public static TurnState getInstance(Integer turnNo, Long currentUserId,
                                        OffsetDateTime startedAt, OffsetDateTime deadlineAt,
                                        Integer actionSeq) {
        TurnState instance = new TurnState();
        instance.setTurnNo(turnNo);
        instance.setCurrentUserId(currentUserId);
        instance.setStartedAt(startedAt);
        instance.setDeadlineAt(deadlineAt);
        instance.setActionSeq(actionSeq);
        return instance;
    }
}
