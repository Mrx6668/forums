package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@TableName("db_post_type")
public class PostDTO {
    @Min(1)
    Integer id;
    String title;
    @TableField("`desc`")
    String desc;
}
