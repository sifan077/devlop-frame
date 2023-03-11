package com.mybatis;

import com.sifan.User;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MapperProxyFactory {

    public static <T> T getMapper(Class<T> mapper) {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/mybatis";
        String user = "root";
        String password = "password";
        Object instance = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                new Class[]{mapper}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Class.forName(driver);
                        Connection connection = DriverManager.getConnection(url, user, password);
                        String sql = "select * from user where name=? and password=?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, "张三");
                        statement.setString(2, "123456");
                        ResultSet resultSet = statement.executeQuery();
                        List<User> list = new ArrayList<>();
                        while (resultSet.next()) {
                            User user = new User();
                            user.setId(resultSet.getInt("id"));
                            user.setName(resultSet.getString("name"));
                            user.setPassword(resultSet.getString("password"));
                            list.add(user);
                        }
                        return list;
                    }
                });

        return (T) instance;
    }
}
