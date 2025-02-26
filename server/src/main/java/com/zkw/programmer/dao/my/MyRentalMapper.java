package com.zkw.programmer.dao.my;

import org.apache.ibatis.annotations.Param;

import java.util.Map;


public interface MyRentalMapper {

    // 根据时间范围获取租借数
    Integer getRentalTotalByDate(@Param("queryMap") Map<String, Object> queryMap);
}
