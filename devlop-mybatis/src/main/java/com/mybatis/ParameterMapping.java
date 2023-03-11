package com.mybatis;

/**
 * @author Clinton Begin
 */
public class ParameterMapping {


    private String property;

    public ParameterMapping(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}