package com.example.backend.entity.vo.respones;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationVO {
    Integer id;
    Integer uid;
    String title;
    String content;
    String type;
    String url;
    Date time;
    Integer browse;
}
