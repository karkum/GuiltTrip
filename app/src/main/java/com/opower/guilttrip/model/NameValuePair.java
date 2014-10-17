package com.opower.guilttrip.model;

public class NameValuePair {

    private String name;
    private String code;

    public NameValuePair(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
