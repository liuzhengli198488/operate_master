package com.gys.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.util.StrUtil;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.entity.SysDic;
import com.gys.mapper.SysDicMapper;
import com.gys.service.ISysDicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 系统码表，加盟商 门店编号可以不填写，不填写情况下表示全局级别的码表 服务实现类
 * </p>
 *
 * @author flynn
 * @since 2022-01-29
 */
@Service
@Transactional(rollbackFor = Exception.class)//事务回滚
public class SysDicServiceImpl implements ISysDicService {
    @Autowired
    private SysDicMapper sysDicMapper;

    @Override
    public List<SysDic> getSysDicListByTypeCode(String client, String stoCode, String typeCode) {
        List<SysDic> resList = new ArrayList<>();
        Example example = new Example(SysDic.class);
        Example.Criteria criteria = example.createCriteria();
        criteria
                .andEqualTo("typeCode", typeCode);
        if (StrUtil.isNotBlank(client)) {
            criteria.andEqualTo("client", client);
        }
        if (StrUtil.isNotBlank(stoCode)) {
            criteria.andEqualTo("stoCode", stoCode);
        }
        example.setOrderByClause(" KYE_ORDER");
        List<SysDic> sysDics = sysDicMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(sysDics)) {
            resList = sysDics;
        }
        return resList;
    }
}
