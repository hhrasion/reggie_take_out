package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.Category;

import java.util.List;

public interface CategoryService1 extends IService<Category> {
    Page getPage(int page, int pageSize);

    Boolean updateCategory(Category category);

    Boolean deleteCategoryById(Long id);

    List<Category> getList(Category category);
}
