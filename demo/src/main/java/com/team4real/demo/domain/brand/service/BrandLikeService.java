package com.team4real.demo.domain.brand.service;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.brand.entity.BrandLike;
import com.team4real.demo.domain.brand.repository.BrandLikeRepository;
import com.team4real.demo.domain.brand.repository.BrandRepository;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandLikeService {
    private final BrandRepository brandRepository;
    private final BrandLikeRepository brandLikeRepository;
    private final AuthUserService authUserService;

    @Transactional
    public void likeBrand(Long brandId) {
        AuthUser user = authUserService.getCurrentAuthUser();
        Brand brand = brandRepository.findById(brandId).orElseThrow(()->new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!brandLikeRepository.existsByAuthUserAndBrand(user, brand)) {
            brandLikeRepository.save(BrandLike.builder().authUser(user).brand(brand).build());
            brandRepository.incrementLikeCount(brandId);
        }
    }

    @Transactional
    public void unlikeBrand(Long brandId) {
        AuthUser authUser = authUserService.getCurrentAuthUser();
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        brandLikeRepository.findByAuthUserAndBrand(authUser, brand).ifPresent(like -> {
            brandLikeRepository.delete(like);
            brandRepository.decrementLikeCount(brandId);
        });
    }

    @Transactional(readOnly = true)
    public boolean hasLiked(Long brandId) {
        AuthUser authUser = authUserService.getCurrentAuthUser();
        Brand brand = brandRepository.findById(brandId).orElseThrow(()->new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        return brandLikeRepository.existsByAuthUserAndBrand(authUser, brand);
    }
}
