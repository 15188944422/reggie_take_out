package com.song.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.common.CustomException;
import com.song.dto.SetmealDto;
import com.song.mapper.SetmealMapper;
import com.song.pojo.Setmeal;
import com.song.pojo.SetmealDish;
import com.song.service.SetmealDishService;
import com.song.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     *
     *  前端传输的数据(json json格式的要用@RequsetBody注解去接收)
     *
     *      {
     *          下面对应的实体类为Setmeal(id是自动生成的)
     *          "name":"营养超值工作餐",
     *          "categoryId":"1399923597874081794",
     *          "price":3800,
     *          "code":"",
     *          "image":"9cd7a80a-da54-4f46-bf33-af3576514cec.jpg",
     *          "description":"营养超值工作餐",
     *          "dishList":[],
     *          "status":1,
     *          "idType":"1399923597874081794", 这个就是套餐id
     *
     *           这里的对应的是 setmeal_dish表 没有 setmealId字段 我们要手动去赋值
     *          "setmealDishes":[
     *             {"copies":2,"dishId":"1423329009705463809","name":"米饭","price":200},
     *             {"copies":1,"dishId":"1423328152549109762","name":"可乐","price":500},
     *             {"copies":1,"dishId":"1397853890262118402","name":"鱼香肉丝","price":3800}
     *          ]
     *      }
     *
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息 操作的是setmeal表 执行insert操作,多出来的两个属性    忽略
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());


        //保存套餐和菜品的关联信息,操作的是setmeal_dish 执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }



    /**
     * 删除套餐,同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐的状态,确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper();

        //多个id 采用in 关键字
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        long count = this.count(queryWrapper);

        if (count>0){
            //如果不能删除,抛出一个业务异常
            throw new CustomException("套餐正在售卖中,不可以删除");
        }

        //如果可以删除,先删除套餐表的数据---setmeal
        this.removeByIds(ids);

        //删除关系表的数据---setmeal_dish
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(dishLambdaQueryWrapper);
    }

    /**
     *  id为套餐的id
     * @param id
     * @return
     */
    @Override
    @Transactional
    public SetmealDto getSetmealWithDish(Long id) {
        SetmealDto setmealDto=new SetmealDto();
        //先 查询 套餐的基本信息--setmeal
        Setmeal setmeal = this.getById(id);

        //后查询 套餐菜品信息 --- setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);


        BeanUtils.copyProperties(setmeal,setmealDto);

        setmealDto.setSetmealDishes(setmealDishList);


        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //修改套餐表 setmeal
        boolean setmeal = this.updateById(setmealDto);

        if (!setmeal) {
            throw new CustomException("更新失败");
        }

        //修改 套餐菜品表 setmeal_dish
        //清理当前套餐菜品表
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((item)->{
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());
        boolean b = setmealDishService.saveBatch(setmealDishes);
        if (!b) {
            throw new CustomException("更新失败");
        }

    }
}
