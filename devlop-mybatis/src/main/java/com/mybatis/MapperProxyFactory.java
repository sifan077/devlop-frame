package com.mybatis;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public class MapperProxyFactory {

    public static <T> T getMapper(Class<T> mapper) {
        Map<Class, TypeHandler> typeHandlerMap = new HashMap<>();
        typeHandlerMap.put(String.class, new StringTypeHandler());
        typeHandlerMap.put(Integer.class, new IntegerTypeHandler());

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
                        String sql = "";
                        boolean isSelect = true;
                        Select select = method.getAnnotation(Select.class);
                        if (Objects.isNull(select)) {
                            isSelect = false;
                        } else {
                            sql = select.value();
                        }

                        Insert insert = method.getDeclaredAnnotation(Insert.class);
                        if (!Objects.isNull(insert)) {
                            sql = insert.value();
                        }
                        Delete delete = method.getDeclaredAnnotation(Delete.class);
                        if (!Objects.isNull(delete)) {
                            sql = delete.value();
                        }
                        Update update = method.getDeclaredAnnotation(Update.class);
                        if (!Objects.isNull(update)) {
                            sql = update.value();
                        }

                        PreparedStatement statement = getPreparedStatement(method, args, connection, sql, typeHandlerMap);
                        if (!isSelect) return statement.executeUpdate();


                        ResultSet resultSet = statement.executeQuery();
                        Class<?> returnType = method.getReturnType();

                        if (!returnType.toString().equals("interface java.util.List")) {
                            Object newInstance = returnType.getDeclaredConstructor().newInstance();
                            while (resultSet.next()) {
                                makeSingleInstance(resultSet, returnType, newInstance, typeHandlerMap);
                            }
                            return newInstance;
                        }

                        Class resultType = null;
                        Type genericReturnType = method.getGenericReturnType();
                        if (genericReturnType instanceof Class) {
                            // 不是泛型
                            resultType = (Class) returnType;
                        } else if (genericReturnType instanceof ParameterizedType) {
                            // 是泛型
                            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                            resultType = (Class) actualTypeArguments[0];
                        }

                        List<Object> list = new ArrayList<>();
                        while (resultSet.next()) {
                            Object newInstance = resultType.getDeclaredConstructor().newInstance();
                            makeSingleInstance(resultSet, resultType, newInstance, typeHandlerMap);
                            list.add(newInstance);
                        }
                        return list;
                    }
                });

        return (T) instance;
    }

    private static PreparedStatement getPreparedStatement(Method method, Object[] args, Connection connection, String sql, Map<Class, TypeHandler> typeHandlerMap) throws SQLException {
        Map<String, Object> paramValueMapping = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String value = parameters[i].getAnnotation(Param.class).value();
            paramValueMapping.put(parameters[i].getName(), args[i]);
            paramValueMapping.put(value, args[i]);
        }
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", tokenHandler);
        String parseSql = parser.parse(sql);

        List<ParameterMapping> parameterMappings = tokenHandler.getParameterMappings();

        PreparedStatement statement
                = connection.prepareStatement(parseSql);
        for (int i = 0; i < parameterMappings.size(); i++) {
            String property = parameterMappings.get(i).getProperty();
            Object o = paramValueMapping.get(property);
            typeHandlerMap.get(o.getClass()).setParameter(statement, i, o);
        }
        return statement;
    }

    private static void makeSingleInstance(ResultSet resultSet, Class resultType, Object newInstance, Map<Class, TypeHandler> typeHandlerMap) throws NoSuchFieldException, java.sql.SQLException, IllegalAccessException, InvocationTargetException {
        Method[] declaredMethods = resultType.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().startsWith("set")) {
                String filed = declaredMethod.getName().substring(3).toLowerCase(Locale.ROOT);
                Field declaredField = resultType.getDeclaredField(filed);
                Object o = typeHandlerMap.get(declaredField.getType()).getParameter(resultSet, filed);
                declaredMethod.invoke(newInstance, o);
            }
        }
    }
}
