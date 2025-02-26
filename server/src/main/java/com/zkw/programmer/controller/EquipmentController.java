package com.zkw.programmer.controller;

import com.zkw.programmer.dto.EquipmentDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.service.IEquipmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController("EquipmentController")
@RequestMapping("/equipment")
public class EquipmentController {

    @Resource
    private IEquipmentService equipmentService;

    /**
     * 分页获取体育器材数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/list")
    public ResponseDTO<PageDTO<EquipmentDTO>> getEquipmentListByPage(@RequestBody PageDTO<EquipmentDTO> pageDTO){
        return equipmentService.getEquipmentListByPage(pageDTO);
    }

    /**
     * 保存体育器材数据(添加、修改)
     * @param equipmentDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveEquipment(@RequestBody EquipmentDTO equipmentDTO){
        return equipmentService.saveEquipment(equipmentDTO);
    }

    /**
     * 删除体育器材数据
     * @param equipmentDTO
     * @return
     */
    @PostMapping("/remove")
    public ResponseDTO<Boolean> removeEquipment(@RequestBody EquipmentDTO equipmentDTO){
        return equipmentService.removeEquipment(equipmentDTO);
    }

}
