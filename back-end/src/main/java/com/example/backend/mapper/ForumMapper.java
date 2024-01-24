package com.example.backend.mapper;

import com.example.backend.entity.dto.PostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@SuppressWarnings("SqlNoDataSourceInspection")
@Mapper
public interface ForumMapper {
    @Select("select * from `db_post_type`")
    List<PostDTO> getPostTypes();
}
