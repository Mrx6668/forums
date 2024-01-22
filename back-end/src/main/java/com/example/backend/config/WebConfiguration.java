package com.example.backend.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class WebConfiguration {
    @Value("${minio.url}")
    String minioUrl;
    @Value("${minio.username}")
    String minioUsername;
    @Value("${minio.password}")
    String minioPassword;
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public MinioClient initMinIoClient(){
        log.info("MinIoClient 正在初始化");
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioUsername,minioPassword)
                .build();
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
