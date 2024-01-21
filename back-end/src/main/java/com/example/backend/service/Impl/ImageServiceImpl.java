package com.example.backend.service.Impl;

import com.example.backend.service.AccountService;
import com.example.backend.service.ImageService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

import static com.example.backend.utils.Const.BUCKET;
import static com.example.backend.utils.Const.UPLOAD_AVATAR_NAME;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {
    @Resource
    MinioClient minioClient;
    @Resource
    AccountService accountService;

    private String getSuffix(String fileName) {
        int suffixIndex = fileName.lastIndexOf(".");
        if (suffixIndex == -1) return "";
        return fileName.substring(suffixIndex);
    }

    @Override
    public String uploadAvatar(MultipartFile avatar, int userId) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String suffix = getSuffix(avatar.getOriginalFilename());
        String avatarPathName = UPLOAD_AVATAR_NAME + uuid + suffix;
        try (InputStream inputStream = avatar.getInputStream()) {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(BUCKET)
                    .stream(inputStream, avatar.getSize(), -1)
                    .object(avatarPathName)
                    .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            log.error("上传出现错误, userId= {}", userId);
            return "上传出现错误，请稍后重试！";
        }
        boolean update = accountService.update()
                .set("avatar", avatarPathName)
                .eq("id", userId).update();
        if (update) log.info("用户 {} 头像更新成功！URL：{}，Size：{}", userId, avatarPathName, avatar.getSize());
        else return "更新头像记录错误，请联系管理员";
        return avatarPathName;
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
