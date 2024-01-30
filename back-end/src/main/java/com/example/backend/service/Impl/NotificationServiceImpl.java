package com.example.backend.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Notification;
import com.example.backend.entity.vo.respones.NotificationVO;
import com.example.backend.mapper.NotificationMapper;
import com.example.backend.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public List<NotificationVO> findUserNotification(int uid) {

        return this.list(Wrappers.<Notification>query().eq("uid", uid))
                .stream()
                .map(notification -> notification.asViewObject(NotificationVO.class))
                .toList();
    }

    @Override
    public void deleteUserNotification(int uid, int id) {
        this.remove(Wrappers.<Notification>query().eq("uid", uid).eq("id",id));
    }

    @Override
    public void deleteAllNotification(int uid) {
        this.remove(Wrappers.<Notification>query().eq("uid", uid));
    }

    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        this.save(new Notification(uid,title,content,type,url,new Date()));
    }
}
