package com.xiw.sqlSession;

import com.xiw.config.XMLConfigBuilder;
import com.xiw.pojo.Configuration;
import org.dom4j.DocumentException;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream) throws DocumentException {
        // 解析文件 封装容器对象
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();

        Configuration configuration = xmlConfigBuilder.parse(inputStream);

        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);

        return sqlSessionFactory;

    }

}
