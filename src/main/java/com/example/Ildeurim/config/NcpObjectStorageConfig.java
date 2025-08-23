package com.example.Ildeurim.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class NcpObjectStorageConfig {

    @Value("${ncp.storage.endpoint:https://kr.object.ncloudstorage.com}")
    private String endpoint;        // NCP Object Storage 엔드포인트
    @Value("${ncp.storage.region:kr-standard}")
    private String region;          // NCP 리전(보통 kr-standard)
    @Value("${ncp.access-key}")     // NCP 콘솔의 Access Key
    private String accessKey;
    @Value("${ncp.secret-key}")     // NCP 콘솔의 Secret Key
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true) // NCP/커스텀 엔드포인트에 안전
                                .build()
                )
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                )
                .build();
    }
}

