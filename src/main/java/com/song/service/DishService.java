package com.song.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.song.dto.DishDto;
import com.song.pojo.Dish;

import java.util.List;

/**
 * 两个表的操作要 在service中去扩展
 */
public interface DishService extends IService<Dish> {

    //新增菜品同时,插入菜品对应的口味数据,需要操作两张表
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品信息,同时更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);

    //boolean updateBatchByIds(List<Dish> dishList);

    //void removeBatchWithFlavor(Long[] ids);
}
