package com.zkw.programmer.dao.my;

import org.apache.ibatis.annotations.Param;


public interface MyEquipmentMapper {

    // 减少体育器材数量
    int decreaseRentalNum(@Param("num") Integer num, @Param("id") String id);

    // 增加体育器材数量
    int addRentalNum(@Param("num") Integer num, @Param("id") String id);


}
