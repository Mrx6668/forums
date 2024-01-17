package com.example.backend.filiter;

import com.example.backend.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
解决跨域
 */
@Component
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(request,response);
        chain.doFilter(request,response);
    }
    public void addCorsHeader(HttpServletRequest request, HttpServletResponse response){
//        response.addHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));//获取发起请求的原始站点运行跨域
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:5173");//允许指定站点跨域
        response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        response.addHeader("Access-Control-Allow-Headers","Authorization,Content-Type");
    }
}
