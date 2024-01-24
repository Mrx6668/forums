package com.example.backend.entity.vo.respones;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostPreviewVO {
    int id;
    int type;
    String title;
    String content;
    List<String> images;
    Date createTime;
    Integer uid;
    String username;
    String avatar;
}
