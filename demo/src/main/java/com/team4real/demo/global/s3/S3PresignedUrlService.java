package com.team4real.demo.global.s3;

import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.global.config.AwsConfig;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3PresignedUrlService {
    private final AwsConfig awsConfig;
    private final AuthUserService authUserService;
    private static final List<String> FORBIDDEN_CHAR = List.of("/", "\\", "..", "\"", ":", "*", "?", "<", ">", "|");
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");

    // fileName 유효성 검증
    private void validateImageFileName(String fileName) {
        if (fileName == null || fileName.isBlank() || FORBIDDEN_CHAR.stream().anyMatch(fileName::contains)) {
            throw new CustomException(ErrorCode.INVALID_FILE_FORMAT);
        }
        String lower = fileName.toLowerCase();
        if (ALLOWED_IMAGE_EXTENSIONS.stream().noneMatch(lower::endsWith)) {
            throw new CustomException(ErrorCode.INVALID_FILE_FORMAT);
        }
    }

    public URL generatePresignedUrl(String fileName, String contentType, long expirationMinutes) {
        validateImageFileName(fileName);
        String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        String uniqueFileName = UUID.randomUUID() + ext;
        String key = String.format("profile/%d/%s",
                authUserService.getCurrentAuthUser().getAuthUserId(),
                uniqueFileName
        );
        // S3Presigner 객체 생성
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(awsConfig.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        awsConfig.getCredentials().getAccessKey(),
                                        awsConfig.getCredentials().getSecretKey()
                                )
                        )
                )
                .build();
        // PutObjectRequest 세팅
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsConfig.getS3().getBucket())
                .key(key)
                .contentType(contentType)
                .build();
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(builder -> builder
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
        );
        presigner.close();
        return presignedRequest.url();
    }
}