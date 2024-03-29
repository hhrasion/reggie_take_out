package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.Dish;
import com.reggie.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    Boolean saveDish(Dish dish);

    Dish getByName(String name);

    Page getPage(Integer page, Integer pageSize, String name);

    DishDto getDishDtoById(Long id);

    Boolean updateDish1(DishDto dishDto);
    Boolean updateDish2(DishDto dishDto);

    Boolean updateStatus(List<Long> ids,Integer status);

    List<DishDto> getDishList(Long categoryId);
}
