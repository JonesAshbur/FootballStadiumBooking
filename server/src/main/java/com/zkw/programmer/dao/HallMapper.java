package com.zkw.programmer.dao;

import com.zkw.programmer.domain.Hall;
import com.zkw.programmer.domain.HallExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HallMapper {
    int countByExample(HallExample example);

    int deleteByExample(HallExample example);

    int deleteByPrimaryKey(String id);

    int insert(Hall record);

    int insertSelective(Hall record);

    List<Hall> selectByExample(HallExample example);

    Hall selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Hall record, @Param("example") HallExample example);

    int updateByExample(@Param("record") Hall record, @Param("example") HallExample example);

    int updateByPrimaryKeySelective(Hall record);

    int updateByPrimaryKey(Hall record);
}
