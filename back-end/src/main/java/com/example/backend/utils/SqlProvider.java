package com.example.backend.utils;

import java.util.Map;

public class SqlProvider {
    public String updateViews(Map<Integer, Long> map) {
        StringBuilder sql = new StringBuilder("UPDATE db_post SET views = CASE id ");
        for (Map.Entry<Integer, Long> entry : map.entrySet()) {
            sql.append("WHEN ").append(entry.getKey()).append(" THEN ").append(entry.getValue()).append(" ");
        }
        sql.append("ELSE views END WHERE id IN (");
        for (Integer id : map.keySet()) {
            sql.append(id).append(",");
        }
        // 删除最后一个逗号
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        return sql.toString();
    }
}

