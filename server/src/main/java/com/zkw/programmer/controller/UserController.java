package com.zkw.programmer.controller;

import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;
import com.zkw.programmer.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController("UserController")
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 分页获取用户数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/list")
    public ResponseDTO<PageDTO<UserDTO>> getUserListByPage(@RequestBody PageDTO<UserDTO> pageDTO, HttpServletRequest request){
        String token = request.getHeader("token");
        return userService.getUserListByPage(pageDTO, token);
    }

    /**
     * 保存用户数据(添加、修改)
     * @param userDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveUser(@RequestBody UserDTO userDTO, HttpServletRequest request){
        String token = request.getHeader("token");
        return userService.saveUser(userDTO, token);
    }

    /**
     * 删除用户数据
     * @param userDTO
     * @return
     */
    @PostMapping("/remove")
    public ResponseDTO<Boolean> removeUser(@RequestBody UserDTO userDTO){
        return userService.removeUser(userDTO);
    }

    /**
     * 用户登录操作处理
     * @param userDTO
     * @return
     */
    @PostMapping("/login")
    public ResponseDTO<UserDTO> login(@RequestBody UserDTO userDTO){
        return userService.login(userDTO);
    }

    /**
     * 用户注册操作处理
     * @param userDTO
     * @return
     */
    @PostMapping("/register")
    public ResponseDTO<Boolean> register(@RequestBody UserDTO userDTO){
        return userService.register(userDTO);
    }

    /**
     * 获取当前登录用户
     * @param userDTO
     * @return
     */
    @PostMapping("/getLoginUser")
    public ResponseDTO<UserDTO> getLoginUser(@RequestBody UserDTO userDTO){
        return userService.getLoginUser(userDTO.getToken());
    }

    /**
     * 获取用户总数
     * @return
     */
    @PostMapping("/total")
    public ResponseDTO<Integer> getUserTotal(){
        return userService.getUserTotal();
    }

    /**
     * 用户退出登录
     * @return
     */
    @PostMapping("/logout")
    public ResponseDTO<Boolean> logout(@RequestBody UserDTO userDTO){
        return userService.logout(userDTO);
    }

}
