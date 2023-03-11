package com;

import com.mybatis.MapperProxyFactory;
import com.sifan.User;
import com.sifan.UserMapper;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        UserMapper userMapper = MapperProxyFactory.getMapper(UserMapper.class);
        List<User> userList = userMapper.getUserList("张三", "123456");
        System.out.println(userList);
        User userById = userMapper.getUserById(3);
        System.out.println(userById);
    }
}
