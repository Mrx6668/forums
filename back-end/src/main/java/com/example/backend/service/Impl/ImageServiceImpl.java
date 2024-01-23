package com.example.backend.service.Impl;

import com.example.backend.entity.dto.Account;
import com.example.backend.entity.dto.StoreImage;
import com.example.backend.mapper.ImageStoreMapper;
import com.example.backend.service.AccountService;
import com.example.backend.service.ImageService;
import com.example.backend.utils.FlowUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.example.backend.utils.Const.*;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {
    @Resource
    MinioClient minioClient;
    @Resource
    AccountService accountService;
    @Resource
    FlowUtils flowUtils;
    @Resource
    ImageStoreMapper imageStoreMapper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    private String getSuffix(String fileName) {
        int suffixIndex = fileName.lastIndexOf(".");
        if (suffixIndex == -1) return "";
        return fileName.substring(suffixIndex);
    }

    @Override
    public String uploadAvatar(MultipartFile avatar, int userId) {
        String avatarPathName = this.uploadToMinio(avatar, UPLOAD_AVATAR_NAME, userId);
        Account account = accountService.getById(userId);
        boolean deleteOldAvatar = this.removeFromMinio(account.getAvatar());
        if (!deleteOldAvatar) return "删除旧头像出错，请联系管理员";
        boolean update = accountService.update()
                .set("avatar", avatarPathName)
                .eq("id", userId).update();
        if (update) log.info("用户 {} 头像更新成功！URL：{}，Size：{}", userId, avatarPathName, avatar.getSize());
        else return "更新头像记录错误，请联系管理员";
        return avatarPathName;
    }

    /**
     * @param file    文件
     * @param prePath minio 内前置路径
     * @param userId  用户id
     * @return 拼接的路径+名称
     */
    private String uploadToMinio(MultipartFile file, String prePath, int userId) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String suffix = getSuffix(file.getOriginalFilename());
        String PathName = prePath + uuid + suffix;
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(BUCKET)
                    .stream(inputStream, file.getSize(), -1)
                    .object(PathName)
                    .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            log.error("上传出现错误, userId= {}", userId);
            return "上传出现错误，请稍后重试！";
        }
        return PathName;
    }

    private boolean removeFromMinio(String fileAllPath) {
        if (fileAllPath == null || fileAllPath.isEmpty()) return true;
        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder()
                    .bucket(BUCKET)
                    .object(fileAllPath)
                    .build();
            minioClient.removeObject(args);
        } catch (Exception e) {
            log.error("删除出现错误, fileAllPath= {}", fileAllPath);
            return false;
        }
        return true;
    }

    /**
     * @param image  图片文件
     * @param userId 用户id
     * @return 路径或错误信息
     */
    @Override
    public String uploadImage(MultipartFile image, int userId,HttpServletResponse response) {
        String key = FORUM_IMAGE_COUNTER + userId;
        if (!flowUtils.limitPeriodCounterCheck(key, 20, 300)) {
            response.setStatus(400);
            return "您上传图片过于频繁，请稍后再试！";
        }
        Date currentTime = new Date();
        String date = dateFormat.format(currentTime);
        String imageName = this.uploadToMinio(image, UPLOAD_CACHE_NAME + date, userId);
        int insert = imageStoreMapper.insert(StoreImage.builder()
                .uid(userId)
                .size(image.getSize())
                .time(currentTime)
                .name(imageName)
                .build());
        if (insert < 1) return "记录插入失败，请联系管理员！";
        return imageName;
    }
    //    @Override
//    public void getImage(HttpServletResponse response, String imageName) {
//        if (imageName.length() <= 13) handleMinioException(404,"404 NOT FOUND");
//        GetObjectArgs args = GetObjectArgs.builder()
//                .bucket(BUCKET)
//                .object(imageName)
//                .build();
//        try(ServletOutputStream outputStream = response.getOutputStream();) {
//            GetObjectResponse imageResponse = minioClient.getObject(args);
//            IOUtils.copy(imageResponse,outputStream);
//        }catch (Exception e){
//            log.error("传输过程中出现错误："+e);
//            handleMinioException(400,"暂时无法获取图片资源，请稍后重试");
//        }

//    }


    @Override
    public void getImage(HttpServletResponse response, String imageName) {
        if (imageName.length() <= 13) {
            response.setStatus(404);
            return;
        }
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(BUCKET)
                .object(imageName)
                .build();
        log.info("getImage ：{}", args);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            GetObjectResponse imageResponse = minioClient.getObject(args);
            response.setHeader("Cache-Control", "max-age=2592000");
            response.setContentType("image/png");
            IOUtils.copy(imageResponse, outputStream);
        } catch (Exception e) {
            log.error("传输过程中出现错误：" + e);
            response.setStatus(500);
            return;
        }
        response.setStatus(200);
    }
//    private void writeResponse(HttpServletResponse response, RestBean<String> restBean) {
//        response.setContentType("application/json");
//        try (PrintWriter writer = response.getWriter()) {
//            writer.write(restBean.asJsonString());
//        } catch (IOException e) {
//            log.error("Error writing to response", e);
//        }

//    }

}
