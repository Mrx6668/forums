package com.example.backend.entity.vo.respones;

import lombok.AllArgsConstructor;
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
    Interact interact;
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
    @Data
    @AllArgsConstructor
    public static class Interact{
        Boolean like;
        Boolean collect;
    }
}
