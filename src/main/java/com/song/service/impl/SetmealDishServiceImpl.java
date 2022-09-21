package com.song.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.mapper.SetmealDishMapper;
import com.song.pojo.SetmealDish;
import com.song.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper,SetmealDish> implements SetmealDishService {
}
