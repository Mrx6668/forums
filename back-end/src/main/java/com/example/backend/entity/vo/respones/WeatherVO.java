package com.example.backend.entity.vo.respones;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class WeatherVO {
    JSONObject location;
    JSONObject now;
    JSONArray hourly;
}
