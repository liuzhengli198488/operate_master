package com.gys.mapper;

import com.gys.common.base.BaseMapper;
import com.gys.entity.GaiaSdMemberCard;
import com.gys.entity.InData;
import com.gys.entity.data.member.ActivateMemberCardOutput;
import com.gys.entity.data.member.ActivateMemberCardTotal;

import java.util.List;

public interface GaiaSdMemberCardMapper extends BaseMapper<GaiaSdMemberCard> {
    List<ActivateMemberCardOutput> getActivateMemberCard(InData inData);

    ActivateMemberCardTotal getActivateMemberCardTotal(InData inData);
}