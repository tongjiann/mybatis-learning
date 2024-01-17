package com.xiw.config;

import com.xiw.pojo.Configuration;
import com.xiw.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * 解析映射配置文件并装入configuration中
 */
public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream in) throws DocumentException {
        Document document = new SAXReader().read(in);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");
        /*
            <select id="selectOne" resultType="com.xiw.pojo.User" parameterType="com.xiw.pojo.User">
                select * from user where id = #{id} and username = #{username}
            </select>
         */
        List<Node> nodeList = rootElement.selectNodes("//select");
        for (Node node : nodeList) {
            if (node instanceof Element element) {
                String id = element.attributeValue("id");
                String resultType = element.attributeValue("resultType");
                String parameterType = element.attributeValue("parameterType");
                String sql = element.getTextTrim();

                MappedStatement mappedStatement = new MappedStatement();
                String statementId = namespace + "." + id;
                mappedStatement.setStatementId(statementId);
                mappedStatement.setParameterType(parameterType);
                mappedStatement.setResultType(resultType);
                mappedStatement.setSql(sql);

                configuration.getMappedStatementMap().put(statementId, mappedStatement);
            }
        }
    }

}
