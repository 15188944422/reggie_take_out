package com.song.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.song.mapper.ShoppingCartMapper;
import com.song.pojo.ShoppingCart;
import com.song.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
