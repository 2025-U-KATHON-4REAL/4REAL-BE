package com.team4real.demo.domain.brand.entity;

import com.team4real.demo.domain.auth.entity.AuthUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"auth_user_id", "brand_id"})})
public class BrandLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUser authUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;
}