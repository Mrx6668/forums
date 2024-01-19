package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.utils.BaseData;
import lombok.Data;

@Data
@TableName("db_account_privacy")
public class AccountPrivacy implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    boolean phone = true;
    boolean email = true;
    boolean wx = true;
    boolean qq = true;
    boolean gender = true;
}
