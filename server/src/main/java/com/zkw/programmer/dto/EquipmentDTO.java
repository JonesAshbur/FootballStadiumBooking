package com.zkw.programmer.dto;

import com.zkw.programmer.annotation.ValidateEntity;

import java.math.BigDecimal;


public class EquipmentDTO {

    private String id;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=16,minLength=1,errorRequiredMsg="体育器材名称不能为空！",errorMaxLengthMsg="体育器材名称长度不能大于16！",errorMinLengthMsg="体育器材名称不能为空！")
    private String name;

    @ValidateEntity(required=true,requiredMaxValue=true,requiredMinValue=true,maxValue=99999999,minValue=0,errorRequiredMsg="体育器材数量不能为空！",errorMaxValueMsg="体育器材数量不能大于99999999！",errorMinValueMsg="体育器材数量不能小于0！")
    private Integer num;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=16,minLength=1,errorRequiredMsg="体育器材品牌不能为空！",errorMaxLengthMsg="体育器材品牌长度不能大于16！",errorMinLengthMsg="体育器材品牌不能为空！")
    private String brand;

    @ValidateEntity(required=true,requiredMaxValue=true,requiredMinValue=true,maxValue=99999999.99,minValue=0,errorRequiredMsg="租借费用不能为空！",errorMaxValueMsg="租借费用不能大于99999999.99！",errorMinValueMsg="租借费用不能小于0！")
    private BigDecimal fee;

    private String photo;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=256,minLength=1,errorRequiredMsg="体育器材简介不能为空！",errorMaxLengthMsg="体育器材简介长度不能大于256！",errorMinLengthMsg="体育器材简介不能为空！")
    private String info;

    private Integer state;

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

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", num=").append(num);
        sb.append(", brand=").append(brand);
        sb.append(", fee=").append(fee);
        sb.append(", photo=").append(photo);
        sb.append(", info=").append(info);
        sb.append(", state=").append(state);
        sb.append("]");
        return sb.toString();
    }
}
