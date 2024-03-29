package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.DishException;
import com.reggie.common.SetmealException;
import com.reggie.domain.Category;
import com.reggie.domain.Setmeal;
import com.reggie.domain.SetmealDish;
import com.reggie.dto.SetmealDto;
import com.reggie.mapper.CategoryMapper;
import com.reggie.mapper.SetmealMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.CategoryService1;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Boolean saveSetmeal(SetmealDto setmealDto) {
        boolean save = super.save(setmealDto);
        Long setmealDtoId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDtoId);
            return item;
        }).collect(Collectors.toList());
        boolean b = setmealDishService.saveBatch(setmealDishes);
        if (!save || !b){
            throw new SetmealException("新增套餐失败");
        }
        return true;
    }

    @Override
    public Page getPage(int page, int pageSize, String name) {

        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page,pageSize);

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Objects.nonNull(name),Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        super.page(setmealPage,lambdaQueryWrapper);

        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();

        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);

            setmealDto.setCategoryName(category.getName());

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(collect);

        return setmealDtoPage;
    }

    @Override
    @Transactional
    public Boolean updateStatus(List<Long> ids, Integer status) {

        ids.forEach((item) -> {
            LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(Setmeal::getStatus,status).eq(Setmeal::getId,item);
            boolean update = super.update(lambdaUpdateWrapper);
            if (!update) {
                throw new SetmealException("更改状态失败");
            }
        });
        return true;
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public Boolean deleteSetmeal(List<Long> ids) {

        // 判断套餐是否启售中
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Setmeal::getStatus,1);
        lambdaQueryWrapper1.in(Setmeal::getId,ids);

        int count = this.count(lambdaQueryWrapper1);
        if (count > 0){
            throw new SetmealException("套餐启售中，不可删除");
        }

        // 删除套餐 setmeal
        boolean b1 = super.removeByIds(ids);
        if (!b1){
            throw new SetmealException("删除失败");
        }

        // 删除套餐与菜品关系信息 setmeal_dish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.in(SetmealDish::getSetmealId,ids);
        boolean b2 = setmealDishService.remove(lambdaQueryWrapper2);
        if (!b2){
            throw new SetmealException("删除失败");
        }

        return true;
    }


    /**
     * 逻辑套餐 逻辑删除
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public Boolean deleteSetmeal1(List<Long> ids) {

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        lambdaQueryWrapper.in(Setmeal::getId,ids);

        int count = this.count(lambdaQueryWrapper);

        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper1.set(Setmeal::getIsDeleted, 1).in(Setmeal::getId, ids);
        boolean update1 = super.update(lambdaUpdateWrapper1);

        if (!update1) {
            throw new SetmealException("删除失败");
        }

        LambdaUpdateWrapper<SetmealDish> lambdaUpdateWrapper2 = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper2.set(SetmealDish::getIsDeleted,1).in(SetmealDish::getSetmealId,ids);
        boolean update2 = setmealDishService.update(lambdaUpdateWrapper2);

        if (!update2) {
            throw new SetmealException("删除失败");
        }

        return true;
    }

    @Override
    public List<SetmealDto> getSetmealList(Long categoryId, Integer status) {

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Objects.nonNull(categoryId),Setmeal::getCategoryId,categoryId);
        setmealLambdaQueryWrapper.eq(Objects.nonNull(status),Setmeal::getStatus,status);

        List<Setmeal> setmealList = super.list(setmealLambdaQueryWrapper);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<SetmealDto> setmealDtoList = setmealList.stream().map((item) -> {
            Long setmealId = item.getId();

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            setmealDishLambdaQueryWrapper.clear();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealId);
            List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

            setmealDto.setSetmealDishes(setmealDishes);

            return setmealDto;
        }).collect(Collectors.toList());

        return setmealDtoList;
    }

    @Override
    public List<SetmealDish> getSetMealDishDetails(Long setmealId) {
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealId);

        return setmealDishService.list(lambdaQueryWrapper);
    }
}
