package com.example.backend.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PwdChangeVO {
    @Length(min = 6,max = 20)
    String oldPassword;
    @Length(min = 6,max = 20)
    String newPassword;
}
