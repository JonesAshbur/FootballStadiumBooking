package com.zkw.programmer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkw.programmer.bean.CodeMsg;
import com.zkw.programmer.dao.EquipmentMapper;
import com.zkw.programmer.dao.RentalMapper;
import com.zkw.programmer.domain.Equipment;
import com.zkw.programmer.domain.EquipmentExample;
import com.zkw.programmer.domain.Rental;
import com.zkw.programmer.domain.RentalExample;
import com.zkw.programmer.dto.EquipmentDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.RentalDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.service.IEquipmentService;
import com.zkw.programmer.service.IRentalService;
import com.zkw.programmer.utils.CommonUtil;
import com.zkw.programmer.utils.CopyUtil;
import com.zkw.programmer.utils.UuidUtil;
import com.zkw.programmer.utils.ValidateEntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
public class EquipmentServiceImpl implements IEquipmentService {

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private RentalMapper rentalMapper;

    @Resource
    private IRentalService rentalService;

    private static final Logger logger = LoggerFactory.getLogger(EquipmentServiceImpl.class);

    /**
     * 分页获取体育器材数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<EquipmentDTO>> getEquipmentListByPage(PageDTO<EquipmentDTO> pageDTO) {
        PageDTO<RentalDTO> rentalPageDTO = new PageDTO<>();
        rentalPageDTO.setPage(1);
        rentalPageDTO.setSize(10);
        PageHelper.startPage(rentalPageDTO.getPage(), rentalPageDTO.getSize());
        // 分页查出租借数据
        List<Rental> rentalList = rentalMapper.selectByExample(new RentalExample());
        PageInfo<Rental> pageInfoRental = new PageInfo<>(rentalList);
        // 获取数据的总数
        rentalPageDTO.setTotal(pageInfoRental.getTotal());
        // 多线程处理租借信息的状态
        try {
            rentalService.operateRentalListState(rentalList, rentalPageDTO.getTotalPage(), rentalPageDTO.getSize());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }


        EquipmentExample equipmentExample = new EquipmentExample();
        // 判断是否进行关键字搜索
        EquipmentDTO searchEquipmentDTO = pageDTO.getSearchEntity();
        EquipmentExample.Criteria criteria = equipmentExample.createCriteria();
        if(!CommonUtil.isEmpty(searchEquipmentDTO.getName())) {
            criteria.andNameLike("%" + searchEquipmentDTO.getName() + "%");
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        if(pageDTO.getSize() == null) {
            pageDTO.setSize(5);
        }
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出体育器材数据
        List<Equipment> equipmentList = equipmentMapper.selectByExample(equipmentExample);
        PageInfo<Equipment> pageInfo = new PageInfo<>(equipmentList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 将domain类型数据  转成 DTO类型数据
        List<EquipmentDTO> equipmentDTOList = CopyUtil.copyList(equipmentList, EquipmentDTO.class);
        pageDTO.setList(equipmentDTOList);


        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存体育器材数据(添加、修改)
     * @param equipmentDTO
     * @return
     */
    @Override
    @Transactional
    public ResponseDTO<Boolean> saveEquipment(EquipmentDTO equipmentDTO) {
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(equipmentDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Equipment equipment = CopyUtil.copy(equipmentDTO, Equipment.class);
        if(CommonUtil.isEmpty(equipment.getId())){
            // id为空 说明是添加数据
            // 生成8位id
            equipment.setId(UuidUtil.getShortUuid());
            // 添加数据到数据库
            if(equipmentMapper.insertSelective(equipment) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.EQUIPMENT_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 修改数据库中数据
            if(equipmentMapper.updateByPrimaryKeySelective(equipment) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.EQUIPMENT_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除体育器材数据
     * @param equipmentDTO
     * @return
     */
    @Override
    @Transactional
    public ResponseDTO<Boolean> removeEquipment(EquipmentDTO equipmentDTO) {
        if(CommonUtil.isEmpty(equipmentDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除体育器材信息
        if(equipmentMapper.deleteByPrimaryKey(equipmentDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.EQUIPMENT_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }
}
