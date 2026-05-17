package com.rummikub.repository;

import com.rummikub.entity.po.GameRoomPO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRoomRepository extends JpaRepository<GameRoomPO, Long> {

    boolean existsByRoomCode(String roomCode);
}
