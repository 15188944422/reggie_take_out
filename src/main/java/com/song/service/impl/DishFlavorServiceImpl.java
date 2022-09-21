package com.song.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.mapper.DishFlavorMapper;
import com.song.pojo.DishFlavor;
import com.song.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
