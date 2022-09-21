package com.song.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.song.common.R;
import com.song.dto.DishDto;
import com.song.pojo.Category;
import com.song.pojo.Dish;
import com.song.pojo.DishFlavor;
import com.song.service.CategoryService;
import com.song.service.DishFlavorService;
import com.song.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     *
     * 分析:为什么要写Page<DishDto> dishDtoPage=new Page<>(page,pageSize);
     *      如果 只写
     *          Page<Dish> pageInfo=new Page<>(page,pageSize); 这个,"菜品分类" 还是一个id
     *          private Long categoryId;
     *      返回给页面的时候 返回给categoryId 无法展示给页面,
     *      所以在DishDto 中添加一个属性 categoryName 根据id查询菜品分类 封装到 DishDto中
     *
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo=new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage=new Page<>(page,pageSize);
        //构造 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();

        //添加过滤添加
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝 忽视records  我们想要的 List<DishDto> 但是 pageInfo 是List<Dish>
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        //将 records 这个集合 中 拷贝到 dishDtoList中去
        List<DishDto> dishDtoList= records.stream().map((item) ->{ // item 是每一个 dish
            // 这里只赋值categoryName 其他属性 为空 所以要拷贝到其他的属性
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());//转换成集合

        dishDtoPage.setRecords(dishDtoList);


        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto flavor = dishService.getByIdWithFlavor(id);
        return R.success(flavor);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     *  数组转换成字符串 Arrays.toString(Object[] objs)
     *
     *  数组转换成集合
     *  1.遍历的方式，依次添加到集合中。
     *  2.Arrays.asList()
     *  3.List.of()方法
     *  4.Collections.addAll(集合，数组)方法，将集合存储到数组中
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        //Arrays.toString(Object[] obj);返回指定数组内容的字符串表示形式。
        log.info(Arrays.toString(ids));
        /*
        for(Long id:ids){
            System.out.println(id);
        }
        */
        //dishService.removeBatchByIds(Arrays.asList(ids));
        //dishService.removeBatchWithFlavor(ids);


        //先删除 菜品口味表
        //构建条件器
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        //boolean remove = dishFlavorService.removeByIds(Arrays.asList(ids));
        List<DishFlavor> dishFlavor = dishFlavorService.list(queryWrapper);
        boolean remove = dishFlavorService.removeBatchByIds(dishFlavor);
        if (!remove) {
            return R.error("删除失败");
        }
        //后删除菜品表
        boolean batch = dishService.removeBatchByIds(Arrays.asList(ids));
        if (!batch) {
            return R.error("删除失败");
        }
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> statusUpdate(@PathVariable int status,Long[] ids){
        log.info(Arrays.toString(ids));
        log.info("status:{}",status);
        /*LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);*/
        List<Dish> dishList=new ArrayList<>();
        Dish dish=new Dish();
        for(Long id:ids){
            dish.setId(id);
            dish.setStatus(status);
            dishList.add(dish);
        }
        //boolean b = dishService.updateBatchByIds(dishList);
        boolean b = dishService.updateBatchById(dishList);

        if (!b){
            if (status==1){
                if (ids.length>1){
                    return R.error("批量启售失败");
                }else {
                    return R.error("启售失败");
                }
            }else {
                if (ids.length>1){
                    return R.error("批量停售失败");
                }else {
                    return R.error("停售失败");
                }
            }
        }
        if (status==1){
            if (ids.length>1){
                return R.success("批量启售成功");
            }else {
                return R.success("启售成功");
            }
        }else {
            if (ids.length>1){
                return R.success("批量停售成功");
            }else {
                return R.success("停售成功");
            }
        }
    }


    /**
     * 解释：
     *  R<List<Dish>> 查询菜品,可能一个菜品有多个菜,所以采用list集合的形式
     *  list(Dish dish)  这里可以采用 Long categoryId 但是采用实体类的形式 更有通用性
     *
     *  根据条件来查询菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        //查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();

        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        //添加条件,查询状态为1(起售状态)的菜品
        queryWrapper.eq(Dish::getStatus,1);


        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        List<DishDto> dishDtoList= dishList.stream().map((item) ->{ // item 是每一个 dish
            // 这里只赋值categoryName 其他属性 为空 所以要拷贝到其他的属性
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();


            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();

            queryWrapper1.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;


        }).collect(Collectors.toList());//转换成集合

        return R.success(dishDtoList);
    }
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){

        //查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();

        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        //添加条件,查询状态为1(起售状态)的菜品
        queryWrapper.eq(Dish::getStatus,1);


        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        return R.success(dishList);
    }
*/
}
