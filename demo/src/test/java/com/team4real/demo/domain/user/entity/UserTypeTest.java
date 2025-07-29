package com.team4real.demo.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserType enum 테스트")
class UserTypeTest {

    @Test
    @DisplayName("UserType enum 값 확인")
    void userTypeValues() {
        // when
        UserType[] userTypes = UserType.values();

        // then
        assertThat(userTypes).hasSize(3);
        assertThat(userTypes).contains(UserType.CREATOR, UserType.BRAND, UserType.ADMIN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CREATOR", "BRAND", "ADMIN"})
    @DisplayName("UserType.valueOf() 테스트")
    void userTypeValueOf(String userTypeName) {
        // when
        UserType userType = UserType.valueOf(userTypeName);

        // then
        assertThat(userType).isNotNull();
        assertThat(userType.name()).isEqualTo(userTypeName);
    }

    @Test
    @DisplayName("UserType enum 순서 확인")
    void userTypeOrdinal() {
        // when & then
        assertThat(UserType.CREATOR.ordinal()).isEqualTo(0);
        assertThat(UserType.BRAND.ordinal()).isEqualTo(1);
        assertThat(UserType.ADMIN.ordinal()).isEqualTo(2);
    }
} 