package com.example.backend.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.backend.entity.RestBean;
import com.example.backend.entity.vo.request.PostCreateVO;
import com.example.backend.service.AiService;
import com.example.backend.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiGenerateController {
    @Resource
    AiService aiService;
    @PostMapping("/title")
    public RestBean<String> aiTitle(@RequestBody String content,
                                    @RequestAttribute(Const.ATTR_USER_ID) int userId){
        String result = aiService.aiTitle(content,userId);
        return result == null ?
                RestBean.failure(400,"生成失败，请稍后再试或者手动输入！") :
                RestBean.success(result);
    }
}
