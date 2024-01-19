package com.example.backend.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.vo.request.*;
import com.example.backend.mapper.AccountMapper;
import com.example.backend.service.AccountService;
import com.example.backend.utils.Const;
import com.example.backend.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FlowUtils flowUtils;
    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    /*
     * loadUserByUsername
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByIdOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("!用户名或密码错误!");
        }
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account findAccountByIdOrEmail(String text) {
        return this.query()
                .eq("username", text)
                .or()
                .eq("email", text)
                .one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {
            if (!verifyLimit(ip)) {
//                return RestBean.failure(601,"请求过于频繁,请稍后再试!").asJsonString();
                return "请求过于频繁,请稍后再试!";
            }


            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("mail", data);

            stringRedisTemplate.opsForValue().set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);

            return null;
        }

    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String username = vo.getUsername();
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if (code == null) return "请先获取验证码";
        if (!vo.getCode().equals(code)) return "验证码错误，请重新输入";
        if (existAccountByEmail(email)) return "邮箱已被注册，请更换邮箱";
        if (existAccountByUsername(username)) return "用户名已被注册，请更换用户名";
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null, username, password, email, "user", new Date());
        if (this.save(account)) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            return null;
        } else {
            return "发生错误！请联系管理员";
        }
    }

    @Override
    public String resetConfirm(ConfirmResetVO resetVO) {
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + resetVO.getEmail());
        if (code == null) {
            return "请先获取验证码";
        }
        if (!code.equals(resetVO.getCode())) {
            return "验证码错误，请重新输入";
        }
        return null;

    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO resetVO) {
        String email = resetVO.getEmail();
        String verify = this.resetConfirm(new ConfirmResetVO(email, resetVO.getCode()));
        if (verify != null)
            return verify;
        String password = passwordEncoder.encode(resetVO.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if (update) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            return null;
        } else {
            return "更新了个寂寞";
        }

    }

    private boolean existAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }

    private boolean existAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }

    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key, 60);
    }

    @Override
    public Account findAccountById(int userId) {

        return this.getById(userId);
    }

    @Override
    public String modifyEmail(int id, EmailModifyVO vo) {
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + vo.getEmail());
        if (code == null) return "请先获取验证码";
        if (!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + vo.getEmail());
        //查找电子邮件是否被占用
        Account account = this.findAccountByIdOrEmail(vo.getEmail());
        if (account != null) {
            if (account.getId() == id) return "您已经绑定此邮箱！";
            return "此邮箱已绑定账号，请更换后再试！";
        }
        this.lambdaUpdate()
                .set(Account::getEmail, vo.getEmail())
                .eq(Account::getId, id)
                .update();
        return null;
    }

    @Override
    public boolean changePassword(int userId, PwdChangeVO vo) {
        Account account = this.getById(userId);
        String oldPwd = account.getPassword();
        if (!passwordEncoder.matches(vo.getOldPassword(), oldPwd)) return false;
        return this.lambdaUpdate()
                .set(Account::getPassword,passwordEncoder.encode(vo.getNewPassword()))
                .eq(Account::getId,userId)
                .update();
    }


}
