package com.zkw.programmer.dto;

import com.zkw.programmer.annotation.ValidateEntity;

import java.math.BigDecimal;


public class HallDTO {

    private String id;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=16,minLength=1,errorRequiredMsg="体育馆名称不能为空！",errorMaxLengthMsg="体育馆名称长度不能大于16！",errorMinLengthMsg="体育馆名称不能为空！")
    private String name;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=64,minLength=1,errorRequiredMsg="体育馆位置不能为空！",errorMaxLengthMsg="体育馆位置长度不能大于64！",errorMinLengthMsg="体育馆位置不能为空！")
    private String location;

    @ValidateEntity(required=true,requiredMaxValue=true,requiredMinValue=true,maxValue=99999999.99,minValue=0,errorRequiredMsg="预约费用不能为空！",errorMaxValueMsg="预约费用不能大于99999999.99！",errorMinValueMsg="预约费用不能小于0！")
    private BigDecimal fee;

    private Integer state;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=256,minLength=1,errorRequiredMsg="体育馆简介不能为空！",errorMaxLengthMsg="体育馆简介长度不能大于256！",errorMinLengthMsg="体育馆简介不能为空！")
    private String info;

    private String photo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", location=").append(location);
        sb.append(", fee=").append(fee);
        sb.append(", state=").append(state);
        sb.append(", info=").append(info);
        sb.append(", photo=").append(photo);
        sb.append("]");
        return sb.toString();
    }
}
