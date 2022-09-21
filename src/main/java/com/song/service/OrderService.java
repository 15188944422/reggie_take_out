package com.song.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.song.pojo.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
