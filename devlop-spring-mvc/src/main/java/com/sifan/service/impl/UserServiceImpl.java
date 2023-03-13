package com.sifan.service.impl;

import com.sifan.service.UserService;
import com.springmvc.annotation.Service;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Override
    public void findUser() {
        System.out.println("========================服务层被调用了==========================");
    }
}
