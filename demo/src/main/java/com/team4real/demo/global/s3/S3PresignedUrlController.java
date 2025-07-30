package com.team4real.demo.global.s3;

import com.team4real.demo.domain.auth.service.AuthUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Map;

@Tag(name = "S3 Presigned URL 발급")
@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3PresignedUrlController {
    private final S3PresignedUrlService s3PresignedUrlService;
    private final AuthUserService authUserService;

    @Operation(summary = "프로필 이미지 업로드를 위한 Presigned URL 발급")
    @PostMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrlForProfile(
            @RequestParam String fileName,
            @RequestParam String contentType
    ) {
        // fileName 유효성 검증
        if (fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        // 서버에서 prefix+fileName 구조로 key 생성
        String key = String.format("profile/role=%s/authUserId=%d/%s",
                authUserService.getCurrentAuthUser().getRole().name(),
                authUserService.getCurrentAuthUser().getAuthUserId(),
                fileName
        );
        URL presignedUrl = s3PresignedUrlService.generatePresignedUrl(key, contentType, 10);
        return ResponseEntity.ok(Map.of("url", presignedUrl.toString()));
    }
}
