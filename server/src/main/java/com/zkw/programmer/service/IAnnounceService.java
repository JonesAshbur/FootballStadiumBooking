package com.zkw.programmer.service;

import com.zkw.programmer.dto.AnnounceDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;


public interface IAnnounceService {

    // 分页获取公告数据
    ResponseDTO<PageDTO<AnnounceDTO>> getAnnounceListByPage(PageDTO<AnnounceDTO> pageDTO);

    // 保存公告数据(添加、修改)
    ResponseDTO<Boolean> saveAnnounce(AnnounceDTO announceDTO);

    // 删除公告数据
    ResponseDTO<Boolean> removeAnnounce(AnnounceDTO announceDTO);
}
