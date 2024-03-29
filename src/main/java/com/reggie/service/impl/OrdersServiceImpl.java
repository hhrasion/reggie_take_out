package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.BaseContext;
import com.reggie.common.CustomerException;
import com.reggie.domain.AddressBook;
import com.reggie.domain.OrderDetail;
import com.reggie.domain.Orders;
import com.reggie.domain.ShoppingCart;
import com.reggie.mapper.OrdersMapper;
import com.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {


    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;

    @Override
    @Transactional
    public Boolean submit(Orders orders) {

        long orderId = IdWorker.getId(); // 订单号
        AtomicInteger totalAmount = new AtomicInteger(); // 订单总金额

        // 获取当前用户id
        Long userId = orders.getUserId();

        // 获取当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartList(userId);

        if (shoppingCartList == null) {
            throw new CustomerException("购物车为空，不能下单");
        }

        // 获取用户收货地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomerException("用户地址信息有误，不能下单");
        }

        // 设置订单详细数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) -> {
            // 计算订单总金额
            totalAmount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(item,orderDetail,"id");
            orderDetail.setOrderId(orderId);

            /*Long dishId = item.getDishId();
            if (dishId != null) {
                orderDetail.setDishId(dishId);
                orderDetail.setDishFlavor(item.getDishFlavor());
            }else {
                orderDetail.setSetmealId(item.getSetmealId());
            }
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());*/
            return orderDetail;
        }).collect(Collectors.toList());
        // 向订单数据表插入数据，多条数据


        boolean saveOrderDetailList = orderDetailService.saveBatch(orderDetailList);
        if (!saveOrderDetailList){
            throw new CustomerException("下单失败");
        }
        // 设置订单属性
        /*orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setAddress(addressBook.getDetail());
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        for (ShoppingCart shoppingCart : shoppingCartList) {
            BigDecimal amount = shoppingCart.getAmount();
            Integer number = shoppingCart.getNumber();
            amount = amount.multiply(BigDecimal.valueOf(number));
            totalAmount = totalAmount.add(amount);
        }*/
        orders.setAmount(new BigDecimal(totalAmount.get()));
        orders.setOrderTime(LocalDateTime.now().toString());
        orders.setCheckoutTime(LocalDateTime.now().toString());
        orders.setStatus(2);
//        orders.setUserName();
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                            + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                            + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                            + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        // 向订单表插入数据，一条数据
        boolean saveOrders = super.save(orders);
        if (!saveOrders){
            throw new CustomerException("下单失败");
        }

        // 清空购物车数据
        boolean cleanShoppingCart = shoppingCartService.cleanShoppingCart(userId);
        if (!cleanShoppingCart){
            throw new CustomerException("下单失败");
        }

        return true;
    }

    @Override
    public Page getPage(Integer page, Integer pageSize) {

        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getUserId,userId);

        super.page(ordersPage,lambdaQueryWrapper);
        return null;
    }
}
