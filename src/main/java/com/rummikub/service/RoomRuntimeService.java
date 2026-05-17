package com.rummikub.service;

import com.rummikub.entity.dto.LoginUserSession;
import com.rummikub.entity.dto.RoomRuntimeDTO;
import com.rummikub.entity.po.GameRoomPO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomRuntimeService {

    private final Map<Long, RoomRuntimeDTO> roomMap = new ConcurrentHashMap<>();

    public RoomRuntimeDTO createRoom(GameRoomPO room, LoginUserSession ownerSession) {
        RoomRuntimeDTO runtimeRoom = RoomRuntimeDTO.getInstance(room, ownerSession);
        roomMap.put(room.getId(), runtimeRoom);
        return runtimeRoom;
    }

    public RoomRuntimeDTO getRoom(Long roomId) {
        return roomMap.get(roomId);
    }
}
