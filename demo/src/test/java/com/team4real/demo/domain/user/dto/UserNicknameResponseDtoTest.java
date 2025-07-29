package com.team4real.demo.domain.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserNicknameResponseDto 테스트")
class UserNicknameResponseDtoTest {

    @Test
    @DisplayName("닉네임으로부터 UserNicknameResponseDto 생성")
    void from_Nickname_Success() {
        // given
        String nickname = "테스트유저";

        // when
        UserNicknameResponseDto responseDto = UserNicknameResponseDto.from(nickname);

        // then
        assertThat(responseDto.nickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("빈 문자열 닉네임으로 UserNicknameResponseDto 생성")
    void from_EmptyNickname_Success() {
        // given
        String nickname = "";

        // when
        UserNicknameResponseDto responseDto = UserNicknameResponseDto.from(nickname);

        // then
        assertThat(responseDto.nickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("null 닉네임으로 UserNicknameResponseDto 생성")
    void from_NullNickname_Success() {
        // given
        String nickname = null;

        // when
        UserNicknameResponseDto responseDto = UserNicknameResponseDto.from(nickname);

        // then
        assertThat(responseDto.nickname()).isNull();
    }

    @Test
    @DisplayName("UserNicknameResponseDto record 구조 확인")
    void userNicknameResponseDtoRecordStructure() {
        // given
        String nickname = "테스트유저";

        // when
        UserNicknameResponseDto responseDto = UserNicknameResponseDto.from(nickname);

        // then
        assertThat(responseDto).isInstanceOf(UserNicknameResponseDto.class);
        assertThat(responseDto.nickname()).isEqualTo(nickname);
    }
} 