package com.team4real.demo.domain.user.entity;

import com.team4real.demo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.ZoneId;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_account")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long userId;

    @Column(unique = true, nullable = false)
    @NotNull
    private String email;

    @NotNull
    @Column(nullable = false)
    private String encryptedPassword;

    @NotNull
    @Column(nullable = false)
    private String nickname;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private UserType userType;

    @Builder
    public User(String email, String encryptedPassword, String nickname, String phoneNumber, UserType userType) {
        this.email = email;
        this.encryptedPassword = encryptedPassword;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }
}
