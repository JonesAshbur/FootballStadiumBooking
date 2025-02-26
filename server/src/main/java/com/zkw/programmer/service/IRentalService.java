package com.zkw.programmer.service;

import com.zkw.programmer.domain.Rental;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.RentalDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;

import java.util.List;


public interface IRentalService {

    // 分页获取租借数据
    ResponseDTO<PageDTO<RentalDTO>> getRentalListByPage(PageDTO<RentalDTO> pageDTO, String token);

    // 保存租借数据(添加、修改)
    ResponseDTO<Boolean> saveRental(RentalDTO rentalDTO);

    // 删除租借数据
    ResponseDTO<Boolean> removeRental(RentalDTO rentalDTO);

    // 获取租借总数
    ResponseDTO<Integer> getRentalTotal(UserDTO userDTO);

    // 获取近五天租借数
    ResponseDTO<List<Integer>> getRentalTotalByDays(UserDTO userDTO);

    // 多线程处理租借数据
    List<Rental> operateRentalListState(List<Rental> rentalList, Integer pages, Integer pageSize) throws InterruptedException;
}
