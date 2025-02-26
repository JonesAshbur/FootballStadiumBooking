package com.zkw.programmer.controller;

import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.RentalDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;
import com.zkw.programmer.service.IRentalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController("RentalController")
@RequestMapping("/rental")
public class RentalController {

    @Resource
    private IRentalService rentalService;

    /**
     * 保存租借数据(添加、修改)
     * @param rentalDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseDTO<Boolean> saveRental(@RequestBody RentalDTO rentalDTO){
        return rentalService.saveRental(rentalDTO);
    }

    /**
     * 分页获取租借数据
     * @param pageDTO
     * @return
     */
    @PostMapping("/list")
    public ResponseDTO<PageDTO<RentalDTO>> getRentalListByPage(@RequestBody PageDTO<RentalDTO> pageDTO, HttpServletRequest request){
        String token = request.getHeader("token");
        return rentalService.getRentalListByPage(pageDTO, token);
    }

    /**
     * 删除租借数据
     * @param rentalDTO
     * @return
     */
    @PostMapping("/remove")
    public ResponseDTO<Boolean> removeRental(@RequestBody RentalDTO rentalDTO){
        return rentalService.removeRental(rentalDTO);
    }

    /**
     * 获取租借总数
     * @return
     */
    @PostMapping("/total")
    public ResponseDTO<Integer> getRentalTotal(@RequestBody UserDTO userDTO){
        return rentalService.getRentalTotal(userDTO);
    }

    /**
     * 获取近五天租借数
     * @return
     */
    @PostMapping("/total-day")
    public ResponseDTO<List<Integer>> getRentalTotalByDays(@RequestBody UserDTO userDTO){
        return rentalService.getRentalTotalByDays(userDTO);
    }

}
