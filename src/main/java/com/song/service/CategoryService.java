package com.song.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.song.pojo.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
