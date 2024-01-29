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
import com.example.backend.entity.vo.respones.PostUpdateVO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    StringRedisTemplate redisTemplate;

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
    public String updatePost(int userId, PostUpdateVO vo) {
        if (!contentLimitCheck(vo.getContent())) return "文章字数过多，请调整后再试！";
        if (!typeSet.contains(vo.getType())) return "对应分类非法，请不要恶意请求！";
        postMapper.update(null, Wrappers.<Post>update()
                .eq("uid", userId)
                .eq("id", vo.getId())
                .set("title", vo.getTitle())
                .set("content", vo.getContent().toString())
                .set("post_type", vo.getType())
        );
        return null;
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
        vo.setLike(postMapper.interactCount(post.getId(), "like"));
        vo.setCollect(postMapper.interactCount(post.getId(), "collect"));
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
    public PostDetailVO getPostDetail(int pid, int userId) {
        PostDetailVO vo = new PostDetailVO();
        Post post = this.getById(pid);
        if (post == null) return null;
        BeanUtils.copyProperties(post, vo);
        PostDetailVO.User user = new PostDetailVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, post.getUid()));
        PostDetailVO.Interact interact = new PostDetailVO.Interact(
                getInteract(pid, userId, "like"),
                getInteract(pid, userId, "collect")
        );
        vo.setInteract(interact);
        vo.setViews(getViewsCount(pid));
        return vo;
    }

    private long getViewsCount(int pid) {
        String key = Const.FORUM_POST_VIEW_COUNT;
        if (Boolean.FALSE.equals(redisTemplate.opsForHash().hasKey(key, String.valueOf(pid)))) {
            long views = this.getById(pid).getViews();
            redisTemplate.opsForHash().put(key, String.valueOf(pid), String.valueOf(views));
//            redisTemplate.opsForValue().set(key, String.valueOf(views));
        }
        Long increment = redisTemplate.opsForHash().increment(key, String.valueOf(pid), 1);
        this.saveViewsSchedule();
        return increment;
    }

    private Boolean getInteract(int pid, int uid, String type) {
        String key = pid + ":" + uid;
        Object state = redisTemplate.opsForHash().get(type, key);
        Boolean cacheState = state == null ? null : Boolean.parseBoolean((String) state);
        if (cacheState != null) return cacheState;
        return postMapper.userInteractCount(pid, uid, type) > 0;
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

    private void saveInteract(String type) {
        synchronized (type.intern()) {
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            redisTemplate.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString()))
                    check.add(Interact.parseInteract(k.toString(), type));
                else
                    uncheck.add(Interact.parseInteract(k.toString(), type));
            });
            if (!check.isEmpty()) {
                log.info("check不为空，执行插入数据库！");
                postMapper.addInsert(check, type);
            }
            if (!uncheck.isEmpty()) {
                log.info("uncheck不为空，执行删除数据库！");
                postMapper.deleteInsert(uncheck, type);
            }
            log.info("redis删除缓存执行！");
            redisTemplate.delete(type);
        }
    }

    @Override
    public String interact(Interact interact, boolean state) {
        String type = interact.getType();
        synchronized (type.intern()) {
            //先放到redis作缓存
            redisTemplate.opsForHash().put(type, interact.toKey(), Boolean.toString(state));
            this.saveInteractSchedule(type);
        }
        return null;
    }

    private final Map<String, Boolean> state = new HashMap<>();
    ScheduledExecutorService interactService = Executors.newScheduledThreadPool(2);
    ScheduledExecutorService viewService = Executors.newScheduledThreadPool(1);

    private void saveInteractSchedule(String type) {
        if (!state.getOrDefault(type, false)) {
            log.info("定时任务执行！");
            state.put(type, true);
            interactService.schedule(() -> {
                try {
                    log.info("saveInteract执行！");
                    this.saveInteract(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                state.put(type, false);
            }, 5, TimeUnit.SECONDS);
        } else {
            log.info("定时任务没有执行！");
        }
    }

    private void saveViewsSchedule() {
        if (!state.getOrDefault("views", false)) {
            state.put("views", true);
            log.info("保存Views定时任务执行！");
            viewService.schedule(() -> {
                try {
                    this.saveViews();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                state.put("views", false);
            }, 15, TimeUnit.SECONDS);
        }
    }

    private void saveViews() {
        // map中 key：id ， value 更新的值
        Map<Integer, Long> map = new HashMap<>();
        redisTemplate.opsForHash().entries(Const.FORUM_POST_VIEW_COUNT).forEach((k, v) -> {
            map.put(Integer.valueOf(k.toString()), Long.valueOf(v.toString()));
            redisTemplate.opsForHash().delete(Const.FORUM_POST_VIEW_COUNT, k);
        });
        Long updated = postMapper.updateViews(map);
        log.info("saveViews 更新了 {} 条记录", updated);
//        redisTemplate.delete(Const.FORUM_POST_VIEW_COUNT);
    }

    @Override
    public List<PostPreviewVO> listPostCollects(int userId) {
        return postMapper.listPostCollects(userId)
                .stream()
                .map(post -> {
                    PostPreviewVO vo = new PostPreviewVO();
                    BeanUtils.copyProperties(post, vo);
                    return vo;
                }).toList();
    }

    @Override
    public void removeCollect(int userId, int pid) {
        postMapper.removeCollect(userId, pid);
    }
}
