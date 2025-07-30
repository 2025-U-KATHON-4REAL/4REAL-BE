package com.team4real.demo.global.s3;

import com.team4real.demo.global.config.AwsConfig;
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


@RequiredArgsConstructor
@Service
public class S3PresignedUrlService {
    private final AwsConfig awsProperties;

    public URL generatePresignedUrl(String key, String contentType, long expirationMinutes) {
        // S3Presigner 객체 생성 (Bean으로 등록하지 않고 매번 생성/close)
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        awsProperties.getCredentials().getAccessKey(),
                                        awsProperties.getCredentials().getSecretKey()
                                )
                        )
                )
                .build();
        // PutObjectRequest 세팅
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucket())
                .key(key)
                .contentType(contentType)
                .build();
        // Presigned URL 생성
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(builder -> builder
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
        );
        presigner.close(); // 자원 해제
        return presignedRequest.url();
    }
}