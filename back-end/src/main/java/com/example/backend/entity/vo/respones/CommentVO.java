package com.example.backend.entity.vo.respones;

import lombok.Data;

import java.util.Date;

@Data
public class CommentVO {
    int id;
    String content;
    Date time;
    String quote;
    User user;
    @Data
    public static class User{
        Integer id;
        String username;
        String avatar;
        Integer gender;
        String qq;
        String wx;
        String phone;
        String email;
    }
}
