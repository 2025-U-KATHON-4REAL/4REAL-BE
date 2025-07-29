package com.team4real.demo.domain.creator.entity;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Creator extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long creatorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", nullable = false, unique = true)
    private AuthUser authUser;

    @Column(nullable = false)
    private String name;

    private String image;
    private String realName;
    private String birth;
    private String gender;

    @Builder
    public Creator(AuthUser authUser, String name, String image, String realName, String birth, String gender) {
        this.authUser = authUser;
        this.name = name;
        this.image = image;
        this.realName = realName;
        this.birth = birth;
        this.gender = gender;
    }
}
