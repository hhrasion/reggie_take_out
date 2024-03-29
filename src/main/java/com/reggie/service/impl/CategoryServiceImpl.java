package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.domain.Category;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public boolean save(Category category) {
        return categoryMapper.insert(category) > 0;
    }

    @Override
    public Page getPage(int page, int pageSize) {
//        categoryMapper.selectMapsPage()
        return null;
    }
}
