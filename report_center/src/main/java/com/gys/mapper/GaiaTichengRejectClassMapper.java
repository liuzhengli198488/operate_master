package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaTichengRejectClass;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GaiaTichengRejectClassMapper extends BaseMapper<GaiaTichengRejectClass> {

    List<String> selectClass(@Param("pId") Integer pId);

}