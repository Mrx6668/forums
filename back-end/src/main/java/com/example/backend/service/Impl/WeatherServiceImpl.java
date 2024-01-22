package com.example.backend.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.backend.entity.vo.respones.WeatherVO;
import com.example.backend.service.WeatherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    @Resource
    RestTemplate restTemplate;
    @Value("${weather.key}")
    String weatherKey;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public WeatherVO fetchWeather(double longitude, double latitude) {
        return fetchFromCache(longitude,latitude);
    }

    private WeatherVO fetchFromCache(double longitude, double latitude) {
        log.info("fetchFromCache：longitude ：{}  ， latitude ： {} ",longitude,latitude);
        byte[] data = restTemplate.getForObject
                ("https://geoapi.qweather.com/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + weatherKey, byte[].class);
        JSONObject geo = decompressStringToJson(data);
        if (geo == null) return null;
        JSONObject location = geo.getJSONArray("location").getJSONObject(0);
        String id = location.getString("id");
        String key = "weather:" + id;
        String cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            return JSONObject.parseObject(cache).to(WeatherVO.class);
        }
        WeatherVO vo = this.fetchFromAPI(id, location);
        if (vo == null) return null;
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(vo), 1, TimeUnit.HOURS);
        return vo;
    }

    private WeatherVO fetchFromAPI(String id, JSONObject location) {
        WeatherVO vo = new WeatherVO();
        vo.setLocation(location);
        byte[] nowByte = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/now?location=" + id + "&key=" + weatherKey, byte[].class);
        JSONObject now = decompressStringToJson(nowByte);
        if (now == null) return null;
        vo.setNow(now.getJSONObject("now"));
        byte[] hourlyByte = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/24h?location=" + id + "&key=" + weatherKey, byte[].class);
        JSONObject hourly = decompressStringToJson(hourlyByte);
        if (hourly == null) return null;
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
        return vo;
    }

    private JSONObject decompressStringToJson(byte[] data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzipInStream = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = gzipInStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            gzipInStream.close();
            outputStream.close();
            return JSONObject.parseObject(outputStream.toString());
        } catch (IOException e) {
//            throw new RuntimeException(e);
            return null;
        }

    }
}
