package com.example.backend.entity.vo.respones;

import lombok.Data;

import java.util.Date;

@Data
public class AccountVO {
    String username;
    String email;
    String role;
    Date registerTime;
}
