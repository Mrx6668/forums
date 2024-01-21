package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.dto.AccountDetails;
import com.example.backend.entity.dto.AccountPrivacy;
import com.example.backend.entity.vo.request.DetailsSaveVO;
import com.example.backend.entity.vo.request.EmailModifyVO;
import com.example.backend.entity.vo.request.PrivacySaveVO;
import com.example.backend.entity.vo.request.PwdChangeVO;
import com.example.backend.entity.vo.respones.AccountDetailsVO;
import com.example.backend.entity.vo.respones.AccountVO;
import com.example.backend.service.AccountDetailsService;
import com.example.backend.service.AccountPrivacyService;
import com.example.backend.service.AccountService;
import com.example.backend.utils.BeanUtils;
import com.example.backend.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService accountService;
    @Resource
    AccountDetailsService accountDetailsService;
    @Resource
    AccountPrivacyService accountPrivacyService;

    @GetMapping("/info")
    public RestBean<AccountVO> getInfo(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        Account account = accountService.findAccountById(userId);
        AccountVO VO = BeanUtils.copyBeans(account, AccountVO.class);
//        AccountVO accountVO = new AccountVO();
//        BeanUtils.copyProperties(account,accountVO);
//        return RestBean.success(accountVO);
        return RestBean.success(VO);
    }

    @GetMapping("/details")
    public RestBean<AccountDetailsVO> getDetailsInfo(@RequestAttribute(Const.ATTR_USER_ID) int userId) {
        AccountDetails accountDetails = Optional.ofNullable(accountDetailsService.findAccountDetailsById(userId))
                .orElseGet(AccountDetails::new);
        return RestBean.success(accountDetails.asViewObject(AccountDetailsVO.class));
    }

    @PostMapping(value = "/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(Const.ATTR_USER_ID) int userId, @RequestBody @Valid DetailsSaveVO vo) {
        return accountDetailsService.saveAccountDetails(userId, vo) ? RestBean.success() : RestBean.failure(400, "用户名已占用，请更换！");
    }

    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                      @RequestBody @Validated EmailModifyVO vo) {
        String result = accountService.modifyEmail(userId, vo);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                         @RequestBody @Valid PwdChangeVO vo) {
        boolean result = accountService.changePassword(userId,vo);
        return result ? RestBean.success() : RestBean.failure(400,"重置密码失败！请检查原始密码是否正确");
    }

    @PostMapping("/save-privacies")
    public RestBean<Void> savePrivacies(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                      @RequestBody @Valid PrivacySaveVO vo){
        boolean result = accountPrivacyService.savePrivacies(userId,vo);
        return result ? RestBean.success() : RestBean.failure(400,"隐私状态修改失败，请联系管理员");
    }

    @GetMapping("/get-privacies")
    public RestBean<AccountPrivacy> getPrivacies(@RequestAttribute(Const.ATTR_USER_ID) int userId){
        AccountPrivacy accountPrivacy = accountPrivacyService.getPrivacies(userId);
        return RestBean.success(accountPrivacy);
    }



}
