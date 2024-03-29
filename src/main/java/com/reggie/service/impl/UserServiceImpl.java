package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.domain.User;
import com.reggie.mapper.UserMapper;
import com.reggie.service.UserService;
import com.reggie.utils.ValidateCodeUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User getUser(String email) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail,email);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    public Boolean newUser(User user) {
        return super.save(user);
    }
}
