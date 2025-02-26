package com.zkw.programmer.controller;

import com.zkw.programmer.dto.HallDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.service.IHallService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController("HallController")
@RequestMapping("/hall")
public class HallController {

    @Resource
    private IHallService hallService;

    /**
     * 分页获取体育场馆数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/list")
    public ResponseDTO<PageDTO<HallDTO>> getHallListByPage(@RequestBody PageDTO<HallDTO> pageDTO){
        return hallService.getHallListByPage(pageDTO);
    }

    /**
     * 保存体育场馆数据(添加、修改)
     * @param hallDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveHall(@RequestBody HallDTO hallDTO){
        return hallService.saveHall(hallDTO);
    }

    /**
     * 删除体育场馆数据
     * @param hallDTO
     * @return
     */
    @PostMapping("/remove")
    public ResponseDTO<Boolean> removeHall(@RequestBody HallDTO hallDTO){
        return hallService.removeHall(hallDTO);
    }

}
