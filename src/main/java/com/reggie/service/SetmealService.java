package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.Setmeal;
import com.reggie.domain.SetmealDish;
import com.reggie.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    Boolean saveSetmeal(SetmealDto setmealDto);

    Page getPage(int page, int pageSize, String name);

    Boolean updateStatus(List<Long> ids, Integer status);

    Boolean deleteSetmeal(List<Long> ids);
    Boolean deleteSetmeal1(List<Long> ids);

    List<SetmealDto> getSetmealList(Long categoryId, Integer status);

    List<SetmealDish> getSetMealDishDetails(Long setmealId);
}
