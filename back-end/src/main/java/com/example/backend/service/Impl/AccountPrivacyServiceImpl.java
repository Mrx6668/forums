package com.example.backend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.AccountPrivacy;
import com.example.backend.entity.vo.request.PrivacySaveVO;
import com.example.backend.mapper.AccountPrivacyMapper;
import com.example.backend.service.AccountPrivacyService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {
    @Override
    public boolean savePrivacies(int userId, PrivacySaveVO vo) {
        vo.setId(userId);
        return this.saveOrUpdate(vo, new UpdateWrapper<AccountPrivacy>().eq("id", userId));
    }

    @Override
    public AccountPrivacy getPrivacies(int userId) {
//        return this.getById(userId);
        return Optional.ofNullable(this.getById(userId)).orElse(new AccountPrivacy());
    }

}
