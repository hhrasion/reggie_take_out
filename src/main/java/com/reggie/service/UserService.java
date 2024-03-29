package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.User;

public interface UserService extends IService<User> {
    User getUser(String email);

    Boolean newUser(User user);
}
