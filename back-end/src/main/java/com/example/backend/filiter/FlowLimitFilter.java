package com.example.backend.filiter;

import com.example.backend.entity.RestBean;
import com.example.backend.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.ORDER_CORS+1)
public class FlowLimitFilter extends HttpFilter {
    @Resource
    StringRedisTemplate template;
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String address = request.getRemoteAddr();
        if(tryCount(address)){
            chain.doFilter(request,response);
        }else {
            writeBlockMessage((response));
        }


    }
    private boolean tryCount(String ip){
        synchronized (ip.intern()){
            if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK + ip))){
                //如果查询到Redis封禁列表中包含该ip
                return false;
            }
            return this.limitPeriodCheck(ip);
        }
    }

    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/jsom;charset=utf-8");
        response.getWriter().write(RestBean.failure(403,"操作频繁，请稍后重试！").asJsonString());
    }

    private boolean limitPeriodCheck(String ip){
        if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_COUNTER + ip))) {
            //检查是否存在该ip的Redis计数器
            Long increment = Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip)).orElse(0L);
            //如果存在，则自增
            if (increment > 10){
                //如果数值》10，则丢进封禁列表
                template.opsForValue().set(Const.FLOW_LIMIT_BLOCK+ip,"",30,TimeUnit.SECONDS);
                return false;
            }

        }else {
            //新建计数器，值为1，存在时间3秒
            template.opsForValue().set(Const.FLOW_LIMIT_COUNTER+ip,"1",3, TimeUnit.SECONDS);
        }
        return true;
    }
}
