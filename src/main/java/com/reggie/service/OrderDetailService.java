package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.domain.OrderDetail;
import com.reggie.domain.Orders;
import com.reggie.mapper.OrderDetailMapper;

public interface OrderDetailService extends IService<OrderDetail> {
    Boolean saveOrders(Orders orders);
}
