package com.example.backend.entity.vo.respones;

import lombok.Data;

import java.util.Date;

@Data
public class PostDetailVO {
    Integer id;
    String title;
    String content;
    Integer postType;
    Date createTime;
    User user;
    @Data
    public static class User {
        Integer id;
        String username;
        String avatar;
        String desc;
        Integer gender;
        String qq;
        String wx;
        String phone;
        String email;
    }
}
