package com.zkw.programmer.service;

import com.zkw.programmer.domain.Appointment;
import com.zkw.programmer.dto.AppointmentDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;

import java.util.List;


public interface IAppointmentService {

    // 分页获取预约数据
    ResponseDTO<PageDTO<AppointmentDTO>> getAppointmentListByPage(PageDTO<AppointmentDTO> pageDTO, String token);

    // 保存预约数据(添加、修改)
    ResponseDTO<Boolean> saveAppointment(AppointmentDTO appointmentDTO);

    // 删除预约数据
    ResponseDTO<Boolean> removeAppointment(AppointmentDTO appointmentDTO);

    // 获取预约总数
    ResponseDTO<Integer> getAppointmentTotal(UserDTO userDTO);

    // 获取近五天预约数
    ResponseDTO<List<Integer>> getAppointmentTotalByDays(UserDTO userDTO);

    // 获取已经预约的数据
    ResponseDTO<List<AppointmentDTO>> getAppointmentExistList();

    // 多线程处理预约信息的状态
    List<Appointment> operateAppointmentListState(List<Appointment> appointmentList, Integer pages, Integer pageSize) throws InterruptedException;
}
