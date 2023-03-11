package com.sifan;

import com.mybatis.Param;
import com.mybatis.Select;

import java.util.List;

public interface UserMapper {

    @Select("select * from user where name=#{name} and password=#{password}")
    List<User> getUserList(@Param("name") String name, @Param("password") String password);

    @Select("select * from user where id = #{id}")
    User getUserById(@Param("id") Integer id);
}
