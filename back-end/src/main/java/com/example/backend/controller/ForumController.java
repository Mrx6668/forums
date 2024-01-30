package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.Interact;
import com.example.backend.entity.dto.PostDTO;
import com.example.backend.entity.vo.request.AddCommentVO;
import com.example.backend.entity.vo.request.PostCreateVO;
import com.example.backend.entity.vo.respones.*;
import com.example.backend.service.ForumService;
import com.example.backend.service.WeatherService;
import com.example.backend.utils.Const;
import com.example.backend.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;
    @Resource
    ForumService forumService;

    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ?
                RestBean.failure(400, "获取地理位置与天气信息出错，请联系管理员！")
                : RestBean.success(vo);
    }

    @GetMapping("/types")
    public RestBean<List<PostDTO>> getPostTypes() {
        List<PostDTO> postTypes = forumService.getPostTypes();
        return RestBean.success(postTypes);
    }

    @PostMapping("/create-post")
    public RestBean<Void> CreatePost(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                     @Valid @RequestBody PostCreateVO vo) {
        return ControllerUtils.messageHandle(() -> forumService.createPost(userId, vo));
    }

    @GetMapping("/list-post")
    public RestBean<List<PostPreviewVO>> listPost(@RequestParam @Min(0) int page,
                                                  @RequestParam @Min(0) int type) {
        List<PostPreviewVO> previewVOS = forumService.listPost(page + 1, type);
        if (previewVOS == null) return RestBean.failure(400,"没有数据了");
        return previewVOS.isEmpty()
                ? RestBean.failure(400, "获取帖子列表失败，请刷新重试")
                : RestBean.success(previewVOS);
    }

    @GetMapping("/top-post")
    public RestBean<List<TopPostVO>> getTopPost() {
        List<TopPostVO> topPost = forumService.getTopPost();
        return RestBean.success(topPost);
    }

    @GetMapping("/post")
    public RestBean<PostDetailVO> getPostDetail(@RequestParam @Min(0) int pid,
                                                @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        return RestBean.success(forumService.getPostDetail(pid, userId));
    }

    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int pid,
                                   @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                   @RequestParam boolean state,
                                   @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        forumService.interact(new Interact(pid, userId, new Date(), type), state);
        return RestBean.success();
    }

    @GetMapping("/collects")
    public RestBean<List<PostPreviewVO>> collects(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        return RestBean.success(forumService.listPostCollects(userId));
    }

    @PostMapping("/remove-collect/{pid}")
    public RestBean<Void> removeCollect(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                        @PathVariable int pid) {
        forumService.removeCollect(userId, pid);
        return RestBean.success();
    }

    @PostMapping("/update-post")
    public RestBean<Void> updateTopic(@RequestBody @Valid PostUpdateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        return ControllerUtils.messageHandle(() -> forumService.updatePost(userId, vo));
    }

    @PostMapping("/add-comment")
    public RestBean<Void> addComment(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                     @Valid @RequestBody AddCommentVO vo) {
        return ControllerUtils.messageHandle(() -> forumService.addComment(userId, vo));
    }

    @GetMapping("/comments")
    public RestBean<List<CommentVO>> comments(@RequestParam @Min(0) int pid,
                                              @RequestParam @Min(0) int page) {
        return RestBean.success(forumService.comments(pid, page + 1));
    }

    @GetMapping("/delete-comment")
    public RestBean<Void> deleteComment(@RequestParam @Min(0) int id,
                                        @RequestAttribute(Const.ATTR_USER_ID) int userId) {

        return forumService.deleteComment(id, userId) > 0 ? RestBean.success() : RestBean.failure(400, "删除失败！");
    }
}
