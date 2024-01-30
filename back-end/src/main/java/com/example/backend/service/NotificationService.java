package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.dto.Notification;
import com.example.backend.entity.vo.respones.NotificationVO;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    List<NotificationVO> findUserNotification(int uid);

    void deleteUserNotification(int uid, int id);

    void deleteAllNotification(int uid);

    void addNotification(int uid, String title, String content, String type, String url);
}
