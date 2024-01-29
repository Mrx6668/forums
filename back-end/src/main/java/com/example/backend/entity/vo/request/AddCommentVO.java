package com.example.backend.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AddCommentVO {
    @Min(1)
    int pid;
    String content;
    @Min(-1)
    int quote;//引用
}
