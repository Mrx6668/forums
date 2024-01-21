package com.example.backend.controller.exception;

import com.example.backend.entity.RestBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MinioException {
//    @ExceptionHandler({
//            InternalException.class,
//            InvalidKeyException.class,
//            InvalidResponseException.class,
//            NoSuchAlgorithmException.class,
//            ServerException.class,
//            XmlParserException.class
//    })
    public static RestBean<Void> handleMinioException(int code,String message) {
        log.info("Error: " +HttpStatus.INTERNAL_SERVER_ERROR);
        return RestBean.failure(code, message);
    }
}
