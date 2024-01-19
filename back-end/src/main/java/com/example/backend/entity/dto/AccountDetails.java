package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.utils.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("db_account_details")

public class AccountDetails implements BaseData {
    @TableId
    Integer id;
    int gender;
    String phone;
    String qq;
    String wx;
    @TableField("`desc`")
    String desc;
}
