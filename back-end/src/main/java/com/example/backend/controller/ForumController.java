package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.vo.respones.WeatherVO;
import com.example.backend.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;

    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ?
                RestBean.failure(400, "获取地理位置与天气信息出错，请联系管理员！")
                : RestBean.success(vo);
    }
}
