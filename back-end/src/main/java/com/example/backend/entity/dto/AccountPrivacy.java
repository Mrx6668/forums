package com.example.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.backend.utils.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("db_account_privacy")
@NoArgsConstructor
public class AccountPrivacy implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    boolean phone = true;
    boolean email = true;
    boolean wx = true;
    boolean qq = true;
    boolean gender = true;

    public String[] hiddenFields() {
        List<String> strings = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.getType().equals(boolean.class) && !field.getBoolean(this)) {
                    strings.add(field.getName());
                }
            } catch (Exception e) {

            }
        }
        return strings.toArray(String[]::new);
    }

    public AccountPrivacy(Integer id) {
        this.id = id;
    }
}
