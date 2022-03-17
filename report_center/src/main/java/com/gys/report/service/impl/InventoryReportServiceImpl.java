package com.gys.report.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.gys.common.exception.BusinessException;
import com.gys.mapper.GaiaSdPhysicalCountDiffMapper;
import com.gys.mapper.GaiaStoreDataMapper;
import com.gys.report.entity.*;
import com.gys.report.service.InventoryReportService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author xiaoyuan on 2020/9/8
 */
@Service
public class InventoryReportServiceImpl implements InventoryReportService {

    @Autowired
    private GaiaSdPhysicalCountDiffMapper physicalCountDiffMapper;

    @Autowired
    private GaiaStoreDataMapper gaiaStoreDataMapper;


    @Override
    public List<DifferenceResultQueryOutData> getDifferenceResultQuery(DifferenceResultQueryInVo inData) {
        List<DifferenceResultQueryOutData> outData = this.physicalCountDiffMapper.getDifferenceResultQueryByAll(inData);
        if (CollUtil.isNotEmpty(outData)) {
            IntStream.range(0, outData.size()).forEach(i -> (outData.get(i)).setIndex(i + 1));
        }
        return outData;
    }


    @Override
    public List<DifferenceResultDetailedQueryOutData> getDifferenceResultDetailedQuery(DifferenceResultQueryInVo inData) {
        //输入盘点单号 商品编码 可以不输入时间
        if (ObjectUtil.isEmpty(inData.getStartDate()) && StrUtil.isEmpty(inData.getGspcVoucherId()) ) {
            throw new BusinessException("盘点单号，起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate()) && StrUtil.isEmpty(inData.getGspcVoucherId()) ) {
            throw new BusinessException("盘点单号，结束日期不能为空！");
        }
        if(StringUtils.isNotEmpty(inData.getProCode())){//多个编码查询
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        //3个条件一起输入
        if ((StrUtil.isNotBlank(inData.getGspcVoucherId()) && StrUtil.isNotBlank(inData.getProCode()) && StrUtil.isNotBlank(inData.getBrSite())) || (StrUtil.isNotBlank(inData.getGspcVoucherId()) && StrUtil.isNotBlank(inData.getBrSite())) ){

            if (StrUtil.isNotBlank(inData.getBrSite())) {
                //门店--配置中心ID
                GaiaDcData dcData = this.physicalCountDiffMapper.getDcData(inData.getClient(), inData.getBrSite());
                //拿上的不是门店的ID   而是仓库的ID  获取仓库对象的数据  门店
                if (ObjectUtil.isEmpty(dcData)) {
                    //10000
                    StoreOutData storeData = this.gaiaStoreDataMapper.getStoreData(inData.getClient(), inData.getBrSite());

                    if (ObjectUtil.isNotEmpty(storeData)) {
                        //加盟商门店
                        //List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getDifferenceResultDetailedQueryByClientBrId(inData);
                        List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getGoodsNumber(inData);
                        return outData;
                    }

                    //拿上的是门店的ID　　　获取门店数据 仓库
                }else {
                    //102
                    List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getDifferenceResultDetailedQueryByClientBrCode(inData);

                    return outData;
                }
            }
        }

        if (StrUtil.isNotBlank(inData.getBrSite())) {
            //门店--配置中心ID
            GaiaDcData dcData = this.physicalCountDiffMapper.getDcData(inData.getClient(), inData.getBrSite());
            //拿上的不是门店的ID   而是仓库的ID  获取仓库对象的数据  门店
            if (ObjectUtil.isEmpty(dcData)) {
                //10000
                StoreOutData storeData = this.gaiaStoreDataMapper.getStoreData(inData.getClient(), inData.getBrSite());

                if (ObjectUtil.isNotEmpty(storeData)) {
                    //加盟商门店
                    //List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getDifferenceResultDetailedQueryByClientBrId(inData);
                    List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getGoodsNumber(inData);
                    return outData;
                }

                //拿上的是门店的ID　　　获取门店数据 仓库
            }else {
                //102
                List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getDifferenceResultDetailedQueryByClientBrCode(inData);

                return outData;
            }
        }


        //只输入盘点单号
        if (StrUtil.isNotBlank(inData.getGspcVoucherId())) {
            //只查询当前加盟商下
            if (ObjectUtil.isEmpty(inData.getStartDate())) {
                throw new BusinessException("起始日期不能为空！");
            }
            if (ObjectUtil.isEmpty(inData.getEndDate())  ) {
                throw new BusinessException("结束日期不能为空！");
            }

            //根据盘点单号查询
            List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getGoodsNumber(inData);

            return outData;
        }
        //所有  时间走这个逻辑
        List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getDifferenceResultDetailedQuery(inData);
        return outData;
    }


    @Override
    public List<InventoryDocumentsOutData> inventoryDocumentQuery(DifferenceResultQueryInVo inData) {
        List<InventoryDocumentsOutData> outData = this.physicalCountDiffMapper.inventoryDocumentQueryByBrId(inData);
        if (CollUtil.isNotEmpty(outData)) {
            IntStream.range(0, outData.size()).forEach(i -> (outData.get(i)).setIndex(i + 1));
        }
        return outData;

    }

    @Override
    public List<DifferenceResultDetailedQueryOutData> getInventoryDataDetail(DifferenceResultQueryInVo inData) {
        if (ObjectUtil.isEmpty(inData.getStartDate()) && StrUtil.isEmpty(inData.getGspcVoucherId()) ) {
            throw new BusinessException("盘点单号，起始日期不能为空！");
        }
        if (ObjectUtil.isEmpty(inData.getEndDate()) && StrUtil.isEmpty(inData.getGspcVoucherId()) ) {
            throw new BusinessException("盘点单号，结束日期不能为空！");
        }
        if(StringUtils.isNotEmpty(inData.getProCode())){
            inData.setProArr(inData.getProCode().split("\\s+ |\\s+|,"));
        }
        List<DifferenceResultDetailedQueryOutData> outData = this.physicalCountDiffMapper.getGoodsNumber(inData);
        return outData;
    }
}
