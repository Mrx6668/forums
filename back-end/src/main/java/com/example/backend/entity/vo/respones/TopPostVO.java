package com.example.backend.entity.vo.respones;

import lombok.Data;

import java.util.Date;

@Data
public class TopPostVO {
    Integer id;
    Integer uid;
    String title;
    String content;
    Integer postType;
    Date createTime;
}
