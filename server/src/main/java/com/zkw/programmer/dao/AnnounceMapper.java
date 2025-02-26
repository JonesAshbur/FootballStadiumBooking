package com.zkw.programmer.dao;

import com.zkw.programmer.domain.Announce;
import com.zkw.programmer.domain.AnnounceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AnnounceMapper {
    int countByExample(AnnounceExample example);

    int deleteByExample(AnnounceExample example);

    int deleteByPrimaryKey(String id);

    int insert(Announce record);

    int insertSelective(Announce record);

    List<Announce> selectByExample(AnnounceExample example);

    Announce selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Announce record, @Param("example") AnnounceExample example);

    int updateByExample(@Param("record") Announce record, @Param("example") AnnounceExample example);

    int updateByPrimaryKeySelective(Announce record);

    int updateByPrimaryKey(Announce record);
}
