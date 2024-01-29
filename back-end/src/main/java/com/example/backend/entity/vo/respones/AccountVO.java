package com.example.backend.entity.vo.respones;

import lombok.Data;

import java.util.Date;

@Data
public class AccountVO {
    int id;
    String username;
    String email;
    String role;
    String avatar;
    Date registerTime;
}
