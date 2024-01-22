package com.example.backend.service;

import com.example.backend.entity.vo.respones.WeatherVO;

public interface WeatherService {
    WeatherVO fetchWeather(double longitude,double latitude);
}
