package com.team4real.demo.domain.user.service;

import com.team4real.demo.domain.user.entity.User;
import com.team4real.demo.domain.user.entity.UserType;
import com.team4real.demo.domain.user.repository.UserRepository;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하는 경우")
    void existsByEmail_WhenExists_ReturnsTrue() {
        // given
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when
        boolean exists = userService.existsByEmail(email);

        // then
        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않는 경우")
    void existsByEmail_WhenNotExists_ReturnsFalse() {
        // given
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // when
        boolean exists = userService.existsByEmail(email);

        // then
        assertThat(exists).isFalse();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 성공")
    void getUserByEmail_WhenExists_ReturnsUser() {
        // given
        String email = "test@example.com";
        User user = createTestUser(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        User foundUser = userService.getUserByEmail(email);

        // then
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 존재하지 않는 경우 예외 발생")
    void getUserByEmail_WhenNotExists_ThrowsException() {
        // given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("ID로 사용자 조회 - 성공")
    void getUserById_WhenExists_ReturnsUser() {
        // given
        Long userId = 1L;
        User user = createTestUser("test@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User foundUser = userService.getUserById(userId);

        // then
        assertThat(foundUser).isEqualTo(user);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("ID로 사용자 조회 - 존재하지 않는 경우 예외 발생")
    void getUserById_WhenNotExists_ThrowsException() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("사용자 생성 - 성공")
    void createUser_Success() {
        // given
        String email = "test@example.com";
        String encryptedPassword = "encryptedPassword123";
        String nickname = "테스트유저";
        String phoneNumber = "010-1234-5678";
        UserType userType = UserType.CREATOR;

        User savedUser = createTestUser(email);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        User createdUser = userService.createUser(email, encryptedPassword, nickname, phoneNumber, userType);

        // then
        assertThat(createdUser).isEqualTo(savedUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("현재 사용자 닉네임 조회 - 성공")
    void getCurrentUserNickname_Success() {
        // given
        String email = "test@example.com";
        String nickname = "테스트유저";
        User user = createTestUser(email);
        user = User.builder()
                .email(email)
                .encryptedPassword("password")
                .nickname(nickname)
                .userType(UserType.CREATOR)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        String currentNickname = userService.getCurrentUserNickname();

        // then
        assertThat(currentNickname).isEqualTo(nickname);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("현재 사용자 조회 - 성공")
    void getCurrentUser_Success() {
        // given
        String email = "test@example.com";
        User user = createTestUser(email);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        User currentUser = userService.getCurrentUser();

        // then
        assertThat(currentUser).isEqualTo(user);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("현재 사용자 조회 - 인증되지 않은 경우 예외 발생")
    void getCurrentUser_WhenNotAuthenticated_ThrowsException() {
        // given
        String email = "test@example.com";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_AUTHENTICATED);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("사용자 정보 조회 - 성공")
    void getUserInfo_Success() {
        // given
        Long userId = 1L;
        User user = createTestUser("test@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User userInfo = userService.getUserInfo(userId);

        // then
        assertThat(userInfo).isEqualTo(user);
        verify(userRepository).findById(userId);
    }

    private User createTestUser(String email) {
        return User.builder()
                .email(email)
                .encryptedPassword("encryptedPassword123")
                .nickname("테스트유저")
                .phoneNumber("010-1234-5678")
                .userType(UserType.CREATOR)
                .build();
    }
} 