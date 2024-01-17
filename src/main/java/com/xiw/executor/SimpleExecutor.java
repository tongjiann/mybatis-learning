package com.xiw.executor;

import com.xiw.config.BoundSql;
import com.xiw.parsing.GenericTokenParser;
import com.xiw.pojo.Configuration;
import com.xiw.pojo.MappedStatement;
import com.xiw.util.ParameterMapping;
import com.xiw.util.ParameterMappingTokenHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    private Connection connection = null;

    private PreparedStatement preparedStatement = null;

    private ResultSet resultSet = null;

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (resultSet != null) {
            resultSet.close();
        }
    }

    @Override
    public <T> List<T> query(Configuration configuration, MappedStatement mappedStatement, Object param) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, InvocationTargetException {

        connection = configuration.getDataSource().getConnection();

        String sql = mappedStatement.getSql();

        // 替换sql
        BoundSql boundSql = getBoundSql(sql);

        String finalSql = boundSql.getFinalSql();

        preparedStatement = connection.prepareStatement(finalSql);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        String parameterType = mappedStatement.getParameterType();
        if (parameterType != null) {

            Class<?> parameterTypeClass = Class.forName(parameterType);

            for (int i = 0; i < parameterMappingList.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();
                // 通过反射获取content属性的值
                Field declaredField = parameterTypeClass.getDeclaredField(content);
                boolean b = declaredField.canAccess(param);
                declaredField.setAccessible(true);

                Object value = declaredField.get(param);
                if (!b) {
                    declaredField.setAccessible(false);
                }
                preparedStatement.setObject(i + 1, value);
            }
        }
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = Class.forName(resultType);
        resultSet = preparedStatement.executeQuery();
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
        Object resultInstance = resultTypeClass.newInstance();

            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                String columnName = metaData.getColumnName(i);
                Object object = resultSet.getObject(columnName);

                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(resultInstance, object);
            }
            list.add((T) resultInstance);
        }

        return list;
    }


    /**
     * 将 #{} 替换为?
     * 将 #{} 里面的值记录下来
     */
    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String finalSql = genericTokenParser.parse(sql);

        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        return new BoundSql(finalSql, parameterMappings);

    }

}
