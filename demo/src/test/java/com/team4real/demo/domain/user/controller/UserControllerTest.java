package com.team4real.demo.domain.user.controller;

import com.team4real.demo.domain.user.dto.UserNicknameResponseDto;
import com.team4real.demo.domain.user.dto.UserResponseDto;
import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.entity.UserType;
import com.team4real.demo.domain.user.service.UserService;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("유저 정보 조회 - 성공")
    void getUserInfo_Success() {
        // given
        Long userId = 1L;
        User user = createTestUser();
        when(userService.getUserInfo(userId)).thenReturn(user);

        // when
        ResponseEntity<UserResponseDto> response = userController.getUserInfo(userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("test@example.com");
        assertThat(response.getBody().nickname()).isEqualTo("테스트유저");
        assertThat(response.getBody().userType()).isEqualTo(UserType.CREATOR);
        verify(userService).getUserInfo(userId);
    }

    @Test
    @DisplayName("유저 정보 조회 - 사용자가 존재하지 않는 경우")
    void getUserInfo_WhenUserNotFound_ThrowsException() {
        // given
        Long userId = 1L;
        when(userService.getUserInfo(userId))
                .thenThrow(new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // when & then
        assertThatThrownBy(() -> userController.getUserInfo(userId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
        verify(userService).getUserInfo(userId);
    }

    @Test
    @DisplayName("현재 유저 닉네임 조회 - 성공")
    void getCurrentUserNickname_Success() {
        // given
        String nickname = "테스트유저";
        when(userService.getCurrentUserNickname()).thenReturn(nickname);

        // when
        ResponseEntity<UserNicknameResponseDto> response = userController.getCurrentUserNickname();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().nickname()).isEqualTo(nickname);
        verify(userService).getCurrentUserNickname();
    }

    @Test
    @DisplayName("현재 유저 닉네임 조회 - 인증되지 않은 경우")
    void getCurrentUserNickname_WhenNotAuthenticated_ThrowsException() {
        // given
        when(userService.getCurrentUserNickname())
                .thenThrow(new CustomException(ErrorCode.NOT_AUTHENTICATED));

        // when & then
        assertThatThrownBy(() -> userController.getCurrentUserNickname())
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_AUTHENTICATED);
        verify(userService).getCurrentUserNickname();
    }

    @Test
    @DisplayName("현재 유저 닉네임 조회 - 빈 닉네임인 경우")
    void getCurrentUserNickname_WithEmptyNickname_Success() {
        // given
        String nickname = "";
        when(userService.getCurrentUserNickname()).thenReturn(nickname);

        // when
        ResponseEntity<UserNicknameResponseDto> response = userController.getCurrentUserNickname();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().nickname()).isEqualTo(nickname);
        verify(userService).getCurrentUserNickname();
    }

    private User createTestUser() {
        return User.builder()
                .email("test@example.com")
                .encryptedPassword("encryptedPassword123")
                .nickname("테스트유저")
                .phoneNumber("010-1234-5678")
                .userType(UserType.CREATOR)
                .build();
    }
} 