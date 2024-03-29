package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomerException;
import com.reggie.domain.Category;
import com.reggie.domain.Dish;
import com.reggie.domain.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService1;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl1 extends ServiceImpl<CategoryMapper, Category> implements CategoryService1 {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public Page getPage(int page, int pageSize) {
        Page<Category> page1 = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        page(page1, lambdaQueryWrapper);
        return page1;
    }

    @Override
    public Boolean updateCategory(Category category) {
        return updateById(category);
    }

    @Override
    public Boolean deleteCategoryById(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        //查询当前分类是否绑定菜品或套餐
        int count1 = dishService.count(dishLambdaQueryWrapper);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0 || count2 > 0) {
            //已经关联菜品或套餐，抛出一个业务异常
            throw new CustomerException("当前分类下已关联内容");
        }
        //正常删除
        return super.removeById(id);
    }

    @Override
    public List<Category> getList(Category category) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = super.list(lambdaQueryWrapper);
        return categoryList;
    }


}
