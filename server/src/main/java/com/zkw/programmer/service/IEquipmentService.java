package com.zkw.programmer.service;

import com.zkw.programmer.dto.EquipmentDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;


public interface IEquipmentService {

    // 分页获取体育器材数据
    ResponseDTO<PageDTO<EquipmentDTO>> getEquipmentListByPage(PageDTO<EquipmentDTO> pageDTO);

    // 保存体育器材数据(添加、修改)
    ResponseDTO<Boolean> saveEquipment(EquipmentDTO equipmentDTO);

    // 删除体育器材数据
    ResponseDTO<Boolean> removeEquipment(EquipmentDTO equipmentDTO);
}
