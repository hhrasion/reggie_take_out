package com.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.domain.Category;
import com.reggie.domain.Employee;
import com.reggie.service.CategoryService;
import com.reggie.service.CategoryService1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryService1 categoryService1;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){
        log.info("新增菜品分类：{}",category.toString());
        boolean save = categoryService.save(category);
//        boolean save = categoryService1.save(category);
        if (save){
            return R.success("新增成功");
        }
        return R.error("新增失败");
    }

    /**
     * 分类分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize){
        Page page1 = categoryService1.getPage(page, pageSize);
        return R.success(page1);
    }

    /**
     * 根据id进行删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteCategory(Long ids){
        Boolean aBoolean = categoryService1.deleteCategoryById(ids);
        if (aBoolean){
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    /**
     * 根据id进行分类修改
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){
        Boolean aBoolean = categoryService1.updateCategory(category);
        if (aBoolean){
            return R.success("更新成功");
        }
        return R.error("更新失败");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> listCategory(Category category){
        List<Category> categoryList = categoryService1.getList(category);
        return R.success(categoryList);

    }
}
