package com.sifan;

import com.mybatis.*;

import java.util.List;

public interface UserMapper {

    @Select("select * from user where name=#{name} and password=#{password}")
    List<User> getUsers(@Param("name") String name, @Param("password") String password);

    @Select("select * from user where id=#{id}")
    User getUserById(@Param("id") Integer id);

    @Delete("delete from user where id=#{id}")
    Integer deleteUserById(@Param("id") Integer id);

    @Update("update user set name=#{newName} where name=#{name}")
    Integer updateUser(@Param("name") String name, @Param("newName") String newName);

    @Insert("insert into user (name,password) values (#{name},#{password})")
    Integer addUser(@Param("name") String name, @Param("password") String password);
}
