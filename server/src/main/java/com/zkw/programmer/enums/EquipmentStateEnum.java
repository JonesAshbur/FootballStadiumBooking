package com.zkw.programmer.enums;


public enum EquipmentStateEnum {

    ON(1,"已上架"),

    OFF(2,"已下架"),

    ;

    Integer code;

    String desc;

    EquipmentStateEnum(Integer code, String desc) {
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
