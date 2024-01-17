package com.xiw.sqlSession;

import com.xiw.executor.Executor;
import com.xiw.executor.SimpleExecutor;
import com.xiw.pojo.Configuration;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        Executor executor = new SimpleExecutor();

        SqlSession sqlSession = new DefaultSqlSession(configuration, executor);

        return sqlSession;
    }

}
