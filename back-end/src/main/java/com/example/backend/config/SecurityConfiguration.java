package com.example.backend.config;

import com.alibaba.fastjson2.JSONObject;
import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.vo.request.AuthorizeVO;
import com.example.backend.filiter.JwtAuthorizeFilter;
import com.example.backend.service.AccountService;
import com.example.backend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {
    @Resource
    JwtUtils jwtUtils;
    @Resource
    JwtAuthorizeFilter filter;
    @Resource
    AccountService accountService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.
                // 授权HTTP请求
                    authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                        authorizationManagerRequestMatcherRegistry
                        // 对于路径为"/api/auth/**"的请求，允许所有人访问
                        .requestMatchers("/api/auth/**","/error").permitAll()
                        // 对于其他所有请求，需要进行身份验证
                        .anyRequest().authenticated();
                })
                // 配置表单登录
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    // 设置登录处理的URL
                    httpSecurityFormLoginConfigurer.loginProcessingUrl("/api/auth/login");
                    // 设置身份验证成功时的处理器
                    httpSecurityFormLoginConfigurer.successHandler(this::onAuthenticationSuccess);
                    // 设置身份验证失败时的处理器
                    httpSecurityFormLoginConfigurer.failureHandler(this::onAuthenticationFailure);
                })
                // 配置注销
                .logout(httpSecurityLogoutConfigurer -> {
                    // 设置注销URL
                    httpSecurityLogoutConfigurer.logoutUrl("/api/auth/logout");
                    // 设置注销成功时的处理器
                    httpSecurityLogoutConfigurer.logoutSuccessHandler(this::onLogoutSuccess);
                })
                //  自定义异常处理
                .exceptionHandling(conf ->{
                    conf.authenticationEntryPoint(this::onUnAuthorized);
                    conf.accessDeniedHandler(this::onAccessDeny);
                })

                // 禁用CSRF（跨站请求伪造）保护
                .csrf(AbstractHttpConfigurer::disable)
                // 配置会话管理
                .sessionManagement(httpSecuritySessionManagementConfigurer -> {
                    // 设置会话创建策略为无状态，即不创建会话
                    httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                // 在UsernamePasswordAuthenticationFilter之前添加自定义过滤器
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                // 构建HttpSecurity对象
                .build();

    }



    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8"); // 设置响应类型
        UserDetails user = (UserDetails) authentication.getPrincipal(); // 获取用户详情
        Account account = accountService.findAccountByIdOrEmail(user.getUsername());
        String token = jwtUtils.creatJwt(user,account.getId(),account.getUsername()); // 创建JWT令牌
        AuthorizeVO authorizeVO = new AuthorizeVO();
        BeanUtils.copyProperties(account, authorizeVO);
        authorizeVO.setExpire(jwtUtils.expireTime()); // 设置令牌过期时间
        authorizeVO.setToken(token); // 设置令牌
//        authorizeVO.setRole(account.getRole()); // 设置角色

//        authorizeVO.setUsername(user.getUsername()); // 设置用户名
        log.info("登录成功:" + authentication.getName()); // 记录登录成功日志
        // 将授权视图对象转换为JSON字符串，并写入响应
        response.getWriter().write(RestBean.success(authorizeVO).asJsonString());
//        response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
    }
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        log.info("登录失败:" + exception.getMessage());
        response.getWriter().write(RestBean.failure(401,"您的登录信息有误。").asJsonString());
    }
    public void onUnAuthorized(HttpServletRequest request, HttpServletResponse  response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(401,exception.getMessage()).asJsonString());
    }
    private void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(403,e.getMessage()).asJsonString());
    }
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        //黑名单模式
        String auth = request.getHeader("Authorization");
        if(jwtUtils.invalidateJwt(auth)){
            log.info("退出成功！");
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("退出成功")));
        }else {
            log.info("退出失败！");
            response.getWriter().write(JSONObject.toJSONString(RestBean.failure(400,"退出失败")));
        }

    }
}
