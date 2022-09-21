package com.song.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
