package com.example.backend.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    @Resource
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    String username;
    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data){
        String email = data.get("email").toString();
        Integer code = (Integer) data.get("code");
        String type = data.get("type").toString();
        SimpleMailMessage message = switch (type){
            case "register" -> createMessage("欢迎来到我们的网站","注册验证码:"+code+", 请在3分钟之内完成验证",email);
            case "reset" -> createMessage("网站密码重置","重置密码验证码:"+code+"， 您正在进行重置密码操作，请在3分钟之内完成验证，请确保是您本人操作",email);
            case "modify" -> createMessage("您正在绑定新的论坛邮箱","验证码:"+code+",有效时间为3分钟，请妥善保管。",email);
            default -> null;
        };
        if (message == null) return;
        mailSender.send(message);
    }

    private SimpleMailMessage createMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
