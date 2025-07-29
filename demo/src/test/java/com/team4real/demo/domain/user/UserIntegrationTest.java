package com.team4real.demo.domain.user;

import com.team4real.demo.domain.user.controller.UserController;
import com.team4real.demo.domain.user.dto.UserNicknameResponseDto;
import com.team4real.demo.domain.user.dto.UserResponseDto;
import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.entity.UserType;
import com.team4real.demo.domain.user.service.UserService;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("User 도메인 통합 테스트")
class UserIntegrationTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private SecurityContext securityContext;

    @Test
    @DisplayName("User 도메인 전체 플로우 테스트 - 성공 케이스")
    void userDomainFlow_Success() {
        // given
        Long userId = 1L;
        User user = createTestUser();
        String nickname = "테스트유저";

        when(userService.getUserInfo(userId)).thenReturn(user);
        when(userService.getCurrentUserNickname()).thenReturn(nickname);

        // when
        ResponseEntity<UserResponseDto> userInfoResponse = userController.getUserInfo(userId);
        ResponseEntity<UserNicknameResponseDto> nicknameResponse = userController.getCurrentUserNickname();

        // then
        assertThat(userInfoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userInfoResponse.getBody()).isNotNull();
        assertThat(userInfoResponse.getBody().email()).isEqualTo("test@example.com");
        assertThat(userInfoResponse.getBody().nickname()).isEqualTo("테스트유저");

        assertThat(nicknameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(nicknameResponse.getBody()).isNotNull();
        assertThat(nicknameResponse.getBody().nickname()).isEqualTo("테스트유저");

        verify(userService).getUserInfo(userId);
        verify(userService).getCurrentUserNickname();
    }

    @Test
    @DisplayName("User 도메인 전체 플로우 테스트 - 실패 케이스")
    void userDomainFlow_Failure() {
        // given
        Long userId = 1L;
        when(userService.getUserInfo(userId))
                .thenThrow(new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        when(userService.getCurrentUserNickname())
                .thenThrow(new CustomException(ErrorCode.NOT_AUTHENTICATED));

        // when & then
        assertThatThrownBy(() -> userController.getUserInfo(userId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);

        assertThatThrownBy(() -> userController.getCurrentUserNickname())
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_AUTHENTICATED);

        verify(userService).getUserInfo(userId);
        verify(userService).getCurrentUserNickname();
    }

    @Test
    @DisplayName("UserResponseDto 변환 테스트")
    void userResponseDtoConversion_Success() {
        // given
        User user = createTestUser();

        // when
        UserResponseDto responseDto = UserResponseDto.from(user);

        // then
        assertThat(responseDto.email()).isEqualTo("test@example.com");
        assertThat(responseDto.nickname()).isEqualTo("테스트유저");
        assertThat(responseDto.phoneNumber()).isEqualTo("010-1234-5678");
        assertThat(responseDto.userType()).isEqualTo(UserType.CREATOR);
    }

    @Test
    @DisplayName("UserNicknameResponseDto 변환 테스트")
    void userNicknameResponseDtoConversion_Success() {
        // given
        String nickname = "테스트유저";

        // when
        UserNicknameResponseDto responseDto = UserNicknameResponseDto.from(nickname);

        // then
        assertThat(responseDto.nickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("User 엔티티 생성 테스트")
    void userEntityCreation_Success() {
        // given
        String email = "test@example.com";
        String encryptedPassword = "encryptedPassword123";
        String nickname = "테스트유저";
        String phoneNumber = "010-1234-5678";
        UserType userType = UserType.CREATOR;

        // when
        User user = User.builder()
                .email(email)
                .encryptedPassword(encryptedPassword)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .userType(userType)
                .build();

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getEncryptedPassword()).isEqualTo(encryptedPassword);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(user.getUserType()).isEqualTo(userType);
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