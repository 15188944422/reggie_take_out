package com.song.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
