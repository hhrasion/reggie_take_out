package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.DishException;
import com.reggie.common.R;
import com.reggie.domain.Dish;
import com.reggie.dto.DishDto;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    List<Long> ids = new ArrayList<>();

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R<String> saveDish(@RequestBody DishDto dishDto) {
        /*Long id1 = dishDto.getId();
        Boolean aBoolean1 = dishService.saveDish(dishDto);
        Long id2 = dishDto.getId();
        Dish dish = dishService.getByName(dishDto.getName());

        Long dishId = dish.getId();
        Boolean aBoolean2 = dishFlavorService.saveDish(dishId, dishDto.getFlavors());
        if (aBoolean1 && aBoolean2) {
            return R.success("新增成功");
        }*/
        try {
            Boolean aBoolean1 = dishService.saveDish(dishDto);
            Long dishId = dishDto.getId();
            Boolean aBoolean2 = dishFlavorService.saveDish(dishId, dishDto.getFlavors());
            if (aBoolean1 && aBoolean2) {
                return R.success("新增成功");
            }
        } catch (Exception e) {
            throw new DishException("新增失败");
        }
        return R.error("新增失败");

    }

    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        Page page1 = dishService.getPage(page, pageSize, name);
        return R.success(page1);
    }

    /**
     * 根据id获取菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getDishDtoById(id);
        return R.success(dishDto);
    }

    /**
     * 更新菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        Boolean aBoolean = dishService.updateDish1(dishDto);
        if (aBoolean){
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    /**
     * 停售菜品
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> stop(@RequestParam List<Long> ids){
        Boolean aBoolean = dishService.updateStatus(ids, 0);
        System.out.println(aBoolean);
        if (aBoolean){
            return R.success("停售成功");
        }
        return R.error("停售失败");
    }

    /**
     * 启售菜品
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> start(@PathVariable Integer status,@RequestParam List<Long> ids){
        Boolean aBoolean = dishService.updateStatus(ids, status);
        if (aBoolean){
            return R.success("启售成功");
        }
        return R.error("启售失败");
    }

    /**
     * 根据分类Id查询菜品
     * @param dish
     * @return
     */
    /*@GetMapping("list")
    public R<List<Dish>> getDishList(Dish dish){
        List<Dish> dishList = dishService.getDishByCategoryId(dish.getCategoryId());
        return R.success(dishList);
    }*/
    /**
     * 根据分类Id查询菜品
     * @param dish
     * @return
     */
    @GetMapping("list")
    public R<List<DishDto>> getDishList1(Dish dish){
        List<DishDto> dishDtoList = dishService.getDishList(dish.getCategoryId());
        return R.success(dishDtoList);
    }
}
