package com.example.backend.controller.exception;

import com.example.backend.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ValidationController {
    @ExceptionHandler(ValidationException.class)
    public RestBean<Void> validationException(ValidationException e) {
        log.warn("ValidationException: " + e.getMessage());
        return RestBean.failure(400, "请求参数有误");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestBean<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMesssage = new StringBuilder("参数校验失败: ");

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMesssage.append(fieldError.getDefaultMessage()).append(", ");
        }

        return RestBean.failure(400, errorMesssage.toString());
    }
}
