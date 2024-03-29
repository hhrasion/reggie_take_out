package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.R;
import com.reggie.domain.ShoppingCart;
import com.reggie.mapper.ShoppingCartMapper;
import com.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public ShoppingCart addShoppingCart(ShoppingCart shoppingCart) {

        // 查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());

        if (dishId != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = super.getOne(lambdaQueryWrapper);

        // 如果存在，就在原来数量基础上加一
        if (Objects.nonNull(one)) {
            one.setNumber(one.getNumber()+1);
            boolean b = super.updateById(one);
        }else {
            // 如果不存在，则添加到购物车，数量默认为一
            shoppingCart.setNumber(1);
            super.save(shoppingCart);
            one = shoppingCart;
        }

        return one;
    }

    @Override
    public ShoppingCart subShoppingCart(ShoppingCart shoppingCart) {

        // 查询当前菜品或者套餐在购物车中的数量是否大于1
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());

        if (dishId != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = super.getOne(lambdaQueryWrapper);

        if (one == null) {
            return null;
        }

        if (one.getNumber() > 1){
            one.setNumber(one.getNumber() - 1);
            boolean update = super.updateById(one);
            return one;
        }else {
            boolean remove = super.remove(lambdaQueryWrapper);
        }

        return null;
    }

    @Override
    public List<ShoppingCart> getShoppingCartList(Long userId) {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        return super.list(lambdaQueryWrapper);
    }

    @Override
    public Boolean cleanShoppingCart(Long userId) {

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);

        return super.remove(lambdaQueryWrapper);
    }
}
