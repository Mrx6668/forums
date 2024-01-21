package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_account")
@AllArgsConstructor
public class Account {
    @TableId(type = IdType.AUTO)
    Integer id;
    @TableField("username")
    String username;
    String password;
    String email;
    String role;
    String avatar;
    Date registerTime;
}
