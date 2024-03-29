package com.reggie;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.domain.Employee;
import com.reggie.dto.DishDto;
import com.reggie.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Test1 {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CategoryService1 categoryService1;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Test
    void TestGet(){
//        Employee all = employeeService.getAll();
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getId,1L);
        Employee one = employeeService.getOne(lambdaQueryWrapper);
        System.out.println(1);
    }
    @Test
    void TestCategoryGetPage(){
        Page page = categoryService1.getPage(1, 10);
        System.out.println(page);
    }
    @Test
    void TestGetDishDtoById(){
        DishDto dishDto = dishService.getDishDtoById(1771199968121921537L);
        System.out.println(dishDto);
    }
    @Test
    void testsetmealDishService(){

//        setmealDishService.saveBatch();
    }
}
