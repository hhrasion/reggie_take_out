package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.domain.Setmeal;
import com.reggie.domain.SetmealDish;
import com.reggie.dto.SetmealDto;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        Boolean aBoolean = setmealService.saveSetmeal(setmealDto);
        System.out.println(aBoolean);
//        boolean b = setmealDishService.saveBatch(setmealDto.getSetmealDishes());
        if (aBoolean){
            return R.success("添加成功");
        }
        return R.error("添加失败");
    }

    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        Page page1 = setmealService.getPage(page, pageSize, name);

        return R.success(page1);
    }

    /**
     * 根据条件查询套餐信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<SetmealDto>> getSetmealList(Setmeal setmeal){

        List<SetmealDto> setmealList = setmealService.getSetmealList(setmeal.getCategoryId(), setmeal.getStatus());

        return R.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> getSetMealDishDetails(@PathVariable Long id){

        List<SetmealDish> setMealDishDetails = setmealService.getSetMealDishDetails(id);

        return R.success(setMealDishDetails);
    }


    /**
     * 更改套餐销售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> start(@PathVariable Integer status,@RequestParam List<Long> ids){
        Boolean aBoolean = setmealService.updateStatus(ids, status);
        if (aBoolean){
            return R.success("更新状态成功");
        }
        return R.error("更新状态失败");
    }

    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){
        Boolean aBoolean = setmealService.deleteSetmeal(ids);
        if (aBoolean){
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

}
