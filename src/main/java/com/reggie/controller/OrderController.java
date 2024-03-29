package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.domain.Orders;
import com.reggie.domain.ShoppingCart;
import com.reggie.service.OrdersService;
import com.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();
        orders.setUserId(userId);

        Boolean submit = ordersService.submit(orders);

        /*// 获取当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartList(userId);
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        for (ShoppingCart shoppingCart : shoppingCartList) {
            BigDecimal amount = shoppingCart.getAmount();
            Integer number = shoppingCart.getNumber();
            amount = amount.multiply(BigDecimal.valueOf(number));
            totalAmount = totalAmount.add(amount);
        }


        // 向订单表插入数据，一条数据
        orders.setAmount(totalAmount);

        // 向订单数据表插入数据，多条数据


        // 清空购物车数据*/
        if (submit){
            return R.success("下单成功");
        }

        return R.error("下单失败");
    }

    @GetMapping("/userPage")
    public R<Page> page(Integer page, Integer pageSize){

        ordersService.getPage(page,pageSize);

        return R.error("查询失败");
    }
}
