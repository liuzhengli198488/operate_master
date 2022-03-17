package com.gys.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gys.entity.GaiaProductBusiness;
import com.gys.entity.wk.dto.*;
import com.gys.entity.wk.entity.GaiaMedicinalQualityRecord;
import com.gys.entity.wk.vo.GaiaMedicinalQualityRecordVo;
import com.gys.entity.wk.vo.GetProductThirdlyOutData;
import com.gys.mapper.GaiaMedicinalQualityRecordMapper;
import com.gys.mapper.GaiaProductBusinessMapper;
import com.gys.service.MedicinalQualityRecordService;
import com.gys.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: tzh
 * @Date: 2021/12/30 15:44
 * @Description: MedicinalQualityRecordServiceImpl
 * @Version 1.0.0
 */
@Service
@Slf4j
public class MedicinalQualityRecordServiceImpl implements MedicinalQualityRecordService {
    @Resource
    private GaiaMedicinalQualityRecordMapper medicinalQualityRecordMapper;

    @Resource
    private GaiaProductBusinessMapper gaiaProductBusinessMapper;

    @Override
    public void add(GaiaMedicinalQualityRecord record) {
        medicinalQualityRecordMapper.insertSelective(record);
    }

    @Override
    public PageInfo<GaiaMedicinalQualityRecordVo> searchRecords(MedicinalQualityRecordQueryDto dto) {
        PageHelper.startPage(dto.getPageNum(),dto.getPageSize());
        List list = medicinalQualityRecordMapper.searchRecords(dto);
        if(CollectionUtils.isEmpty(list)){
            return new PageInfo<>();
        }
        PageInfo<GaiaMedicinalQualityRecordVo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void del(MedicinalQualityRecordDelDto dto) {
          medicinalQualityRecordMapper.del(dto);
    }

    @Override
    public PageInfo<GetProductThirdlyOutData> queryProFourthly(GetProductThirdlyInData inData) {
        List<GetProductThirdlyOutData> dataMap = new ArrayList<>();
        PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        if (ObjectUtil.isEmpty(inData.getMateType())) {
            inData.setMateType("0");
        }
        if (StringUtils.isNotEmpty(inData.getContent())) {
            inData.setProArr(inData.getContent().split("\\s+ |\\s+|,"));
            System.out.println(inData.getContent());
        }
        /**
         * 1.门店用 2.加盟商下查询商品
         */
        switch (inData.getType()) {
            case "1":
                dataMap = this.gaiaProductBusinessMapper.queryProFourthlyByStore(inData);
                break;
            case "2":
                dataMap = this.gaiaProductBusinessMapper.queryProFourthlyByClinet(inData);
                break;
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(dataMap)) {
            pageInfo = new PageInfo<>(dataMap);
        } else {
            pageInfo = new PageInfo<>();
        }

        return pageInfo;
    }

    @Override
    public GaiaMedicinalQualityRecord getOneRecord(MedicinalQualityRecordDetailDto dto) {
        GaiaMedicinalQualityRecord record = medicinalQualityRecordMapper.selectByPrimaryKey(dto.getId());
        return record;
    }

    @Override
    public void update(List<MedicinalQualityRecordUpdateDto> dtos) {
        medicinalQualityRecordMapper.updateList(dtos);
    }

    @Override
    public void addRecords(List<MedicinalQualityRecordDto> dtos) {
       // medicinalQualityRecordMapper.insertList()
        medicinalQualityRecordMapper.addList(dtos);
    }

    @Override
    public GaiaMedicinalQualityRecord selectOne(Long id) {
        return medicinalQualityRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateById(MedicinalQualityRecordUpdateDto dto) {
        GaiaMedicinalQualityRecord record=new GaiaMedicinalQualityRecord();
        BeanCopyUtils.copyPropertiesIgnoreNull(dto,record);
        medicinalQualityRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<GaiaMedicinalQualityRecord> selectList(List<Long> ids) {
        return medicinalQualityRecordMapper.selectList(ids);
    }
    static List<GaiaProductBusiness>  list = new ArrayList<>();
    @Override
    public String test() {

        while (true){
            List<GaiaProductBusiness>  outData = gaiaProductBusinessMapper.getTestList();
            for (GaiaProductBusiness productBusiness : outData){
                System.out.println(productBusiness);
                GaiaProductBusiness business = new GaiaProductBusiness();
                BeanCopyUtils.copyPropertiesIgnoreNull(productBusiness,business);
                System.out.println("-------------"+business);
                String str = new String();
                str = str + "-----123你好時間你好時間你好時間你好時間你好時間你好時間你好時間你好時間";
                System.out.println(str);
                String s = new String();
                s = s + str;
                System.out.println(s);
            }
            list.addAll(outData);
        }
    }
}
