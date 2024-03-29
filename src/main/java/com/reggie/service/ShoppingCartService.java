package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart addShoppingCart(ShoppingCart shoppingCart);

    ShoppingCart subShoppingCart(ShoppingCart shoppingCart);

    List<ShoppingCart> getShoppingCartList(Long userId);

    Boolean cleanShoppingCart(Long userId);

}
