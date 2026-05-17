package com.rummikub.service;

import com.rummikub.entity.dto.LoginUserSession;
import com.rummikub.entity.dto.RoomRuntimeDTO;
import com.rummikub.entity.po.GameRoomPO;
import com.rummikub.entity.request.CreateRoomRequest;
import com.rummikub.enums.ExceptionEnum;
import com.rummikub.enums.RoomStatus;
import com.rummikub.exception.CustomException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomRuntimeServiceTest {

    @Test
    void requestCheckUsesDefaultRules() {
        CreateRoomRequest request = new CreateRoomRequest();

        request.check();

        assertEquals(4, request.getMaxPlayers());
        assertEquals(30, request.getInitialMeldScore());
        assertEquals(60, request.getTurnTimeSeconds());
        assertTrue(request.getUseJoker());
    }

    @Test
    void requestCheckRejectsInvalidMaxPlayers() {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setMaxPlayers(5);

        CustomException exception = assertThrows(CustomException.class, request::check);

        assertEquals(ExceptionEnum.ROOM_MAX_PLAYERS_ERROR, exception.getExceptionEnum());
    }

    @Test
    void createRuntimeRoomAddsOwnerPlayer() {
        RoomRuntimeService roomRuntimeService = new RoomRuntimeService();
        CreateRoomRequest request = new CreateRoomRequest();
        request.check();
        GameRoomPO room = GameRoomPO.getInstance("00010001", 1L, request);
        room.setId(100L);

        RoomRuntimeDTO runtimeRoom = roomRuntimeService.createRoom(room, buildSession());

        assertEquals(100L, runtimeRoom.getRoomId());
        assertEquals("00010001", runtimeRoom.getRoomCode());
        assertEquals(1, runtimeRoom.getPlayers().size());
        assertEquals(1, runtimeRoom.getPlayers().getFirst().getSeatNo());
        assertTrue(runtimeRoom.getPlayers().getFirst().getReady());
    }

    private LoginUserSession buildSession() {
        LoginUserSession session = new LoginUserSession();
        session.setUserId(1L);
        session.setUsername("alice");
        session.setNickname("Alice");
        session.setStatus(1);
        return session;
    }
}
