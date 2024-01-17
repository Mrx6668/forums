package com.example.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    @Value("${spring.jwt.secret}")
    private String key;
    @Value("${spring.jwt.expire}")
    private int expire;
    @Autowired
    StringRedisTemplate redisTemplate;
    public String creatJwt(UserDetails details, int id, String username){
        // 使用HMAC256算法和密钥创建JWT
        Algorithm algorithm = Algorithm.HMAC256(key);
        Date expire = this.expireTime(); // 获取过期时间
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id) // 添加id声明
                .withClaim("username",username) // 添加用户名声明
                .withClaim("authorities",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) // 添加权限声明
                .withExpiresAt(expire) // 设置过期时间
                .withIssuedAt(new Date()) // 设置发行时间
                .sign(algorithm); // 使用算法签名JWT
    }

    public DecodedJWT resolveJwt(String HeaderToken){
        String token = convertToken(HeaderToken); // 转换令牌
        if (token == null){
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(key); // 使用HMAC256算法和密钥验证JWT
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = jwtVerifier.verify(token); // 验证令牌
            if(isTokenExpired(verify.getId())) return null;// 如果令牌被拉入黑名单，则返回null
            Date expiresAt = verify.getExpiresAt(); // 获取过期时间
            return new Date().after(expiresAt) ? null : verify; // 如果令牌已过期，返回null，否则返回解码的JWT
        }catch (JWTVerificationException exception){
            return null; // 如果验证失败，返回null
        }
    }

    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims(); // 获取声明
        return User
                .withUsername(claims.get("username").asString()) // 设置用户名
                .password("*******") // 设置密码（此处为占位符）
                .authorities(claims.get("authorities").asArray(String.class)) // 设置权限
                .build(); // 构建UserDetails对象
    }

    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24); // Calendar.HOUR在这里的作用就是告诉add方法你想要改变的是“小时”这个字段。而expire*24则告诉add方法你想要将这个字段增加多少。
        return calendar.getTime(); // 返回过期时间
    }

    public Integer toId(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims(); // 获取声明
        return claims.get("id").asInt(); // 返回id声明的值（转换为整数）
    }

    private String convertToken(String HeaderToken){
        if (HeaderToken == null || !HeaderToken.startsWith("Bearer ")){
            return null; // 如果令牌为空或不以"Bearer "开头，返回null
        }
        return HeaderToken.substring(7); // 返回去掉"Bearer "前缀的令牌
    }

    public boolean invalidateJwt(String headerToken){
        String token = convertToken(headerToken); // 转换令牌
        if (token == null){
            return false;
        }
        Algorithm algorithm = Algorithm.HMAC256(key); // 使用HMAC256算法和密钥验证JWT
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id, jwt.getExpiresAt()); // 删除令牌
        }catch (JWTVerificationException exception){
            return false; // 如果验证失败，返回false
        }

    }

    private boolean deleteToken(String uuid, Date time){
        if(isTokenExpired(uuid)){
            return false;
        }
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(),0);
        redisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }
    private boolean isTokenExpired(String uuid){
        return Boolean.TRUE.equals(redisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid));
    }

}
