package com.team4real.demo.domain.brand.entity;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long brandId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", nullable = false, unique = true)
    private AuthUser authUser;

    @Column(nullable = false)
    private String name;

    private String image;
    private String keyword;
    private String description;

    @Column(nullable = false)
    private int likeCount = 0;

    @Builder
    public Brand(AuthUser authUser, String name, String image, String keyword, String description) {
        this.authUser = authUser;
        this.name = name;
        this.image = image;
        this.keyword = keyword;
        this.description = description;
    }
}
