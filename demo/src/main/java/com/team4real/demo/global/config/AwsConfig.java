package com.team4real.demo.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsConfig {
    private Credentials credentials;
    private String region;
    private S3 s3;

    @Getter @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }
    @Getter @Setter
    public static class S3 {
        private String bucket;
    }
}
