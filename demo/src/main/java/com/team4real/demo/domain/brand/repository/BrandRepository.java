package com.team4real.demo.domain.brand.repository;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.brand.entity.Brand;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByAuthUser(AuthUser authUser);

    @Modifying(clearAutomatically = true)
    @Query("update Brand b set b.likeCount = b.likeCount + 1 where b.brandId = :brandId")
    void incrementLikeCount(@Param("brandId") Long brandId);

    @Modifying(clearAutomatically = true)
    @Query("update Brand b set b.likeCount = b.likeCount - 1 where b.brandId = :brandId")
    void decrementLikeCount(@Param("brandId") Long brandId);
}
