package com.zkw.programmer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkw.programmer.bean.CodeMsg;
import com.zkw.programmer.dao.HallMapper;
import com.zkw.programmer.domain.Hall;
import com.zkw.programmer.domain.HallExample;
import com.zkw.programmer.dto.HallDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.service.IHallService;
import com.zkw.programmer.utils.CommonUtil;
import com.zkw.programmer.utils.CopyUtil;
import com.zkw.programmer.utils.UuidUtil;
import com.zkw.programmer.utils.ValidateEntityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class HallServiceImpl implements IHallService {

    @Resource
    private HallMapper hallMapper;

    /**
     * 分页获取体育场馆数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<HallDTO>> getHallListByPage(PageDTO<HallDTO> pageDTO) {
        HallExample hallExample = new HallExample();
        // 判断是否进行关键字搜索
        HallDTO searchHallDTO = pageDTO.getSearchEntity();
        HallExample.Criteria criteria = hallExample.createCriteria();
        if(!CommonUtil.isEmpty(searchHallDTO.getName())) {
            criteria.andNameLike("%" + searchHallDTO.getName() + "%");
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        if(pageDTO.getSize() == null) {
            pageDTO.setSize(5);
        }
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出体育场馆数据
        List<Hall> hallList = hallMapper.selectByExample(hallExample);
        PageInfo<Hall> pageInfo = new PageInfo<>(hallList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 将domain类型数据  转成 DTO类型数据
        List<HallDTO> hallDTOList = CopyUtil.copyList(hallList, HallDTO.class);
        pageDTO.setList(hallDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存体育场馆数据(添加、修改)
     * @param hallDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveHall(HallDTO hallDTO) {
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(hallDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Hall hall = CopyUtil.copy(hallDTO, Hall.class);
        if(CommonUtil.isEmpty(hall.getId())){
            // id为空 说明是添加数据
            // 生成8位id
            hall.setId(UuidUtil.getShortUuid());
            // 添加数据到数据库
            if(hallMapper.insertSelective(hall) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.HALL_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 修改数据库中数据
            if(hallMapper.updateByPrimaryKeySelective(hall) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.HALL_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除体育场馆数据
     * @param hallDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeHall(HallDTO hallDTO) {
        if(CommonUtil.isEmpty(hallDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除体育场馆信息
        if(hallMapper.deleteByPrimaryKey(hallDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.HALL_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }
}
