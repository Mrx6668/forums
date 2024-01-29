package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_post_comment")
public class PostComment {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer uid;
    Integer pid;
    String content;
    Date time;
    Integer quote;
}
