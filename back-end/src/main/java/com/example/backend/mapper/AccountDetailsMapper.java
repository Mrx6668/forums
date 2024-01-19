package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.dto.AccountDetails;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountDetailsMapper extends BaseMapper<AccountDetails> {
}
