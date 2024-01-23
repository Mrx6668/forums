package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.service.ImageService;
import com.example.backend.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/image")
public class ImageController {
    @Resource
    ImageService imageService;

    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file") MultipartFile avatar,
                                         @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        if (avatar.getSize() > Const.MAX_AVATAR_SIZE) {
            return RestBean.failure(400, "头像不能大于1.5MB");
        }
        log.info("用户：{}  正在上传头像：{}", userId, avatar.getName());
        String message = imageService.uploadAvatar(avatar, userId);
        return message.startsWith("/") ? RestBean.success(message) : RestBean.failure(400, message);
    }

    @GetMapping("/get")
    public void getImage(HttpServletResponse response,@RequestParam String imageName){
        imageService.getImage(response,imageName);
    }

    @PostMapping("/cache")
    public RestBean<String> uploadImage(@RequestParam("file") MultipartFile image,
                                        @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                        HttpServletResponse response){
        if (image.getSize() > Const.MAX_IMAGE_SIZE)
            return RestBean.failure(400,"图片不能大于5MB");
        log.info("用户：{}  正在上传图片：{}", userId, image.getName());
        String message = imageService.uploadImage(image, userId,response);
        return message.startsWith("/") ? RestBean.success(message) : RestBean.failure(400, message);
    }
}
