package com.xiw.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.DruidDataSourceUtils;
import com.xiw.io.Resources;
import com.xiw.pojo.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigBuilder {

    private final Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    private static DruidDataSource parseDataSource(Element rootElement) {
        List<Node> nodes = rootElement.selectNodes("//property");
        // <property name="url" value="jdbc:mysql:///ipersistent-test"/>
        Properties properties = new Properties();
        for (Node node : nodes) {
            if (node instanceof Element element) {
                String name = element.attributeValue("name");
                String value = element.attributeValue("value");
                properties.setProperty(name, value);
            }
        }

        // 创建数据源对象
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(properties.getProperty("driverClassName"));
        druidDataSource.setUrl(properties.getProperty("url"));
        druidDataSource.setUsername(properties.getProperty("username"));
        druidDataSource.setPassword(properties.getProperty("password"));
        return druidDataSource;
    }

    /**
     * 使用dom4j+xpath
     */
    public Configuration parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();

        configuration.setDataSource(parseDataSource(rootElement));

        // 解析映射配置文件
        List<Node> nodeList = rootElement.selectNodes("//mapper");
        for (Node node : nodeList) {
            if (node instanceof Element element) {
                String mapperPath = element.attributeValue("resource");
                InputStream resourceAsStream = Resources.getResourceAsStream(mapperPath);
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
                xmlMapperBuilder.parse(resourceAsStream);
            }
        }

        return configuration;
    }

}
