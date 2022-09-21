package com.song.dto;

import com.song.pojo.Dish;
import com.song.pojo.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DishDto 继承饿Dish 所以继承过来 Dish的属性
 * 页面在向后台传参的时候 会封装到一起
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
