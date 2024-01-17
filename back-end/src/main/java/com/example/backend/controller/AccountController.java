package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.vo.respones.AccountVO;
import com.example.backend.service.AccountService;
import com.example.backend.utils.BeanUtils;
import com.example.backend.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService accountService;
    @GetMapping("/info")
    public RestBean<AccountVO> getInfo(@RequestAttribute(Const.ATTR_USER_ID) int userId){
        Account account = accountService.findAccountById(userId);
        Object VO = BeanUtils.copyBeans(account, AccountVO.class);
//        AccountVO accountVO = new AccountVO();
//        BeanUtils.copyProperties(account,accountVO);
//        return RestBean.success(accountVO);
        return RestBean.success((AccountVO) VO);
    }
}
