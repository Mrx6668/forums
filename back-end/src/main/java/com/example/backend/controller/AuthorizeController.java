package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.vo.request.ConfirmResetVO;
import com.example.backend.entity.vo.request.EmailRegisterVO;
import com.example.backend.entity.vo.request.EmailResetVO;
import com.example.backend.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    @Resource
    private AccountService accountService;

    @GetMapping("ask-code")
    public RestBean<Void> askCode(@RequestParam @Email String email,
                                  @RequestParam @Pattern(regexp = "register|reset") String type,
                                  HttpServletRequest request) {
//        String message = accountService.registerEmailVerifyCode(type,email,request.getRemoteAddr());
//        if (message == null)
//            return RestBean.success();
//        else
//            return RestBean.failure(400,message);
        return this.messageHandle(() ->
                accountService.registerEmailVerifyCode(type, email, request.getRemoteAddr()));
    }

    @PostMapping("register")
    public RestBean<Void> register(@RequestBody @Validated EmailRegisterVO vo) {
        return messageHandle(() ->
                accountService.registerEmailAccount(vo));
    }

    @PostMapping("/reset")
    public RestBean<Void> reset(@RequestBody @Validated ConfirmResetVO vo) {
        return messageHandle(() ->
                accountService.resetConfirm(vo)
        );
    }

    @PostMapping("/reset-confirm")
    public RestBean<Void> ResetConfirm(@RequestBody @Validated EmailResetVO vo){
        return messageHandle(() ->
                accountService.resetEmailAccountPassword(vo)
        );
    }

    private RestBean<Void> messageHandle(Supplier<String> action) {
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }
}
