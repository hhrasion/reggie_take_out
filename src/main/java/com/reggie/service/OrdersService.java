package com.reggie.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.Orders;
import org.apache.ibatis.annotations.Mapper;


public interface OrdersService extends IService<Orders> {
    Boolean submit(Orders orders);

    Page getPage(Integer page, Integer pageSize);
}
