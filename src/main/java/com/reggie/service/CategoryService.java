package com.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.domain.Category;

public interface CategoryService {
    boolean save(Category category);
    Page getPage(int page,int pageSize);
}
