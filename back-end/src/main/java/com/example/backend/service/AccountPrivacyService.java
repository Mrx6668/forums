package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.dto.AccountPrivacy;
import com.example.backend.entity.vo.request.PrivacySaveVO;


public interface AccountPrivacyService extends IService<AccountPrivacy> {
    boolean savePrivacies(int userId, PrivacySaveVO vo);

    AccountPrivacy getPrivacies(int userId);
}
