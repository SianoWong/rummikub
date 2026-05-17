package com.rummikub.service;

import com.rummikub.entity.dto.LoginUserSession;
import com.rummikub.entity.dto.RoomRuntimeDTO;
import com.rummikub.entity.po.GameRoomPO;
import com.rummikub.entity.request.CreateRoomRequest;
import com.rummikub.entity.response.CreateRoomResponse;
import com.rummikub.repository.GameRoomRepository;
import com.rummikub.util.RoomCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final GameRoomRepository gameRoomRepository;
    private final RoomRuntimeService roomRuntimeService;

    public RoomService(GameRoomRepository gameRoomRepository, RoomRuntimeService roomRuntimeService) {
        this.gameRoomRepository = gameRoomRepository;
        this.roomRuntimeService = roomRuntimeService;
    }

    @Transactional
    public CreateRoomResponse createRoom(CreateRoomRequest request, LoginUserSession session) {
        request.check();
        String roomCode = RoomCodeGenerator.generate(session.getUserId());
        GameRoomPO room = GameRoomPO.getInstance(
                roomCode,
                session.getUserId(),
                request
        );
        room = gameRoomRepository.save(room);
        RoomRuntimeDTO runtimeRoom = roomRuntimeService.createRoom(room, session);
        return CreateRoomResponse.getInstance(runtimeRoom);
    }
}
