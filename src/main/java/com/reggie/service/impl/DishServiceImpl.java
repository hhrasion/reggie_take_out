package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.DishException;
import com.reggie.domain.Category;
import com.reggie.domain.Dish;
import com.reggie.domain.DishFlavor;
import com.reggie.dto.DishDto;
import com.reggie.mapper.CategoryMapper;
import com.reggie.mapper.DishFlavorMapper;
import com.reggie.mapper.DishMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.CategoryService1;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public Boolean saveDish(Dish dish) {
        return super.save(dish);
    }

    @Override
    public Dish getByName(String name) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getName, name);

        return super.getOne(dishLambdaQueryWrapper);
    }

    @Override
    public Page getPage(Integer page, Integer pageSize, String name) {

        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(!Objects.isNull(name), Dish::getName, name);

        super.page(pageInfo, dishLambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();

        // 方法1
        /*List<Long> categoryIdList = records.stream().map((item) -> {
            Long categoryId = item.getCategoryId();
            return categoryId;
        }).collect(Collectors.toList());

        List<Category> categories = categoryMapper.selectBatchIds(categoryIdList);

        Map<Long, String> categoriesMap = categories.stream().collect(Collectors.toMap(Category::getId, Category::getName));

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();

            dishDto.setCategoryName(categoriesMap.get(categoryId));

            return dishDto;
        }).collect(Collectors.toList());*/

        // 方法2
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryMapper.selectById(categoryId);
            String categoryName = category.getName();

            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return dishDtoPage;
//        return null;
    }


    @Override
    public DishDto getDishDtoById(Long id) {

        DishDto dishDto = new DishDto();

        System.out.println("start");

        Dish dish = super.getById(id);


        BeanUtils.copyProperties(dish, dishDto);

        Long categoryId = dishDto.getCategoryId();
        Category category = categoryMapper.selectById(categoryId);

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

        dishDto.setFlavors(dishFlavorList);

        String categoryName = category.getName();
        dishDto.setCategoryName(categoryName);

        return dishDto;
    }

    @Override
    @Transactional
    public Boolean updateDish1(DishDto dishDto) {

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDto, dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        try {
            boolean update = super.updateById(dish);
            boolean delete = dishFlavorService.remove(lambdaQueryWrapper);
            boolean saveBatch = dishFlavorService.saveBatch(flavors);
            return (update && delete && saveBatch);
        } catch (Exception e) {
            throw new DishException("修改失败");
        }

    }

    @Override
    public Boolean updateDish2(DishDto dishDto) {

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDto, dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().peek((item) -> item.setDishId(dishId)).collect(Collectors.toList());

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        LambdaUpdateWrapper<DishFlavor> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(DishFlavor::getIsDeleted,1).eq(DishFlavor::getDishId,dishDto.getId());

        try {
            boolean update1 = super.updateById(dish);
            boolean update2 = dishFlavorService.update(null, lambdaUpdateWrapper);
            boolean b = dishFlavorService.saveOrUpdateBatch(flavors);
            return (update1 && update2 && b);
        } catch (Exception e) {
            throw new DishException("修改失败");
        }

    }

    @Override
    @Transactional
    public Boolean updateStatus(List<Long> ids,Integer status) {
//        LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ids.stream().map((item) -> {
            LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(Dish::getStatus, status).eq(Dish::getId, item);
            boolean update = super.update(lambdaUpdateWrapper);
            if (!update) {
                throw new DishException("更改状态失败");
            }
            return true;
        }).collect(Collectors.toList());
        return true;
    }

    @Override
    public List<DishDto> getDishList(Long categoryId) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(!Objects.isNull(categoryId),Dish::getCategoryId,categoryId);
        lambdaQueryWrapper.eq(Dish::getStatus,1);

        List<Dish> dishList = super.list(lambdaQueryWrapper);

        /*List<Long> dishIdList = dishList.stream().map(Dish::getId).collect(Collectors.toList());

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,dishIdList);
        List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);*/


        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            return dishDto;
        }).collect(Collectors.toList());

//        List<DishDto> dishDtoList = new ArrayList<>();

//        BeanUtils.copyProperties(dishList,dishDtoList);

        System.out.println(dishDtoList);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishDtoList = dishDtoList.stream().map((item) -> {
            Long DishId = item.getId();
            dishFlavorLambdaQueryWrapper.clear();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,DishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            item.setFlavors(dishFlavors);
            return item;
        }).collect(Collectors.toList());

        return dishDtoList;
    }


}
