package com.example.backend.controller.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockException extends RuntimeException{
    private Integer code = 403;
    private String message = "操作频繁，请稍后重试";

    public BlockException() {
    }
}
