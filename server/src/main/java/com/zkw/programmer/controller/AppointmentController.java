package com.zkw.programmer.controller;

import com.zkw.programmer.dto.AppointmentDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;
import com.zkw.programmer.service.IAppointmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController("AppointmentController")
@RequestMapping("/appointment")
public class AppointmentController {

    @Resource
    private IAppointmentService appointmentService;


    /**
     * 保存预约数据(添加、修改)
     * @param appointmentDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveAppointment(@RequestBody AppointmentDTO appointmentDTO){
        return appointmentService.saveAppointment(appointmentDTO);
    }

    /**
     * 分页获取预约数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/list")
    public ResponseDTO<PageDTO<AppointmentDTO>> getAppointmentListByPage(@RequestBody PageDTO<AppointmentDTO> pageDTO, HttpServletRequest request){
        String token = request.getHeader("token");
        return appointmentService.getAppointmentListByPage(pageDTO, token);
    }

    /**
     * 删除预约数据
     * @param appointmentDTO
     * @return
     */
    @PostMapping("/remove")
    public ResponseDTO<Boolean> removeAppointment(@RequestBody AppointmentDTO appointmentDTO){
        return appointmentService.removeAppointment(appointmentDTO);
    }

    /**
     * 获取预约总数
     * @return
     */
    @PostMapping("/total")
    public ResponseDTO<Integer> getAppointmentTotal(@RequestBody UserDTO userDTO){
        return appointmentService.getAppointmentTotal(userDTO);
    }

    /**
     * 获取近五天预约数
     * @return
     */
    @PostMapping("/total-day")
    public ResponseDTO<List<Integer>> getAppointmentTotalByDays(@RequestBody UserDTO userDTO){
        return appointmentService.getAppointmentTotalByDays(userDTO);
    }

    /**
     * 获取已经预约的数据
     * @return
     */
    @PostMapping("/list_exist")
    public ResponseDTO<List<AppointmentDTO>> getAppointmentExistList() {
        return appointmentService.getAppointmentExistList();
    }

}
