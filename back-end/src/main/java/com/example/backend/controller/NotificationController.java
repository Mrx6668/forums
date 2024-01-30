package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.vo.respones.NotificationVO;
import com.example.backend.service.NotificationService;
import com.example.backend.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Resource
    NotificationService notificationService;

    @GetMapping("/list")
    public RestBean<List<NotificationVO>> listNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid) {
        return RestBean.success(notificationService.findUserNotification(uid));
    }

    @GetMapping("/delete")
    public RestBean<Void> deleteNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                             @RequestParam @Min(0) int id) {
        notificationService.deleteUserNotification(uid, id);
        return RestBean.success();
    }

    @GetMapping("/delete-all")
    public RestBean<Void> deleteNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid) {
        notificationService.deleteAllNotification(uid);
        return RestBean.success();
    }
}
