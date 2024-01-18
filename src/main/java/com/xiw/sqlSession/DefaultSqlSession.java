package com.xiw.sqlSession;

import com.xiw.executor.Executor;
import com.xiw.pojo.Configuration;
import com.xiw.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> List<T> selectList(String statementId, Object param) throws SQLException, IntrospectionException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<T> list = executor.query(configuration, mappedStatement, param);
        return list;
    }

    @Override
    public <T> T selectOne(String statementId, Object param) throws SQLException, IntrospectionException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        List<T> list = this.selectList(statementId, param);
        int size = list.size();
        if (size == 1) {
            return list.get(0);
        } else if (size > 1) {
            throw new RuntimeException("返回结果过多");
        } else {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            executor.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理生成基于接口的代理对象
        Object proxy = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 具体逻辑 执行底层的JDBC
                // 通过sqlSession中的方法完成调用
                // 准备statementId param

                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;

                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                String sqlCommandType = mappedStatement.getSqlCommandType();
                Object param = null;
                if (args != null) {
                    param = args[0];
                }
                switch (sqlCommandType) {
                    case "select":
                        Type genericReturnType = method.getGenericReturnType();
                        // 判断是否实现了泛型类型参数化
                        if (genericReturnType instanceof ParameterizedType) {
                            return selectList(statementId, param);
                        }
                        return selectOne(statementId, param);
                    case "insert":
                        break;
                    case "update":
                        break;
                    case "delete":
                        break;
                }
                return null;
            }
        });
        return (T) proxy;
    }

}
