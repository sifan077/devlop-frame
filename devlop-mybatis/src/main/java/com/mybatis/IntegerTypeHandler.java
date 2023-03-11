package com.mybatis;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerTypeHandler implements TypeHandler<Integer> {

    @Override
    public void setParameter(PreparedStatement statement, int i, Integer value) throws SQLException {
        statement.setInt(i + 1, value);
    }

    @Override
    public Integer getParameter(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getInt(column);
    }
}
