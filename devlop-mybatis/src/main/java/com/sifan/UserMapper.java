package com.sifan;

import com.mybatis.Select;

import java.util.List;

public interface UserMapper {

    @Select("select * from user where name = #{name} and password=#{password}")
    List<User> getUserList(String name, String password);

    @Select("select * from user where id = #{id}")
    User getUserById(Integer id);
}
