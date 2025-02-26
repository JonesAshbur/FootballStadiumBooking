package com.zkw.programmer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zkw.programmer.annotation.ValidateEntity;

import java.math.BigDecimal;
import java.util.Date;

public class AppointmentDTO {

    private String id;

    private String hallId;

    private String hallName;

    private String hallPhoto;

    private String userId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ValidateEntity(required=true,errorRequiredMsg="预约开始时间不能为空！")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @ValidateEntity(required=true,errorRequiredMsg="预约结束时间不能为空！")
    private Date endTime;

    private Integer state;

    private Date createTime;

    private BigDecimal fee;

    private String feeRule;

    @ValidateEntity(requiredMaxLength=true,maxLength=128,errorMaxLengthMsg="审核备注长度不能大于128！")
    private String remark;

    private UserDTO userDTO;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public String getHallPhoto() {
        return hallPhoto;
    }

    public void setHallPhoto(String hallPhoto) {
        this.hallPhoto = hallPhoto;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", hallId=").append(hallId);
        sb.append(", hallName=").append(hallName);
        sb.append(", hallPhoto=").append(hallPhoto);
        sb.append(", userId=").append(userId);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", state=").append(state);
        sb.append(", createTime=").append(createTime);
        sb.append(", fee=").append(fee);
        sb.append(", feeRule=").append(feeRule);
        sb.append(", remark=").append(remark);
        sb.append(", userDTO=").append(userDTO);
        sb.append("]");
        return sb.toString();
    }
}
