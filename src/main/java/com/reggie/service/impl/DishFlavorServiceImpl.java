package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.domain.Dish;
import com.reggie.domain.DishFlavor;
import com.reggie.dto.DishDto;
import com.reggie.mapper.DishFlavorMapper;
import com.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
    @Override
    public Boolean saveDish(Long dishId, List<DishFlavor> dishFlavorList) {
        for (DishFlavor dishFlavor : dishFlavorList) {
            dishFlavor.setDishId(dishId);
        }
        return super.saveBatch(dishFlavorList);
    }

    @Override
    public Boolean save1(List<DishFlavor> dishFlavorList) {
        return super.saveBatch(dishFlavorList);
    }
}
