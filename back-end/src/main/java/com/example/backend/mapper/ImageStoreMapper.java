package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.dto.StoreImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageStoreMapper extends BaseMapper<StoreImage> {
}
