package com.song.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.common.CustomException;
import com.song.mapper.CategoryMapper;
import com.song.pojo.Category;
import com.song.pojo.Dish;
import com.song.pojo.Setmeal;
import com.song.service.CategoryService;
import com.song.service.DishService;
import com.song.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * category 是菜品和套餐的分类
 * dish 是菜品表
 * setmeal 是套餐表
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类,在删除之前需要判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品,如果已经关联,则抛出业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件,根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        long count1 = dishService.count(dishLambdaQueryWrapper);

        if (count1>0) {//大于0已经关联了
            //已经关联菜品,则抛出业务异常
            throw new CustomException("当前分类下,关联了菜品,不可以删除");
        }
        //查询当前分类是否关联了套餐,如果已经关联,则抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件,根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2>0) {//大于0已经关联了
            //已经关联套餐,则抛出业务异常
            throw new CustomException("当前分类下,关联了套餐,不可以删除");

        }
        //正常删除分类
        super.removeById(id);
    }
}
