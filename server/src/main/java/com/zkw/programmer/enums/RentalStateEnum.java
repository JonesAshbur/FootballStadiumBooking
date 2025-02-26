package com.zkw.programmer.enums;


public enum RentalStateEnum {

    WAIT(1,"待审核"),

    SUCCESS(2,"审核通过"),

    FAIL(3,"审核不通过"),

    RENTAL(4,"租借中"),

    FINISH(5,"已完成"),

    CANCEL(6,"已取消"),
    ;

    Integer code;

    String desc;

    RentalStateEnum(Integer code, String desc) {
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
