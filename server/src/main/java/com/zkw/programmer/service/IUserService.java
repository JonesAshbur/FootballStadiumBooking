package com.zkw.programmer.service;

import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;


public interface IUserService {

    // 分页获取用户数据
    ResponseDTO<PageDTO<UserDTO>> getUserListByPage(PageDTO<UserDTO> pageDTO, String token);

    // 保存用户数据(添加、修改)
    ResponseDTO<Boolean> saveUser(UserDTO userDTO, String token);

    // 删除用户数据
    ResponseDTO<Boolean> removeUser(UserDTO userDTO);

    // 用户登录操作
    ResponseDTO<UserDTO> login(UserDTO userDTO);

    // 当前登录用户
    ResponseDTO<UserDTO> getLoginUser(String token);

    // 用户注册操作
    ResponseDTO<Boolean> register(UserDTO userDTO);

    // 检查用户是否登录
    ResponseDTO<UserDTO> checkLogin(UserDTO userDTO);

    // 退出登录操作
    ResponseDTO<Boolean> logout(UserDTO userDTO);

    // 获取用户总数
    ResponseDTO<Integer> getUserTotal();

}
