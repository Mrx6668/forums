package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("db_image_store")
public class StoreImage {
    Integer uid;
    String name;
    Date time;
    double size;
}
