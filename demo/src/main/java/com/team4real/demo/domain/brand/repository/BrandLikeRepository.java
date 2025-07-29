package com.team4real.demo.domain.brand.repository;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.brand.entity.BrandLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandLikeRepository extends JpaRepository<BrandLike, Long> {
    Optional<BrandLike> findByAuthUserAndBrand(AuthUser user, Brand brand);
    boolean existsByAuthUserAndBrand(AuthUser user, Brand brand);
    void deleteByAuthUserAndBrand(AuthUser user, Brand brand);
}