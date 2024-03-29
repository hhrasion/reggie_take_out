package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.Dish;
import com.reggie.domain.DishFlavor;
import com.reggie.dto.DishDto;

import java.util.List;

public interface DishFlavorService extends IService<DishFlavor> {
    Boolean saveDish(Long dishId, List<DishFlavor> dishFlavorList);
    Boolean save1(List<DishFlavor> dishFlavorList);
}
