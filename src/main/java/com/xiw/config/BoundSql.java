package com.xiw.config;

import com.xiw.util.ParameterMapping;

import java.util.List;

public class BoundSql {


    private String finalSql;

    private List<ParameterMapping> parameterMappingList;

    public String getFinalSql() {
        return finalSql;
    }

    public void setFinalSql(String finalSql) {
        this.finalSql = finalSql;
    }

    public List<ParameterMapping> getParameterMappingList() {
        return parameterMappingList;
    }

    public void setParameterMappingList(List<ParameterMapping> parameterMappingList) {
        this.parameterMappingList = parameterMappingList;
    }

    public BoundSql(String finalSql, List<ParameterMapping> parameterMappingList) {
        this.finalSql = finalSql;
        this.parameterMappingList = parameterMappingList;
    }

}
