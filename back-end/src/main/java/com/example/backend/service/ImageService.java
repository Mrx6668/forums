package com.example.backend.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadAvatar(MultipartFile avatar, int userId);

    void getImage(HttpServletResponse response, String imageName);
}
