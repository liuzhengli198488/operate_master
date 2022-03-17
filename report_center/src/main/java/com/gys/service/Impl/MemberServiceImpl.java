package com.gys.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.PageInfo;
import com.gys.entity.InData;
import com.gys.entity.data.member.ActivateMemberCardOutput;
import com.gys.entity.data.member.ActivateMemberCardTotal;
import com.gys.mapper.GaiaSdMemberCardMapper;
import com.gys.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author 胡鑫鑫
 */
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {
    @Resource
    private GaiaSdMemberCardMapper memberCardMapper;

    @Override
    public PageInfo<ActivateMemberCardOutput> getActivateMemberCard(InData inData){
        PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        List<ActivateMemberCardOutput> outData = memberCardMapper.getActivateMemberCard(inData);
        PageInfo pageInfo = null;
        if (ObjectUtil.isNotEmpty(outData)) {
            IntStream.range(0, outData.size()).forEach(i -> (outData.get(i)).setIndex(i + 1));
            ActivateMemberCardTotal outTotal = memberCardMapper.getActivateMemberCardTotal(inData);
            pageInfo = new PageInfo(outData,outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    @Override
    public List<ActivateMemberCardOutput> getActivateMemberCardCSV(InData inData){
        List<ActivateMemberCardOutput> outData = memberCardMapper.getActivateMemberCard(inData);
        if (ObjectUtil.isNotEmpty(outData)) {
            IntStream.range(0, outData.size()).forEach(i -> (outData.get(i)).setIndex(i + 1));
        }
        return outData;
    }
}
