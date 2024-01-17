package com.xiw.sqlSession;

public interface SqlSessionFactory {


    /**
     * 生产SqlSession 创建Executor
     */
    SqlSession openSession();

}
