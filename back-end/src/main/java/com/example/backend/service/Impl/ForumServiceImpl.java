package com.example.backend.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Post;
import com.example.backend.entity.dto.PostDTO;
import com.example.backend.entity.vo.request.PostCreateVO;
import com.example.backend.mapper.ForumMapper;
import com.example.backend.mapper.PostMapper;
import com.example.backend.service.ForumService;
import com.example.backend.utils.Const;
import com.example.backend.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ForumServiceImpl extends ServiceImpl<PostMapper, Post> implements ForumService {
    @Resource
    ForumMapper forumMapper;
    @Resource
    FlowUtils flowUtils;
    @PostConstruct
    private void initTypeSet(){
        typeSet = this.getPostTypes().stream().map(PostDTO::getId).collect(Collectors.toSet());
        log.info("成功获取typeSet：{} ",typeSet);
    }
    private Set<Integer> typeSet = null;
    @Override
    public List<PostDTO> getPostTypes() {
        return forumMapper.getPostTypes();
    }

    private boolean contentLimitCheck(JSONObject object) {
        if (object == null) return false;
        long length = 0;
        JSONArray ops = object.getJSONArray("ops");
        for (int i = 0; i < ops.size(); i++) {
            JSONObject op = ops.getJSONObject(i);
            if (op.containsKey("insert")) {
                String insert = op.getString("insert");
                length += insert.length();
                if (length > 20000) return false;
            }
        }
        return true;
    }

    @Override
    public String createPost(int userId, PostCreateVO vo) {
        if (!contentLimitCheck(vo.getContent())) return "文章字数过多，请调整后再试！";
        if (!typeSet.contains(vo.getType())) return "对应分类非法，请不要恶意请求！";
        String key = Const.FORUM_POST_CREATE_LIMIT;
        if (!flowUtils.limitPeriodCounterCheck(key, 3, 1800)) return "发文过于频繁，请稍后再试！";
        Post post = new Post(null,userId, vo.getTitle(),vo.getContent().toJSONString(), vo.getType(), null);
        boolean save = this.save(post);
        return save ? null : "保存发生错误，请重试或联系管理员";
    }
    //    private boolean contentLimitCheck(JSONObject object) {
//        if (object == null) return false;
//        long length = 0;
//        for (Object op : object.getJSONArray("ops")) {
//            length += JSONObject.parseObject((String) op).getString("insert").length();
//            if (length > 20000) return false;
//        }
//        return true;
//    }

}
