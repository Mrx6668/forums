package com.example.backend.utils;

import com.example.backend.entity.RestBean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

//@Component
public class ControllerUtils {
    public static <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if (message == null){
            return RestBean.success();
        }else {
            return RestBean.failure(400,message);
        }
    }
}
