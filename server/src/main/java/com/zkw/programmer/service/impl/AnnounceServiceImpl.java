package com.zkw.programmer.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zkw.programmer.bean.CodeMsg;
import com.zkw.programmer.dao.AnnounceMapper;
import com.zkw.programmer.dao.UserMapper;
import com.zkw.programmer.domain.Announce;
import com.zkw.programmer.domain.AnnounceExample;
import com.zkw.programmer.domain.User;
import com.zkw.programmer.dto.AnnounceDTO;
import com.zkw.programmer.dto.PageDTO;
import com.zkw.programmer.dto.ResponseDTO;
import com.zkw.programmer.dto.UserDTO;
import com.zkw.programmer.service.IAnnounceService;
import com.zkw.programmer.utils.CommonUtil;
import com.zkw.programmer.utils.CopyUtil;
import com.zkw.programmer.utils.UuidUtil;
import com.zkw.programmer.utils.ValidateEntityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class AnnounceServiceImpl implements IAnnounceService {

    @Resource
    private AnnounceMapper announceMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 分页获取公告数据
     * @param pageDTO
     * @return
     */
    @Override
    public ResponseDTO<PageDTO<AnnounceDTO>> getAnnounceListByPage(PageDTO<AnnounceDTO> pageDTO) {
        AnnounceExample announceExample = new AnnounceExample();
        // 判断是否进行关键字搜索
        AnnounceDTO searchAnnounceDTO = pageDTO.getSearchEntity();
        AnnounceExample.Criteria criteria = announceExample.createCriteria();
        if(!CommonUtil.isEmpty(searchAnnounceDTO.getContent())) {
            criteria.andContentLike("%" + searchAnnounceDTO.getContent() + "%");
        }
        // 不知道当前页多少，默认为第一页
        if(pageDTO.getPage() == null){
            pageDTO.setPage(1);
        }
        if(pageDTO.getSize() == null) {
            pageDTO.setSize(5);
        }
        announceExample.setOrderByClause("create_time desc");
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getSize());
        // 分页查出公告数据
        List<Announce> announceList = announceMapper.selectByExample(announceExample);
        PageInfo<Announce> pageInfo = new PageInfo<>(announceList);
        // 获取数据的总数
        pageDTO.setTotal(pageInfo.getTotal());
        // 将domain类型数据  转成 DTO类型数据
        List<AnnounceDTO> announceDTOList = CopyUtil.copyList(announceList, AnnounceDTO.class);
        for(AnnounceDTO announceDTO : announceDTOList) {
            User user = userMapper.selectByPrimaryKey(announceDTO.getUserId());
            announceDTO.setUserDTO(CopyUtil.copy(user, UserDTO.class));
        }
        pageDTO.setList(announceDTOList);
        return ResponseDTO.success(pageDTO);
    }

    /**
     * 保存公告数据(添加、修改)
     * @param announceDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> saveAnnounce(AnnounceDTO announceDTO) {
        // 进行统一表单验证
        CodeMsg validate = ValidateEntityUtil.validate(announceDTO);
        if(!validate.getCode().equals(CodeMsg.SUCCESS.getCode())){
            return ResponseDTO.errorByMsg(validate);
        }
        Announce announce = CopyUtil.copy(announceDTO, Announce.class);
        if(CommonUtil.isEmpty(announce.getId())){
            // id为空 说明是添加数据
            // 生成8位id
            announce.setId(UuidUtil.getShortUuid());
            announce.setCreateTime(new Date());
            // 添加数据到数据库
            if(announceMapper.insertSelective(announce) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.ANNOUNCE_ADD_ERROR);
            }
        }else{
            // id不为空 说明是修改数据
            // 修改数据库中数据
            if(announceMapper.updateByPrimaryKeySelective(announce) == 0){
                return ResponseDTO.errorByMsg(CodeMsg.ANNOUNCE_EDIT_ERROR);
            }
        }
        return ResponseDTO.successByMsg(true, "保存成功！");
    }

    /**
     * 删除公告数据
     * @param announceDTO
     * @return
     */
    @Override
    public ResponseDTO<Boolean> removeAnnounce(AnnounceDTO announceDTO) {
        if(CommonUtil.isEmpty(announceDTO.getId())){
            return ResponseDTO.errorByMsg(CodeMsg.DATA_ERROR);
        }
        // 删除公告信息
        if(announceMapper.deleteByPrimaryKey(announceDTO.getId()) == 0){
            return ResponseDTO.errorByMsg(CodeMsg.ANNOUNCE_DELETE_ERROR);
        }
        return ResponseDTO.successByMsg(true, "删除成功！");
    }
}
