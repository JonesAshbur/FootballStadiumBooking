package com.zkw.programmer.enums;


public enum HallStateEnum {

    NORMAL(1,"正常"),

    REPAIR(2,"维护中"),

    ;

    Integer code;

    String desc;

    HallStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
