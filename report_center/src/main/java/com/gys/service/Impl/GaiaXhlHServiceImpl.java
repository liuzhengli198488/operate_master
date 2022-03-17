package com.gys.service.Impl;

import com.gys.common.enums.XhlTypeEnum;
import com.gys.entity.GaiaCalDate;
import com.gys.entity.GaiaXhlD;
import com.gys.entity.GaiaXhlH;
import com.gys.entity.data.XhlDCond;
import com.gys.mapper.GaiaXhlDMapper;
import com.gys.mapper.GaiaXhlHMapper;
import com.gys.service.GaiaCalDateService;
import com.gys.service.GaiaXhlHService;
import com.gys.util.BeanCopyUtils;
import com.gys.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.gys.common.enums.XhlTypeEnum.SEND_XHL;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/9/1 15:01
 */
@Service("gaiaXhlHService")
public class GaiaXhlHServiceImpl implements GaiaXhlHService {
    @Resource
    private GaiaXhlHMapper gaiaXhlHMapper;
    @Resource
    private GaiaXhlDMapper gaiaXhlDMapper;
    @Resource
    private GaiaCalDateService gaiaCalDateService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveStoreXhl(String client, Date tjDate,Map<String, BigDecimal> industryAverage) {
        //统计门店下货率并保存
        XhlDCond cond = new XhlDCond();
        cond.setClient(client);
        cond.setDnDate(DateUtil.formatDate(tjDate, "yyyyMMdd"));
        List<GaiaXhlD> storeXhlList = gaiaXhlDMapper.getStoreXhlByDate(cond);
        //去除铺货的下货率
        List<GaiaXhlD> storeXHlLess = gaiaXhlDMapper.getStoreXhlByDateWithOutShopGoods(cond);
        if(CollectionUtils.isEmpty(storeXhlList)|| storeXhlList.size() == 0){
            return;
        }
        //合并两个list
        if(!CollectionUtils.isEmpty(storeXHlLess)){
            for (GaiaXhlD gaiaXhlD :storeXhlList){
                for (GaiaXhlD gaiaXhlDLess:storeXHlLess){
                    if(gaiaXhlD.getClient().equals(gaiaXhlDLess.getClient())&&gaiaXhlD.getStoreId().equals(gaiaXhlDLess.getStoreId())&&gaiaXhlD.getTjDate().equals(gaiaXhlDLess.getTjDate())){
                        BeanCopyUtils.copyPropertiesIgnoreNull(gaiaXhlDLess,gaiaXhlD);
                    }
                }
            }
        }
        saveStoreXhlList(storeXhlList);
        //汇总生成加盟商维度下货率  行业平均值 INDUSTRY AVERAGE
        saveClientXhl(client, tjDate, industryAverage);
    }

    private void saveClientXhl(String client, Date tjDate, Map<String, BigDecimal> industryAverage) {
        XhlDCond cond = new XhlDCond();
        cond.setClient(client);
        cond.setDnDate(DateUtil.formatDate(tjDate, "yyyy-MM-dd"));
        //统计当天的
        GaiaXhlD countXhl = gaiaXhlDMapper.countXhl(cond);
        if (countXhl == null) {
            return;
        }
        List<GaiaXhlH> xhlHList = transformXhlHList(countXhl, industryAverage);
        for (GaiaXhlH gaiaXhlH : xhlHList) {
            GaiaXhlH unique = gaiaXhlHMapper.getUnique(gaiaXhlH);
            if (unique == null) {
                gaiaXhlHMapper.add(gaiaXhlH);
            } else {
                //周次、数量、金额、品项数据有差异时更新数据库记录
                if (!unique.getWeekNum().equals(gaiaXhlH.getWeekNum())
                        || unique.getUpQuantity().compareTo(gaiaXhlH.getUpQuantity()) != 0
                        || unique.getDownQuantity().compareTo(gaiaXhlH.getDownQuantity()) != 0
                        || unique.getUpProductNum().compareTo(gaiaXhlH.getUpQuantity()) != 0
                        || unique.getDownProductNum().compareTo(gaiaXhlH.getDownQuantity()) != 0
                        || unique.getUpAmount().compareTo(gaiaXhlH.getUpAmount()) != 0
                        || unique.getDownAmount().compareTo(gaiaXhlH.getDownAmount()) != 0

                        || unique.getUpQuantityLess().compareTo(gaiaXhlH.getUpQuantityLess()) != 0
                        || unique.getDownQuantityLess().compareTo(gaiaXhlH.getDownQuantityLess()) != 0
                        || unique.getUpProductNumLess().compareTo(gaiaXhlH.getUpQuantityLess()) != 0
                        || unique.getDownProductNumLess().compareTo(gaiaXhlH.getDownQuantityLess()) != 0
                        || unique.getUpAmountLess().compareTo(gaiaXhlH.getUpAmountLess()) != 0
                        || unique.getDownAmountLess().compareTo(gaiaXhlH.getDownAmountLess()) != 0
                        || unique.getIndustryAverage().compareTo(gaiaXhlH.getIndustryAverage()) != 0
                ) {
                    fillUniqueXhlH(unique, gaiaXhlH);
                    gaiaXhlHMapper.update(unique);
                }
            }
        }
    }

