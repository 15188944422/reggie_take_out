package com.song.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.song.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}
