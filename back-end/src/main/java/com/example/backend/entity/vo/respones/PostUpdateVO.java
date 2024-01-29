package com.example.backend.entity.vo.respones;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PostUpdateVO {
    @Min(0)
    int id;
    @Min(1) @Max(5)
    int type;
    @Length(min = 1, max = 50)
    String title;
    JSONObject content;
}
