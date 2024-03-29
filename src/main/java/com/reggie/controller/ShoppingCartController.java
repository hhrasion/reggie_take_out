package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.domain.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(HttpSession session, @RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart);

        // 设置用户id，指定当前是哪个用户的购物车
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        shoppingCart.setCreateTime(LocalDateTime.now().toString());

        ShoppingCart addShoppingCart = shoppingCartService.addShoppingCart(shoppingCart);

        if (addShoppingCart == null) {
            return R.error("删除失败");
        }

        return R.success(addShoppingCart);
    }
    @PostMapping("/sub")
    public R<ShoppingCart> sub(HttpSession session, @RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart);

        // 设置用户id，指定当前是哪个用户的购物车
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        shoppingCart.setCreateTime(LocalDateTime.now().toString());

        ShoppingCart addShoppingCart = shoppingCartService.subShoppingCart(shoppingCart);

        return R.success(addShoppingCart);
    }

    /**
     * 根据用户id查询购物车数据
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartList(){
        log.info("查看购物车...");
        // 设置用户id，指定当前是哪个用户的购物车
        Long currentId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartService.getShoppingCartList(currentId);

        return R.success(shoppingCartList);
    }

    /**
     * 根据用户id清空购物车数据
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart(){

        Long currentId = BaseContext.getCurrentId();
        Boolean aBoolean = shoppingCartService.cleanShoppingCart(currentId);

        if (aBoolean){
            return R.success("清空成功");
        }

        return R.error("清空失败");
    }

}
