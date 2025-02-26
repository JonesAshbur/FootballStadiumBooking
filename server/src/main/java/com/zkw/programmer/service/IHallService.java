package com.zkw.programmer.service;

import com.zkw.programmer.dto.HallDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;


public interface IHallService {

    // 分页获取体育场馆数据
    ResponseDTO<PageDTO<HallDTO>> getHallListByPage(PageDTO<HallDTO> pageDTO);

    // 保存体育场馆数据(添加、修改)
    ResponseDTO<Boolean> saveHall(HallDTO hallDTO);

    // 删除体育场馆数据
    ResponseDTO<Boolean> removeHall(HallDTO hallDTO);
}
