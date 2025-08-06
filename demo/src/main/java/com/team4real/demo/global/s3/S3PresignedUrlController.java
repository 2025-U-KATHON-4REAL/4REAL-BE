package com.team4real.demo.global.s3;

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

    @Operation(summary = "프로필 이미지 업로드를 위한 Presigned URL 발급")
    @PostMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrlForProfile(
            @RequestParam String fileName,
            @RequestParam String contentType
    ) {
        URL presignedUrl = s3PresignedUrlService.generatePresignedUrl(fileName, contentType, 10);
        return ResponseEntity.ok(Map.of("url", presignedUrl.toString()));
    }
}
