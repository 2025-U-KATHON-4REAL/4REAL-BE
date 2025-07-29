package com.team4real.demo.domain.user.controller;

import com.team4real.demo.domain.user.dto.UserNicknameResponseDto;
import com.team4real.demo.domain.user.dto.UserResponseDto;
import com.team4real.demo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "유저 정보 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable final Long userId) {
        UserResponseDto userResponse = UserResponseDto.from(userService.getUserInfo(userId));
        return ResponseEntity.ok().body(userResponse);
    }

    @Operation(summary = "현재 유저 닉네임 조회")
    @GetMapping("/nickname")
    public ResponseEntity<UserNicknameResponseDto> getCurrentUserNickname() {
        String nickname = userService.getCurrentUserNickname();
        UserNicknameResponseDto response = UserNicknameResponseDto.from(nickname);
        return ResponseEntity.ok().body(response);
    }
}