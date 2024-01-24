package com.example.backend.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kotlin.time.TimeSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CacheUtils {
    @Autowired
    StringRedisTemplate redisTemplate;

    public <T> T takeFromCache(String key, Class<T> itemType){
        String s = redisTemplate.opsForValue().get(key);
        if (s == null) return null;
        return JSONObject.parseObject(s).to(itemType);
    }
    public <T> List<T> takeListFromCache(String key, Class<T> itemType){
        String s = redisTemplate.opsForValue().get(key);
        log.info("takeListFromCache" + s);
        if (s == null) return null;
//        return JSONArray.parseArray(s).toList(itemType);
        return JSON.parseArray(s, itemType);
    }
    public <T> void saveToCache(String key, T data, long expire) {
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(data),expire, TimeUnit.SECONDS);
    }
    public <T> void saveListToCache(String key, List<T> list, long expire) {
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list),expire, TimeUnit.SECONDS);
    }
//    public void deleteCache(String key){
//        redisTemplate.delete(key);
//    }
    public void deleteCache(String pattern){
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

}
