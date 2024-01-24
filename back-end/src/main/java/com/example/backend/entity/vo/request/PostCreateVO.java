package com.example.backend.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PostCreateVO {
    int type;
    @Length(min = 1,max = 50)
    String title;
    JSONObject content;
}
