package com.example.backend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
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

    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor(){
        log.info("Mybatis Plus 分页器 注册中 PaginationInnerInterceptor");
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(100L);
        return paginationInnerInterceptor;
    }
}
