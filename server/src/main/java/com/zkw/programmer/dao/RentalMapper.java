package com.zkw.programmer.dao;

import com.zkw.programmer.domain.Rental;
import com.zkw.programmer.domain.RentalExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RentalMapper {
    int countByExample(RentalExample example);

    int deleteByExample(RentalExample example);

    int deleteByPrimaryKey(String id);

    int insert(Rental record);

    int insertSelective(Rental record);

    List<Rental> selectByExample(RentalExample example);

    Rental selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Rental record, @Param("example") RentalExample example);

    int updateByExample(@Param("record") Rental record, @Param("example") RentalExample example);

    int updateByPrimaryKeySelective(Rental record);

    int updateByPrimaryKey(Rental record);
}
