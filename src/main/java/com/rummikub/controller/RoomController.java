package com.rummikub.controller;

import com.rummikub.entity.dto.LoginUserSession;
import com.rummikub.entity.request.CreateRoomRequest;
import com.rummikub.entity.response.ApiResponse;
import com.rummikub.entity.response.CreateRoomResponse;
import com.rummikub.service.RoomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ApiResponse<CreateRoomResponse> createRoom(@RequestBody CreateRoomRequest request,
                                                      @AuthenticationPrincipal LoginUserSession session) {
        return ApiResponse.success(roomService.createRoom(request, session));
    }
}
