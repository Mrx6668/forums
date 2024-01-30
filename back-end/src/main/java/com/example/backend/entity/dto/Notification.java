package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.utils.BaseData;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName("db_notification")
public class Notification implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer uid;
    String title;
    String content;
    String type;
    String url;
    Date time;
    Integer browse;

    public Notification(Integer uid, String title, String content, String type, String url, Date time) {
        this.uid = uid;
        this.title = title;
        this.content = content;
        this.type = type;
        this.url = url;
        this.time = time;
    }
}
