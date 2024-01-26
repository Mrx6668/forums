package com.example.backend.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.*;
import com.example.backend.entity.vo.request.PostCreateVO;
import com.example.backend.entity.vo.respones.PostDetailVO;
import com.example.backend.entity.vo.respones.PostPreviewVO;
import com.example.backend.entity.vo.respones.TopPostVO;
import com.example.backend.mapper.*;
import com.example.backend.service.ForumService;
import com.example.backend.utils.CacheUtils;
import com.example.backend.utils.Const;
import com.example.backend.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ForumServiceImpl extends ServiceImpl<PostMapper, Post> implements ForumService {
    @Resource
    ForumMapper forumMapper;
    @Resource
    PostMapper postMapper;
    @Resource
    AccountMapper accountMapper;
    @Resource
    AccountDetailsMapper accountDetailsMapper;
    @Resource
    AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    FlowUtils flowUtils;
    @Resource
    CacheUtils cacheUtils;

    @PostConstruct
    private void initTypeSet() {
        typeSet = this.getPostTypes().stream().map(PostDTO::getId).collect(Collectors.toSet());
        log.info("成功获取typeSet：{} ", typeSet);
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
        String key = Const.FORUM_POST_CREATE_LIMIT + userId;
        if (!flowUtils.limitPeriodCounterCheck(key, 3, 1800)) return "发文过于频繁，请稍后再试！";
        Post post = Post.builder()
                .uid(userId)
                .title(vo.getTitle())
                .content(vo.getContent().toJSONString())
                .postType(vo.getType())
                .build();

//        Post post = new Post(null, userId, vo.getTitle(), vo.getContent().toJSONString(), vo.getType(), null);
        boolean save = this.save(post);
        cacheUtils.deleteCache(Const.FORUM_POST_PREVIEW_CACHE + "*");
        return save ? null : "保存发生错误，请重试或联系管理员";
    }

    @Override
    public List<PostPreviewVO> listPost(int pageNum, int type) {
        String key = Const.FORUM_POST_PREVIEW_CACHE + pageNum + ":" + type;
//        List<Post> posts;
        //从缓存中取
        List<PostPreviewVO> previewVOS = cacheUtils.takeListFromCache(key, PostPreviewVO.class);
        if (previewVOS != null) return previewVOS;
        Page<Post> page = Page.of(pageNum, 10);
//        if (page.getSize() == 0) return null;
        LambdaQueryWrapper<Post> wrapper = Wrappers.<Post>lambdaQuery()
                .eq(type != 0, Post::getPostType, type)
                .orderByDesc(Post::getCreateTime);
        Page<Post> postPage = postMapper.selectPage(page, wrapper);
//        //分类别 如果传入0 则不不限类别从数据库查询
//        if (type == 0)
//            postMapper.selectPage(page, wrapper);
//            posts = postMapper.postList(pageNum * 10);
//        else
//            posts = postMapper.postListByType(pageNum * 10, type);
        List<Post> posts = postPage.getRecords();
        if (posts.isEmpty()) return null;
        previewVOS = posts.stream().map(this::resolvePostToPostView).toList();
        cacheUtils.saveListToCache(key, previewVOS, 30);
        return previewVOS;
    }

    //解析帖子中content内容 分离image 减少预览的字数300字
    private PostPreviewVO resolvePostToPostView(Post post) {
        PostPreviewVO vo = new PostPreviewVO();
        BeanUtils.copyProperties(post, vo);
        Account account = accountMapper.selectById(post.getUid());
        BeanUtils.copyProperties(account, vo, "id");
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(post.getContent()).getJSONArray("ops");
        for (Object op : ops) {
            Object insert = ((JSONObject) op).get("insert");
            if (insert instanceof String text) {
                if (previewText.length() >= 300) continue;
                previewText.append(insert);
            } else if (insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image"))
                        .ifPresent(obj -> images.add(obj.toString()));
            }
        }
        vo.setContent(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }

    @Override
    public List<TopPostVO> getTopPost() {
        return postMapper.getTopPost();
    }

    @Override
    public PostDetailVO getPostDetail(int pid) {
        PostDetailVO vo = new PostDetailVO();
        Post post = this.getById(pid);
        if (post == null) return null;
        BeanUtils.copyProperties(post, vo);
        PostDetailVO.User user = new PostDetailVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, post.getUid()));
        return vo;
    }

    private <T> T fillUserDetailsByPrivacy(T target, int uid) {
        AccountDetails accountDetails = accountDetailsMapper.selectById(uid);
        Account account = accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy = accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields();
        BeanUtils.copyProperties(account, target, ignores);
        BeanUtils.copyProperties(accountDetails, target, ignores);
        return target;
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
