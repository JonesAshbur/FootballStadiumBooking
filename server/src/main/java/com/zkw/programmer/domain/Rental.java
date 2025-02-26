package com.zkw.programmer.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Rental {
    private String id;

    private Date startTime;

    private Date endTime;

    private BigDecimal fee;

    private String feeRule;

    private Integer num;

    private Date createTime;

    private String userId;

    private Integer state;

    private String equipmentId;

    private String equipmentName;

    private String equipmentPhoto;

    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getFeeRule() {
        return feeRule;
    }

    public void setFeeRule(String feeRule) {
        this.feeRule = feeRule;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentPhoto() {
        return equipmentPhoto;
    }

    public void setEquipmentPhoto(String equipmentPhoto) {
        this.equipmentPhoto = equipmentPhoto;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", fee=").append(fee);
        sb.append(", feeRule=").append(feeRule);
        sb.append(", num=").append(num);
        sb.append(", createTime=").append(createTime);
        sb.append(", userId=").append(userId);
        sb.append(", state=").append(state);
        sb.append(", equipmentId=").append(equipmentId);
        sb.append(", equipmentName=").append(equipmentName);
        sb.append(", equipmentPhoto=").append(equipmentPhoto);
        sb.append(", remark=").append(remark);
        sb.append("]");
        return sb.toString();
    }
}