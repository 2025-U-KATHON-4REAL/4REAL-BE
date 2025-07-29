package com.team4real.demo.domain.brand.controller;

import com.team4real.demo.domain.brand.service.BrandLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BrandLike")
@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandLikeController {
    private final BrandLikeService brandLikeService;

    @Operation(summary = "브랜드 찜 추가")
    @PostMapping("/{brandId}/like")
    public ResponseEntity<Void> like(@PathVariable Long brandId) {
        brandLikeService.likeBrand(brandId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "브랜드 찜 삭제")
    @DeleteMapping("/{brandId}/like")
    public ResponseEntity<Void> unlike(@PathVariable Long brandId) {
        brandLikeService.unlikeBrand(brandId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "브랜드 찜 상태 확인")
    @GetMapping("/{brandId}/like-status")
    public ResponseEntity<Boolean> likeStatus(@PathVariable Long brandId) {
        return ResponseEntity.ok(brandLikeService.hasLiked(brandId));
    }
}
