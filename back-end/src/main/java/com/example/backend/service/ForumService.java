package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.dto.Post;
import com.example.backend.entity.dto.PostDTO;
import com.example.backend.entity.vo.request.PostCreateVO;
import com.example.backend.entity.vo.respones.PostPreviewVO;

import java.util.List;

public interface ForumService  extends IService<Post> {
    List<PostDTO> getPostTypes();
    String createPost(int userId, PostCreateVO vo);
    List<PostPreviewVO> listPost(int page,int type);
}
