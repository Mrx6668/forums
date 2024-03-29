package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.dto.Interact;
import com.example.backend.entity.dto.Post;
import com.example.backend.entity.dto.PostDTO;
import com.example.backend.entity.vo.request.AddCommentVO;
import com.example.backend.entity.vo.request.PostCreateVO;
import com.example.backend.entity.vo.respones.*;

import java.util.List;

public interface ForumService extends IService<Post> {
    List<PostDTO> getPostTypes();

    String createPost(int userId, PostCreateVO vo);

    List<PostPreviewVO> listPost(int page, int type);

    List<TopPostVO> getTopPost();

    PostDetailVO getPostDetail(int pid, int userId);

    String interact(Interact interact, boolean state);

    List<PostPreviewVO> listPostCollects(int userId);

    void removeCollect(int userId, int pid);

    String updatePost(int userId, PostUpdateVO vo);

    String addComment(int userId, AddCommentVO vo);
    List<CommentVO> comments(int pid,int page);

    int deleteComment(int id, int userId);
}
