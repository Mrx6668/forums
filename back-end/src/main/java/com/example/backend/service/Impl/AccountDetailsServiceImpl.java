package com.example.backend.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.dto.AccountDetails;
import com.example.backend.entity.vo.request.DetailsSaveVO;
import com.example.backend.entity.vo.request.EmailModifyVO;
import com.example.backend.mapper.AccountDetailsMapper;
import com.example.backend.service.AccountDetailsService;
import com.example.backend.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {

    @Resource
    AccountService accountService;

    @Override
    public AccountDetails findAccountDetailsById(int id) {
        return this.getById(id);
    }

    @Override
    public boolean saveAccountDetails(int id, DetailsSaveVO vo) {
        Account account = accountService.lambdaQuery()
                .eq(Account::getUsername, vo.getUsername())
                //todo
                .one();
        if (account == null || account.getId() == id) {
            boolean updateAccount = accountService.lambdaUpdate()
                    .eq(Account::getId, id)
                    .set(Account::getUsername, vo.getUsername()).update();
            boolean saveOrUpdateAccountDetails = this.saveOrUpdate(
                    new AccountDetails(id, vo.getGender(), vo.getPhone(), vo.getQq(), vo.getWx(), vo.getDesc())
            );
            return updateAccount && saveOrUpdateAccountDetails;
        }
        return false;
    }


}
