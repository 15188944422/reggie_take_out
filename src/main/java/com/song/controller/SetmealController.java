package com.song.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.song.common.R;
import com.song.dto.SetmealDto;
import com.song.pojo.Category;
import com.song.pojo.Setmeal;
import com.song.service.CategoryService;
import com.song.service.SetmealDishService;
import com.song.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     *
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息,{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }


    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        //
        Page<SetmealDto> dtoPage=new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件,根据name 进行like模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);

        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //对象的拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");//records 代表记录,也就是分页的每条数据,因为泛型不一样 所以不用拷贝
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> setmealDtoList=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);

            if (byId!=null){
                //分类名称
                String byIdName = byId.getName();
                setmealDto.setCategoryName(byIdName);

            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(setmealDtoList);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }


    @GetMapping("/{id}")
    @Cacheable(value = "setmealCache" ,key="#id")
    public R<SetmealDto> show(@PathVariable Long id){
        log.info("id:{}",id);
        SetmealDto setmealWithDish = setmealService.getSetmealWithDish(id);
        return R.success(setmealWithDish);
    }

    @PutMapping
    public R<String> edit(@RequestBody SetmealDto setmealDto){
        log.info("接收到的参数为:{}",setmealDto);
        log.info(setmealDto.getName());
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    @PostMapping("/status/{status}")
    public R<String> stopSelling(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("status:{}",status);
        log.info("ids:{}",ids);

        List<Setmeal> setmeals=new ArrayList<>();
        for (Long id : ids) {
            Setmeal setmeal=new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            setmeals.add(setmeal);
        }
        setmealService.updateBatchById(setmeals);
        return R.success("操作成功");
    }


}
