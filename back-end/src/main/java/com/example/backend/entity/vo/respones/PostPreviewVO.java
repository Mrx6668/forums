package com.example.backend.entity.vo.respones;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostPreviewVO {
    int id;
    int PostType;
    String title;
    String content;
    List<String> images;
    Date createTime;
    Integer uid;
    String username;
    String avatar;
    int like;
    int collect;
}
