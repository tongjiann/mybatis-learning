package com.xiw.sqlSession;

import com.xiw.executor.Executor;
import com.xiw.pojo.Configuration;
import com.xiw.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

}
