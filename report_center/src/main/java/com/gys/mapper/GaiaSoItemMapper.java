package com.gys.mapper;
import com.gys.entity.GaiaSoItem;
import com.gys.entity.GaiaSoItemKey;
import com.gys.entity.data.approval.dto.ApprovalDto;
import com.gys.entity.data.approval.vo.GaiaSoItemVo;

import java.util.List;

public interface GaiaSoItemMapper {
    int deleteByPrimaryKey(GaiaSoItemKey key);

    int insert(GaiaSoItem record);

    int insertSelective(GaiaSoItem record);

    GaiaSoItem selectByPrimaryKey(GaiaSoItemKey key);

    int updateByPrimaryKeySelective(GaiaSoItem record);

    int updateByPrimaryKey(GaiaSoItem record);

    List<GaiaSoItemVo> getProCodes(ApprovalDto deliveryNumber);
}