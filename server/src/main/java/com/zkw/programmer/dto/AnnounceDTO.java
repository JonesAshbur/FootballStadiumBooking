package com.zkw.programmer.dto;

import com.zkw.programmer.annotation.ValidateEntity;

import java.util.Date;


public class AnnounceDTO {

    private String id;

    @ValidateEntity(required=true,requiredMaxLength=true,requiredMinLength=true,maxLength=512,minLength=1,errorRequiredMsg="公告内容不能为空！",errorMaxLengthMsg="公告内容长度不能大于512！",errorMinLengthMsg="公告内容不能为空！")
    private String content;

    private String userId;

    private Date createTime;

    private UserDTO userDTO;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
        sb.append(", content=").append(content);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", userDTO=").append(userDTO);
        sb.append("]");
        return sb.toString();
    }
}
