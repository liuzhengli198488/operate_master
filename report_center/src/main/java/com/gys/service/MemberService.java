package com.gys.service;

import com.gys.common.data.PageInfo;
import com.gys.entity.InData;
import com.gys.entity.data.member.ActivateMemberCardOutput;

import java.util.List;

/**
 * @author 胡鑫鑫
 */
public interface MemberService {

    PageInfo<ActivateMemberCardOutput> getActivateMemberCard(InData inData);
    List<ActivateMemberCardOutput> getActivateMemberCardCSV(InData inData);

}
