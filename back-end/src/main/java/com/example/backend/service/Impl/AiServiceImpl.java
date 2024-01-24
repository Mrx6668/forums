package com.example.backend.service.Impl;

import com.example.backend.service.AiService;
import com.example.backend.utils.Const;
import com.example.backend.utils.FlowUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.client.AiClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiServiceImpl implements AiService {
    @Resource
    AiClient aiClient;
    @Resource
    FlowUtils flowUtils;
    private static final String aiTitlePromote = "请你现在充当一个标题生成器，用于生成社区论坛帖子的标题，请根据以下内容生成一个合理、吸引人的标题：";
    @Override
    public String aiTitle(String content,int userId) {
        if (!flowUtils.limitPeriodCounterCheck(Const.AI_TITLE_GENERATE_LIMIT,5,60))
            return null;
        if (content.length() < 5 || content.length() > 5000) return null;
        log.info("aiTitle："+content);
        return aiClient.generate(aiTitlePromote + content);
    }
}
