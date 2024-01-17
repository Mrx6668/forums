package com.example.backend.entity.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
//@AllArgsConstructor
public class AuthorizeVO {
    String username;
    String role;
    String token;
    Date expire;
}
