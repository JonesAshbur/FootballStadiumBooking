package com.zkw.programmer.controller;

import com.zkw.programmer.dto.AnnounceDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.service.IAnnounceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController("AnnounceController")
@RequestMapping("/announce")
public class AnnounceController {

    @Resource
    private IAnnounceService announceService;

    /**
     * 分页获取公告数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/list")
    public ResponseDTO<PageDTO<AnnounceDTO>> getAnnounceListByPage(@RequestBody PageDTO<AnnounceDTO> pageDTO){
        return announceService.getAnnounceListByPage(pageDTO);
    }

    /**
     * 保存公告数据(添加、修改)
     * @param announceDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveAnnounce(@RequestBody AnnounceDTO announceDTO){
        return announceService.saveAnnounce(announceDTO);
    }

    /**
     * 删除公告数据
     * @param announceDTO
     * @return
     */
    @PostMapping("/remove")
    public ResponseDTO<Boolean> removeAnnounce(@RequestBody AnnounceDTO announceDTO){
        return announceService.removeAnnounce(announceDTO);
    }
}
