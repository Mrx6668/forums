package com.example.backend.utils;

import java.lang.reflect.Constructor;

public class BeanUtils {
    public static Object copyBeans(Object source, Class<?> cla) {
        try {
            Constructor<?> constructor = cla.getConstructor();
            Object newIns = constructor.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(source, newIns);
            return newIns;
        } catch (Exception e) {
            System.err.println("""
                    ----------------------
                    copyBeans 时 发生错误
                    ----------------------
                     """);
            throw new RuntimeException("copyBeans 时 发生错误");
        }
    }
}
