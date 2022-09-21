package com.song.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.song.mapper.OrderDetailMapper;
import com.song.pojo.OrderDetail;
import com.song.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
