package com.example.demo.entity;

public class ResultUserType {
    private Integer code;
    private String desc;
    public ResultUserType(Integer code,String desc){
        this.code = code;
        this.desc = desc;
    }
    public Integer getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }
}

