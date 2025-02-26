package com.zkw.programmer.dao.my;

import org.apache.ibatis.annotations.Param;

import java.util.Map;


public interface MyAppointmentMapper {

    // 根据时间范围获取预约数
    Integer getAppointmentTotalByDate(@Param("queryMap") Map<String, Object> queryMap);
}
