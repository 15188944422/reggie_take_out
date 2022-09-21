package com.song.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.song.dto.SetmealDto;
import com.song.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {



    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐,同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 修改套餐 先展示给页面
     * @param id
     */
    SetmealDto getSetmealWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);

}