    private void fillUniqueXhlH(GaiaXhlH unique, GaiaXhlH gaiaXhlH) {
        unique.setUpQuantity(gaiaXhlH.getUpQuantity());
        unique.setUpQuantityLess(gaiaXhlH.getUpQuantityLess());
        unique.setDownQuantity(gaiaXhlH.getDownQuantity());
        unique.setDownQuantityLess(gaiaXhlH.getDownQuantityLess());
        if (BigDecimal.ZERO.compareTo(unique.getDownQuantity()) < 0) {
            unique.setQuantityRate(unique.getUpQuantity().divide(unique.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        if (BigDecimal.ZERO.compareTo(unique.getDownQuantityLess()) < 0) {
            unique.setQuantityRateLess(unique.getUpQuantityLess().divide(unique.getDownQuantityLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        unique.setUpProductNum(gaiaXhlH.getUpProductNum());
        unique.setUpProductNumLess(gaiaXhlH.getUpProductNumLess());
        unique.setDownProductNum(gaiaXhlH.getDownProductNum());
        unique.setDownProductNumLess(gaiaXhlH.getDownProductNumLess());
        if (BigDecimal.ZERO.compareTo(unique.getDownProductNum()) < 0) {
            unique.setProductRate(unique.getUpProductNum().divide(unique.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        if (BigDecimal.ZERO.compareTo(unique.getDownProductNumLess()) < 0) {
            unique.setProductRateLess(unique.getUpProductNumLess().divide(unique.getDownProductNumLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        unique.setUpAmount(gaiaXhlH.getUpAmount());
        unique.setUpAmountLess(gaiaXhlH.getUpAmountLess());
        unique.setDownAmount(gaiaXhlH.getDownAmount());
        unique.setDownAmountLess(gaiaXhlH.getDownAmountLess());
        unique.setIndustryAverage(gaiaXhlH.getIndustryAverage());
        if (BigDecimal.ZERO.compareTo(unique.getDownAmount()) < 0) {
            unique.setAmountRate(unique.getUpAmount().divide(unique.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        if (BigDecimal.ZERO.compareTo(unique.getDownAmountLess()) < 0) {
            unique.setAmountRateLess(unique.getUpAmountLess().divide(unique.getDownAmountLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        }
        unique.setUpdateTime(new Date());
        unique.setWeekNum(gaiaXhlH.getWeekNum());
    }

    private List<GaiaXhlH> transformXhlHList(GaiaXhlD countXhl,Map<String, BigDecimal> industryAverage) {
        List<GaiaXhlH> xhlHList = new ArrayList<>();
        // finalIndustryAverage 最终下货率 billingIndustryAverage 开单配货率 DepotIndustryAverage 仓库发货率
       // 统计类型 1-配货 2-发货 3-最终
        //Map<String, BigDecimal> industryAverage = dropRateService.getIndustryAverage(countXhl.getClient());
        BigDecimal finalIndustryAverage = industryAverage.get("finalIndustryAverage");
        BigDecimal billingIndustryAverage = industryAverage.get("billingIndustryAverage");
        BigDecimal depotIndustryAverage = industryAverage.get("depotIndustryAverage");
        GaiaXhlH sendXhl = fillGaiaXhlH(countXhl, SEND_XHL,billingIndustryAverage);
        xhlHList.add(sendXhl);
        GaiaXhlH gzXhl = fillGaiaXhlH(countXhl, XhlTypeEnum.GZ_XHL,depotIndustryAverage);
        xhlHList.add(gzXhl);
        GaiaXhlH finalXhl = fillGaiaXhlH(countXhl, XhlTypeEnum.FINAL_XHL,finalIndustryAverage);
        xhlHList.add(finalXhl);
        return xhlHList;
    }

    private GaiaXhlH fillGaiaXhlH(GaiaXhlD countXhl, XhlTypeEnum xhlType, BigDecimal industryAverage) {
        GaiaXhlH gaiaXhlH = new GaiaXhlH();
        gaiaXhlH.setClient(countXhl.getClient());
        gaiaXhlH.setTjType(xhlType.type);
        gaiaXhlH.setTjDate(countXhl.getTjDate());
        GaiaCalDate gaiaCalDate = gaiaCalDateService.getByDate(DateUtil.formatDate(gaiaXhlH.getTjDate(), "yyyyMMdd"));
        if (gaiaCalDate != null) {
            gaiaXhlH.setYearName(gaiaCalDate.getGcdYear());
            gaiaXhlH.setWeekNum(Integer.valueOf(gaiaCalDate.getGcdWeek()));
        } else {
            gaiaXhlH.setYearName(DateUtil.formatDate(gaiaXhlH.getTjDate(), "yyyy"));
            gaiaXhlH.setWeekNum(DateUtil.weekOfYear(gaiaXhlH.getTjDate()));
        }
        switch (xhlType) {
            //配货下货率
            case SEND_XHL:
                gaiaXhlH.setUpQuantity(countXhl.getDnNum());
                gaiaXhlH.setUpQuantityLess(countXhl.getDnNumLess());
                gaiaXhlH.setDownQuantity(countXhl.getOrderNum());
                gaiaXhlH.setDownQuantityLess(countXhl.getOrderNumLess());
                gaiaXhlH.setUpProductNum(countXhl.getDnProductNum());
                gaiaXhlH.setUpProductNumLess(countXhl.getDnProductNumLess());
                gaiaXhlH.setDownProductNum(countXhl.getOrderProductNum());
                gaiaXhlH.setDownProductNumLess(countXhl.getOrderProductNumLess());
                gaiaXhlH.setUpAmount(countXhl.getDnAmount().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setUpAmountLess(countXhl.getDnAmountLess().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setDownAmount(countXhl.getOrderAmount().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setDownAmountLess(countXhl.getOrderAmountLess().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setIndustryAverage(industryAverage);
                break;
            //过账下货率
            case GZ_XHL:
                gaiaXhlH.setUpQuantity(countXhl.getGzNum());
                gaiaXhlH.setUpQuantityLess(countXhl.getGzNumLess());
                gaiaXhlH.setDownQuantity(countXhl.getSendNum());
                gaiaXhlH.setDownQuantityLess(countXhl.getSendNumLess());
                gaiaXhlH.setUpProductNum(countXhl.getGzProductNum());
                gaiaXhlH.setUpProductNumLess(countXhl.getGzProductNumLess());
                gaiaXhlH.setDownProductNum(countXhl.getSendProductNum());
                gaiaXhlH.setDownProductNumLess(countXhl.getSendProductNumLess());
                gaiaXhlH.setUpAmount(countXhl.getGzAmount().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setUpAmountLess(countXhl.getGzAmountLess().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setDownAmount(countXhl.getSendAmount().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setDownAmountLess(countXhl.getSendAmountLess().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setIndustryAverage(industryAverage);
                break;
            // 最终下货率
            case FINAL_XHL:
                gaiaXhlH.setUpQuantity(countXhl.getGzNum());
                gaiaXhlH.setUpQuantityLess(countXhl.getGzNumLess());
                gaiaXhlH.setDownQuantity(countXhl.getFinalNum());
                gaiaXhlH.setDownQuantityLess(countXhl.getFinalNumLess());
                gaiaXhlH.setUpProductNum(countXhl.getGzProductNum());
                gaiaXhlH.setUpProductNumLess(countXhl.getGzProductNumLess());
                gaiaXhlH.setDownProductNum(countXhl.getFinalProductNum());
                gaiaXhlH.setDownProductNumLess(countXhl.getFinalProductNumLess());
                gaiaXhlH.setUpAmount(countXhl.getGzAmount().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setUpAmountLess(countXhl.getGzAmountLess().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setDownAmount(countXhl.getFinalOrderAmount().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setDownAmountLess(countXhl.getFinalOrderAmountLess().setScale(0, RoundingMode.HALF_UP));
                gaiaXhlH.setIndustryAverage(industryAverage);
                break;
            default:
                break;
        }
        if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownQuantity()) < 0) {
            gaiaXhlH.setQuantityRate(gaiaXhlH.getUpQuantity().divide(gaiaXhlH.getDownQuantity(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        }
        if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownQuantityLess()) < 0) {
            gaiaXhlH.setQuantityRateLess(gaiaXhlH.getUpQuantityLess().divide(gaiaXhlH.getDownQuantityLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        }
        if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownProductNum()) < 0) {
            gaiaXhlH.setProductRate(gaiaXhlH.getUpProductNum().divide(gaiaXhlH.getDownProductNum(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        }
        if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownProductNumLess()) < 0) {
            gaiaXhlH.setProductRate(gaiaXhlH.getUpProductNumLess().divide(gaiaXhlH.getDownProductNumLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        }
        if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownAmount()) < 0) {
            gaiaXhlH.setAmountRate(gaiaXhlH.getUpAmount().divide(gaiaXhlH.getDownAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        }
        if (BigDecimal.ZERO.compareTo(gaiaXhlH.getDownAmountLess()) < 0) {
            gaiaXhlH.setAmountRate(gaiaXhlH.getUpAmountLess().divide(gaiaXhlH.getDownAmountLess(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
        }
        return gaiaXhlH;
    }

    private void saveStoreXhlList(List<GaiaXhlD> storeXhlList) {
        for (GaiaXhlD gaiaXhlD : storeXhlList) {
            GaiaXhlD unique = gaiaXhlDMapper.getUnique(gaiaXhlD);
            if (unique == null) {
                gaiaXhlDMapper.add(gaiaXhlD);
            } else {
                //判断是否存在数据更新
                if (    gaiaXhlD.getSendNum().compareTo(unique.getSendNum()) != 0
                        || gaiaXhlD.getGzNum().compareTo(unique.getGzNum()) != 0
                        || gaiaXhlD.getFinalNum().compareTo(unique.getFinalNum()) != 0
                        || gaiaXhlD.getSendProductNum().compareTo(unique.getSendProductNum()) != 0
                        || gaiaXhlD.getDnProductNum().compareTo(unique.getDnProductNum()) != 0
                        || gaiaXhlD.getDnNum().compareTo(unique.getDnNum()) != 0
                        || gaiaXhlD.getDnAmount().compareTo(unique.getDnAmount()) != 0
                        || gaiaXhlD.getGzProductNum().compareTo(unique.getGzProductNum()) != 0
                        || gaiaXhlD.getFinalProductNum().compareTo(unique.getFinalProductNum()) != 0
                        || gaiaXhlD.getSendAmount().compareTo(unique.getSendAmount()) != 0
                        || gaiaXhlD.getGzAmount().compareTo(unique.getGzAmount()) != 0
                        || gaiaXhlD.getFinalOrderAmount().compareTo(unique.getFinalOrderAmount()) != 0

                        || (gaiaXhlD.getSendNumLess()==null?BigDecimal.ZERO:gaiaXhlD.getSendNumLess()).compareTo(unique.getSendNumLess())!=0
                        || (gaiaXhlD.getGzNumLess()==null?BigDecimal.ZERO:gaiaXhlD.getGzNumLess()).compareTo(unique.getGzNumLess())!=0
                        || (gaiaXhlD.getFinalNumLess()==null?BigDecimal.ZERO:gaiaXhlD.getFinalNumLess()).compareTo(unique.getFinalNumLess()) != 0
                        || (gaiaXhlD.getSendProductNumLess()==null?BigDecimal.ZERO: gaiaXhlD.getSendProductNumLess()).compareTo(unique.getSendProductNumLess()) != 0
                        || (gaiaXhlD.getDnProductNumLess()==null?BigDecimal.ZERO:gaiaXhlD.getDnProductNumLess()).compareTo(unique.getDnProductNumLess()) != 0
                        || (gaiaXhlD.getDnNumLess()==null?BigDecimal.ZERO:gaiaXhlD.getDnNumLess()).compareTo(unique.getDnNumLess()) != 0
                        || (gaiaXhlD.getDnAmountLess()==null?BigDecimal.ZERO:gaiaXhlD.getDnAmountLess()).compareTo(unique.getDnAmountLess()) != 0
                        || (gaiaXhlD.getGzProductNumLess()==null?BigDecimal.ZERO:gaiaXhlD.getGzProductNumLess()).compareTo(unique.getGzProductNumLess()) != 0
                        || (gaiaXhlD.getFinalProductNumLess()==null?BigDecimal.ZERO:gaiaXhlD.getFinalProductNumLess()).compareTo(unique.getFinalProductNumLess())!= 0
                        || (gaiaXhlD.getSendAmountLess()==null?BigDecimal.ZERO:gaiaXhlD.getSendAmountLess()).compareTo(unique.getSendAmountLess()) != 0
                        || (gaiaXhlD.getGzAmountLess()==null?BigDecimal.ZERO:gaiaXhlD.getGzAmountLess()).compareTo(unique.getGzAmountLess()) != 0
                        || (gaiaXhlD.getFinalOrderAmountLess()==null?BigDecimal.ZERO:gaiaXhlD.getFinalOrderAmountLess()).compareTo(unique.getFinalOrderAmountLess()) != 0
                ) {
                    fillUniqueXhlD(unique, gaiaXhlD);
                    gaiaXhlDMapper.update(unique);
                }
            }
        }
    }

    private void fillUniqueXhlD(GaiaXhlD unique, GaiaXhlD gaiaXhlD) {
        BeanCopyUtils.copyPropertiesIgnoreNull(gaiaXhlD,unique);
        unique.setUpdateTime(new Date());
    }

}
