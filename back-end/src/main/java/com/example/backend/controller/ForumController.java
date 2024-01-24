package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.PostDTO;
import com.example.backend.entity.vo.request.PostCreateVO;
import com.example.backend.entity.vo.respones.PostPreviewVO;
import com.example.backend.entity.vo.respones.WeatherVO;
import com.example.backend.service.ForumService;
import com.example.backend.service.WeatherService;
import com.example.backend.utils.Const;
import com.example.backend.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/forum")
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
        List<PostPreviewVO> previewVOS = forumService.listPost(page, type);
        return previewVOS.isEmpty()
                ? RestBean.failure(400, "获取帖子列表失败，请刷新重试")
                : RestBean.success(previewVOS);
    }
}
