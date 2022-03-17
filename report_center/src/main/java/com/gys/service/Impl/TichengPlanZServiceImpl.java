package com.gys.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.gys.annotation.CommissionReportField;
import com.gys.common.constant.CommonConstant;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.data.StoreSimpleInfoWithPlan;
import com.gys.common.data.*;
import com.gys.common.enums.StaffTypeEnum;
import com.gys.common.exception.BusinessException;
import com.gys.common.kylin.RowMapper;
import com.gys.common.response.Result;
import com.gys.entity.*;
import com.gys.entity.data.MonthPushMoney.*;
import com.gys.entity.data.MonthPushMoney.V2.*;
import com.gys.entity.data.TichengProplanStoN;
import com.gys.entity.data.commissionplan.CommissionSummaryDetailDTO;
import com.gys.entity.data.commissionplan.StoreCommissionSummary;
import com.gys.entity.data.commissionplan.StoreCommissionSummaryDO;
import com.gys.entity.data.salesSummary.UserCommissionSummaryDetail;
import com.gys.mapper.*;
import com.gys.service.TiChengCaculateService;
import com.gys.service.TichengPlanZService;
import com.gys.util.BigDecimalUtil;
import com.gys.util.CommonUtil;
import com.gys.util.CosUtils;
import com.gys.util.UtilConst;
import com.gys.util.csv.CsvClient;
import com.gys.util.csv.CustomCommissionCsvClient;
import com.gys.util.csv.dto.CsvFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings("all")
public class TichengPlanZServiceImpl implements TichengPlanZService {

    @Autowired
    private GaiaTichengPlanZMapper tichengPlanZMapper;

    @Autowired
    private GaiaTichengSaleplanZMapper tichengSaleplanZMapper;

    @Autowired
    private GaiaTichengSaleplanStoMapper tichengSaleplanStoMapper;

    @Autowired
    private GaiaTichengSaleplanMMapper tichengSaleplanMMapper;

    @Autowired
    private GaiaTichengRejectClassMapper tichengRejectClassMapper;

    @Autowired
    private GaiaTichengRejectProMapper tichengRejectProMapper;

    @Autowired
    private GaiaTichengProplanZMapper tichengProplanZMapper;

    @Autowired
    private TichengProplanBasicMapper tichengProplanBasicMapper;

    @Autowired
    private GaiaTichengProplanZNMapper tichengProplanZNMapper;

    @Autowired
    private TichengProplanSettingMapper tichengProplanSettingMapper;

    @Autowired
    private TichengProplanProNMapper tichengProplanProNMapper;

    @Autowired
    private TichengProplanStoNMapper tichengProplanStoMapper;

    @Autowired
    private GaiaTichengProplanProMapper tichengProplanProMapper;

    @Resource(name = "kylinJdbcTemplateFactory")
    private JdbcTemplate kylinJdbcTemplate;

    @Autowired
    private GaiaProductBusinessMapper gaiaProductBusinessMapper;

    @Autowired
    private GaiaProductClassMapper gaiaProductClassMapper;

    @Autowired
    private CosUtils cosUtils;

    @Resource
    private GaiaGlobalDataMapper gaiaGlobalDataMapper;

    @Resource
    private UserCommissionSummaryDetailMapper userCommissionSummaryDetailMapper;

    /**
     * 1.先查询出门店日均销售 来获取的到最大最小的毛利率
     * 2.拿毛利率和sale_D 的毛利率进行对比计算出提成
     * 3.单品提成需要单独计算
     *
     * @return
     */
    @Override
    public PageInfo selectMonthPushMoneyBySalesperson(MonthPushMoneyBySalespersonInData inData) {
//        List<StoreClassDTO> storeClassList = new ArrayList<>();
//        List<SalespersonSaleDetailDTO> salespersonSaleDetailList = new ArrayList();
        List<MonthPushMoneyBySalespersonOutData> outData = new ArrayList<>();

        if (ObjectUtil.isEmpty(inData.getSuitMonth())) {
            throw new BusinessException("提成月份不能为空！");
        }
        //查询营业员销售的提成
        List<MonthPushMoneyBySalespersonOutData> sales = tichengPlanZMapper.selectMonthSalesBySales(inData);
        if (ObjectUtil.isNotEmpty(sales)) {
            outData.addAll(sales);
        }
        //查询营业员单品销售的提成
        List<MonthPushMoneyBySalespersonOutData> pros = this.tichengPlanZMapper.selectMonthSalesByPro(inData);
        if (ObjectUtil.isNotEmpty(pros)) {
            outData.addAll(pros);
        }


        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            MonthPushMoneyBySalespersonOutTotal outTotal = new MonthPushMoneyBySalespersonOutTotal();
            for (MonthPushMoneyBySalespersonOutData monthData : outData) {
                outTotal.setAmt(CommonUtil.stripTrailingZeros(monthData.getAmt()).add(CommonUtil.stripTrailingZeros(outTotal.getAmt())));
                outTotal.setDays(CommonUtil.stripTrailingZeros(monthData.getDays()).add(CommonUtil.stripTrailingZeros(outTotal.getDays())));
                outTotal.setDeductionWage(CommonUtil.stripTrailingZeros(monthData.getDeductionWage()).add(CommonUtil.stripTrailingZeros(outTotal.getDeductionWage())));
            }
            if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                DecimalFormat df = new DecimalFormat("0.00%");
                outTotal.setDeductionRate(df.format(CommonUtil.stripTrailingZeros(outTotal.getDeductionWage()).divide(CommonUtil.stripTrailingZeros(outTotal.getAmt()), BigDecimal.ROUND_HALF_EVEN)));
            }
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public PageInfo selectMonthPushMoneyByStore(MonthPushMoneyByStoreInData inData) {
        if (ObjectUtil.isEmpty(inData.getSuitMonth())) {
            throw new BusinessException("提成月份不能为空！");
        }
        List<MonthPushMoneyByStoreOutData> outData = new ArrayList<>();
        //查询门店销售的提成
        List<MonthPushMoneyByStoreOutData> sales = this.tichengPlanZMapper.selectMonthSalesByStoreAndSales(inData);

        //查询门店单品销售的提成
        List<MonthPushMoneyByStoreOutData> pros = this.tichengPlanZMapper.selectMonthSalesByStoreAndPro(inData);

        MonthPushMoneyByStoreOutData data = null;

        //当两种销售都存在
        if (ObjectUtil.isNotEmpty(sales) && ObjectUtil.isNotEmpty(sales)) {
            for (MonthPushMoneyByStoreOutData sale : sales) {
                BigDecimal amt = BigDecimal.ZERO;
                BigDecimal deductionWage = BigDecimal.ZERO;

                data = new MonthPushMoneyByStoreOutData();
                data.setStoCode(sale.getStoCode());
                data.setStoName(sale.getStoName());
                data.setDays(sale.getDays());
                data.setDeductionWageSales(CommonUtil.stripTrailingZeros(sale.getDeductionWageSales()));
                amt = CommonUtil.stripTrailingZeros(sale.getAmt());
                deductionWage = CommonUtil.stripTrailingZeros(sale.getDeductionWageSales());
                for (MonthPushMoneyByStoreOutData pro : pros) {
                    if (sale.getStoCode().equals(pro.getStoCode())) {
                        data.setDeductionWagePro(CommonUtil.stripTrailingZeros(pro.getDeductionWagePro()));
                        amt = amt.add(CommonUtil.stripTrailingZeros(pro.getAmt()));
                        deductionWage = deductionWage.add(CommonUtil.stripTrailingZeros(pro.getDeductionWagePro()));
                    }
                }

                data.setAmt(amt);
                data.setDeductionWage(deductionWage);
                if (BigDecimal.ZERO.compareTo(CommonUtil.stripTrailingZeros(amt)) != 0) {
                    data.setDeductionWageSalesRate(CommonUtil.stripTrailingZeros(data.getDeductionWageSales()).multiply(new BigDecimal(100)).divide(amt, 2, BigDecimal.ROUND_HALF_EVEN));
                    data.setDeductionWageProRade(CommonUtil.stripTrailingZeros(data.getDeductionWagePro()).multiply(new BigDecimal(100)).divide(amt, 2, BigDecimal.ROUND_HALF_EVEN));
                    data.setDeductionWageRate(deductionWage.multiply(new BigDecimal(100)).divide(amt, 2, BigDecimal.ROUND_HALF_EVEN));
                }
                outData.add(data);
            }
        } else if (ObjectUtil.isNotEmpty(sales) && ObjectUtil.isEmpty(sales)) { //只有销售数据
            for (MonthPushMoneyByStoreOutData sale : sales) {
                data = new MonthPushMoneyByStoreOutData();
                data.setStoCode(sale.getStoCode());
                data.setStoName(sale.getStoName());
                data.setDays(sale.getDays());
                data.setDeductionWageSales(CommonUtil.stripTrailingZeros(sale.getDeductionWageSales()));
                data.setDeductionWage(CommonUtil.stripTrailingZeros(sale.getDeductionWageSales()));
                data.setAmt(sale.getAmt());
                data.setDeductionWageSalesRate(CommonUtil.stripTrailingZeros(sale.getDeductionWageSalesRate()));
                if (BigDecimal.ZERO.compareTo(CommonUtil.stripTrailingZeros(data.getAmt())) != 0) {
                    data.setDeductionWageRate(CommonUtil.stripTrailingZeros(data.getDeductionWage()).divide(CommonUtil.stripTrailingZeros(data.getAmt()), BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(100)));
                }
            }
            outData.add(data);
        } else if (ObjectUtil.isEmpty(sales) && ObjectUtil.isNotEmpty(sales)) { //只有单品数据
            for (MonthPushMoneyByStoreOutData pro : pros) {
                data = new MonthPushMoneyByStoreOutData();
                data.setStoCode(pro.getStoCode());
                data.setStoName(pro.getStoName());
                data.setDays(pro.getDays());
                data.setDeductionWagePro(CommonUtil.stripTrailingZeros(pro.getDeductionWagePro()));
                data.setDeductionWage(CommonUtil.stripTrailingZeros(pro.getDeductionWagePro()));
                data.setAmt(pro.getAmt());
                data.setDeductionWageProRade(CommonUtil.stripTrailingZeros(pro.getDeductionWageProRade()));
                if (BigDecimal.ZERO.compareTo(CommonUtil.stripTrailingZeros(data.getAmt())) != 0) {
                    data.setDeductionWageRate(CommonUtil.stripTrailingZeros(data.getDeductionWage()).divide(CommonUtil.stripTrailingZeros(data.getAmt()), BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(100)));
                }
            }
            outData.add(data);
        }
        PageInfo pageInfo;
        if (ObjectUtil.isNotEmpty(outData)) {
            // 集合列的数据汇总

            MonthPushMoneyByStoreOutTotal outTotal = new MonthPushMoneyByStoreOutTotal();
            for (MonthPushMoneyByStoreOutData monthData : outData) {
                outTotal.setAmt(CommonUtil.stripTrailingZeros(monthData.getAmt()).add(CommonUtil.stripTrailingZeros(outTotal.getAmt())));
                outTotal.setDays(CommonUtil.stripTrailingZeros(monthData.getDays()).add(CommonUtil.stripTrailingZeros(outTotal.getDays())));
                outTotal.setDeductionWageSales(CommonUtil.stripTrailingZeros(monthData.getDeductionWageSales()).add(CommonUtil.stripTrailingZeros(outTotal.getDeductionWageSales())));
                outTotal.setDeductionWagePro(CommonUtil.stripTrailingZeros(monthData.getDeductionWagePro()).add(CommonUtil.stripTrailingZeros(outTotal.getDeductionWagePro())));
                outTotal.setDeductionWage(CommonUtil.stripTrailingZeros(monthData.getDeductionWage()).add(CommonUtil.stripTrailingZeros(outTotal.getDeductionWage())));
            }
            if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                DecimalFormat df = new DecimalFormat("0.00%");
                outTotal.setDeductionWageProRade(df.format(CommonUtil.stripTrailingZeros(outTotal.getDeductionWagePro()).divide(CommonUtil.stripTrailingZeros(outTotal.getAmt()), BigDecimal.ROUND_HALF_EVEN)));
                outTotal.setDeductionWageSalesRate(df.format(CommonUtil.stripTrailingZeros(outTotal.getDeductionWageSales()).divide(CommonUtil.stripTrailingZeros(outTotal.getAmt()), BigDecimal.ROUND_HALF_EVEN)));
                outTotal.setDeductionWageRate(df.format(CommonUtil.stripTrailingZeros(outTotal.getDeductionWage()).divide(CommonUtil.stripTrailingZeros(outTotal.getAmt()), BigDecimal.ROUND_HALF_EVEN)));
            }
            pageInfo = new PageInfo(outData, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public PageInfo<ProductionVo> dismantledSale(Production production) {
        if (ObjectUtil.isEmpty(production.getStartDate()) && ObjectUtil.isEmpty(production.getEndDate()) && ObjectUtil.isEmpty(production.getProductCode())) {
            throw new BusinessException("商品编码,起始时间,结束时间不能为空！");
        }
        if (ObjectUtil.isEmpty(production.getBrIdList()) || production.getBrIdList().length < 0) {
            throw new BusinessException("请输入门店");
        }

        List<ProductionVo> list = tichengPlanZMapper.dismantledSale(production);

        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(list)) {
            pageInfo = new PageInfo(list);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;

    }


    @Override
    public PageInfo selectMonthPushListV2Page(PushMoneyByStoreV2InData inData) {
        List<PushMoneyByStoreV2OutData> outDatas = new ArrayList<>();
        List<TichengZInfoBO> tichengz = new ArrayList<>();
        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate());
        tcMap.put("endDate", inData.getEndDate());
        tcMap.put("planName", inData.getPlanName());
        tcMap.put("planId", inData.getPlanId());
        tcMap.put("source", inData.getSource());
        if (ObjectUtil.isEmpty(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
            tichengz.addAll(tichengProplanZMapper.selectAllByPro(tcMap));
        } else if ("1".equals(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
        } else if ("2".equals(inData.getType())) {
            tichengz.addAll(tichengProplanZMapper.selectAllByPro(tcMap));
        }
        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(tichengz)) {
            for (TichengZInfoBO tc : tichengz) {
                if ("1".equals(tc.getType())) {
                    PageInfo sale = selectMonthPushBySaleV2(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), inData.getStoArr(), tc.getPlanRejectDiscountRateSymbol(), tc.getPlanRejectDiscountRate()));
                    if (ObjectUtil.isNotEmpty(sale.getList())) {
                        outDatas.addAll(sale.getList());
                    }
                } else {
                    PageInfo sale = selectMonthPushByProV2(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), inData.getStoArr(), tc.getPlanRejectDiscountRateSymbol(), tc.getPlanRejectDiscountRate()));
                    if (ObjectUtil.isNotEmpty(sale.getList())) {
                        outDatas.addAll(sale.getList());
                    }
                }
            }

            // 集合列的数据汇总
            PushMoneyByStoreV2OutTotal outTotal = new PushMoneyByStoreV2OutTotal();
            for (PushMoneyByStoreV2OutData monthData : outDatas) {
//                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(),outTotal.getAmt()));
////                outTotal.setDays(BigDecimalUtil.add(monthData.getDays(),outTotal.getDays()));
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
//            if(outTotal.getAmt().compareTo(BigDecimal.ZERO)!=0) {
//                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(),outTotal.getAmt(),4));
//            }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    @Override
    public PageInfo selectMonthPushBySaleV2(PushMoneyByStoreV2InData inData) {
//        inData.setClient("1000001/**/3");
//        inData.setDeleteFlag("0");
//        inData.setId(10);
        List<PushMoneyByStoreV2OutData> outDatas = new ArrayList<>();

//        long start = 0;
//        long stop = 0;
//        start = System.currentTimeMillis();

        //  1.查询当前销售计划
        Map queryMap = new HashMap();                       // queryMap没用上
        queryMap.put("client", inData.getClient());
        queryMap.put("planId", inData.getPlanId());
        //  提成主表
        Example tichengZExample = new Example(GaiaTichengSaleplanZ.class);
        tichengZExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("id", inData.getPlanId()).andNotEqualTo("deleteFlag", "1");
        List<GaiaTichengSaleplanZ> tichengZs = tichengSaleplanZMapper.selectByExample(tichengZExample);
        if (ObjectUtil.isEmpty(tichengZs) || tichengZs.size() > 1) {
            throw new BusinessException("销售提成设置不正确,请检查设置!");
        }
        GaiaTichengSaleplanZ tichengZ = tichengZs.get(0);
        String startDate = null;
        String endDate = null;
        if ("2".equals(inData.getType())) {
            startDate = DateUtil.format(DateUtil.parse(inData.getTrialStartDate()), DatePattern.NORM_DATE_PATTERN);
            endDate = DateUtil.format(DateUtil.parse(inData.getEndDate()), DatePattern.NORM_DATE_PATTERN);

        } else {
            startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
            endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);

        }

//       DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
//         DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);

        //  提成门店表
        List<String> tichengSto = tichengSaleplanStoMapper.selectSto(inData.getPlanId());
//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }
        //  提成规则表
        Example tichengMExample = new Example(GaiaTichengSaleplanM.class);
        tichengMExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("pid", inData.getPlanId()).andEqualTo("deleteFlag", "0");
        List<GaiaTichengSaleplanM> tichengMs = tichengSaleplanMMapper.selectByExample(tichengMExample);


        //  剔除的商品分类
        List<String> rejectClass = tichengRejectClassMapper.selectClass(inData.getPlanId());
        //  剔除商品
        List<String> rejectPro = tichengRejectProMapper.selectPro(tichengZ);

//        stop = System.currentTimeMillis();
//        long s1 = TimeUnit.MILLISECONDS.toMillis(stop - start);
//        log.info("时间s1:" + String.valueOf(s1));

        //  2、取单店日均，确定销售级别
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" D.GSSD_BR_ID as brId, ")
                .append(" S.STO_SHORT_NAME as brName, ")
                .append(" round(sum(GSSD_AMT),2) as amt, ") //-- 销售额
                .append(" count(distinct GSSD_KL_DATE_BR) as days, ") //    销售天数
                .append(" round(SUM(GSSD_AMT)/COUNT(distinct GSSD_KL_DATE_BR),2) as amtAvg ") //  本店日均销售额
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON D.CLIENT = S.CLIENT AND D.GSSD_BR_ID = S.STO_CODE ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
        if (ObjectUtil.isNotEmpty(querySto)) {
            stoLvBuilder.append(" AND D.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(querySto));
        }
        stoLvBuilder.append(" GROUP BY D.CLIENT,D.GSSD_BR_ID,S.STO_SHORT_NAME ");
        stoLvBuilder.append(" ORDER BY D.GSSD_BR_ID ");
        log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
        List<Map<String, Object>> stoLvData = kylinJdbcTemplate.queryForList(stoLvBuilder.toString());
//            log.info("统计数据返回结果:{}", JSONObject.toJSONString(stoLvData));

        //  查询总的销售天数
        StringBuilder saleDaysBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" count(distinct D.GSSD_DATE) as days ") //    销售天数
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
        saleDaysBuilder.append(" GROUP BY D.CLIENT ");
        log.info("sql统计数据：{加盟商销售天数}:" + saleDaysBuilder.toString());
        Map<String, Object> saleDaysData = kylinJdbcTemplate.queryForMap(saleDaysBuilder.toString());


        //  4、取单店毛利区间，销售额，毛利额
        StringBuilder saleBuilder = new StringBuilder().append("SELECT D.GSSD_BR_ID as brId,");
        //  提成的基础数额 提成方式: 0 按销售额提成 1 按毛利额提成
        if ("0".equals(tichengZ.getPlanAmtWay())) {
            saleBuilder.append(" round(sum(GSSD_AMT),2) as baseAmount , "); //  销售额
        } else if ("1".equals(tichengZ.getPlanAmtWay())) {
            saleBuilder.append(" round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),2) as baseAmount, "); //  毛利额
        }
        //  毛利率: 0.商品毛利率 = 零售价*数量 -成本额  1.销售毛利率 = 实收金额-成本额
        if ("0".equals(tichengZ.getPlanRateWay())) {
            saleBuilder.append(" round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 2 )/ round(sum(GSSD_MOV_PRICE), 2 ) as  grossProfitRate "); //
        } else if ("1".equals(tichengZ.getPlanRateWay())) {
            saleBuilder.append(" GSSD_KL_MARGIN_AREA as grossProfitRate ");//    成本额
        }

        // 如果设置了剔除折扣率
        if (StringUtils.isNotEmpty(inData.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(inData.getPlanRejectDiscountRate())) {
            saleBuilder.append(" , ABS(case when min( GSSD_PRC1 ) = 0 or sum( GSSD_QTY )=0 then 0 else round(SUM( GSSD_AMT )/(min( GSSD_PRC1 )* sum( GSSD_QTY )), 4 ) end ) as ZKL ");
        }

        saleBuilder.append(" FROM GAIA_SD_SALE_D as D ")
                .append(" INNER JOIN GAIA_PRODUCT_BUSINESS  B ON B.CLIENT = D.CLIENT AND B.PRO_SITE = GSSD_BR_ID AND B.PRO_SELF_CODE = D.GSSD_PRO_ID ")
                .append(" WHERE 1=1 ")
                .append(" AND D.CLIENT ='" + inData.getClient() + "'")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'");
        if (ObjectUtil.isNotEmpty(querySto)) {
            saleBuilder.append(" AND D.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(querySto));
        }
        if ("0".equals(tichengZ.getPlanIfNegative())) {
            saleBuilder.append(" AND D.GSSD_KL_NEGATIVE_MARGIN >= '0' ");  //   (<='0')  不含负毛利，(<='1') 或者此系数不写为含负毛利
        }
        if (ObjectUtil.isNotEmpty(rejectClass)) {
            saleBuilder.append(" AND B.PRO_CLASS NOT IN ")
                    .append(CommonUtil.queryByBatch(rejectClass));  //  剔除的商品分类
        }
        if (ObjectUtil.isNotEmpty(rejectPro)) {
            saleBuilder.append(" AND D.GSSD_PRO_ID NOT IN ")
                    .append(CommonUtil.queryByBatch(rejectPro));  //  剔除的商品
        }
        saleBuilder.append(" GROUP BY D.GSSD_BR_ID");
        if ("1".equals(tichengZ.getPlanRateWay())) {
            saleBuilder.append(",GSSD_KL_MARGIN_AREA ");
        }
        // 如果设置了剔除折扣率
        if (StringUtils.isNotEmpty(inData.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(inData.getPlanRejectDiscountRate())) {
            switch (inData.getPlanRejectDiscountRateSymbol()) {
                case "=":
                    inData.setPlanRejectDiscountRateSymbol("!=");
                    break;
                case ">":
                    inData.setPlanRejectDiscountRateSymbol("<=");
                    break;
                case ">=":
                    inData.setPlanRejectDiscountRateSymbol("<");
                    break;
                case "<":
                    inData.setPlanRejectDiscountRateSymbol(">=");
                    break;
                case "<=":
                    inData.setPlanRejectDiscountRateSymbol(">");
                    break;
            }
            saleBuilder.append("having ZKL").append(inData.getPlanRejectDiscountRateSymbol()).append(inData.getPlanRejectDiscountRate());
        }
//                    .append(" ORDER BY D.GSSD_BR_ID,GSSD_KL_MARGIN_AREA ");
        log.info("sql统计数据：{取单店毛利区间，销售额，毛利额:}:" + saleBuilder.toString());
        List<Map<String, Object>> saleDatas = kylinJdbcTemplate.queryForList(saleBuilder.toString());
//                log.info("统计数据返回结果:{}", JSONObject.toJSONString(saleData));

        //  3.按照门店日均 把毛利率区间分给各个门店
        if (ObjectUtil.isNotEmpty(stoLvData)) {
            Map<String, Map> roleGroups = new HashMap<>();
            for (Map stoLv : stoLvData) {
                Map<String, Object> map = new HashMap<>();
                List<GaiaTichengSaleplanM> roles = new ArrayList<>();
                BigDecimal amtAvg = BigDecimalUtil.toBigDecimal(stoLv.get("amtAvg"));
                if (ObjectUtil.isNotEmpty(amtAvg) || ObjectUtil.isNotEmpty(tichengMs)) {
                    for (GaiaTichengSaleplanM tichengM : tichengMs) {
                        if (amtAvg.compareTo(tichengM.getMinDailySaleAmt()) == 1
                                && amtAvg.compareTo(tichengM.getMaxDailySaleAmt()) < 1) {
                            roles.add(tichengM);
                        }
                    }
                }
                map.put("brId", String.valueOf(stoLv.get("brId")));
                map.put("brName", String.valueOf(stoLv.get("brName")));
                map.put("days", String.valueOf(stoLv.get("days")));
                map.put("amt", String.valueOf(stoLv.get("amt")));
                map.put("amtAvg", String.valueOf(stoLv.get("amtAvg")));
                map.put("roles", roles);
                roleGroups.put(String.valueOf(stoLv.get("brId")), map);
            }
            stoLvData.forEach(item -> {
                PushMoneyByStoreV2OutData outData = new PushMoneyByStoreV2OutData();
                System.err.println(item.get("brId"));
                //  算提成
                Map roleBySto = roleGroups.get(item.get("brId"));
                outData.setPlanName(tichengZ.getPlanName());
                outData.setPlanId(inData.getPlanId());
                outData.setStartDate(tichengZ.getPlanStartDate());
                outData.setEndtDate(tichengZ.getPlanEndDate());
                if ("1".equals(tichengZ.getPlanStatus())) {
                    outData.setStatus("1");
                } else if ("2".equals(tichengZ.getDeleteFlag())) {
                    outData.setStatus("2");
                }
                outData.setType("销售提成");
                outData.setTypeValue("1");
                outData.setStoCode(String.valueOf(roleBySto.get("brId")));
                outData.setStoName(String.valueOf(roleBySto.get("brName")));
                outData.setDays(BigDecimalUtil.toBigDecimal(roleBySto.get("days")));
                outData.setAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("amt")));
                BigDecimal tcPrices = BigDecimal.ZERO;
                if (ObjectUtil.isNotEmpty(tichengMs)) {
                    List<GaiaTichengSaleplanM> role2 = (List<GaiaTichengSaleplanM>) roleBySto.get("roles");

                    //筛选出对应门店和营业员的数据
                    List<Map> saleData = saleDatas.stream().filter(s -> s.get("brId").equals(item.get("brId"))).collect(Collectors.toList());

                    for (Map saleD : saleData) {
                        BigDecimal baseAmount = BigDecimalUtil.toBigDecimal(saleD.get("baseAmount"));
                        BigDecimal grossProfitRate = BigDecimalUtil.toBigDecimal(saleD.get("grossProfitRate"));
                        for (GaiaTichengSaleplanM tichengM : role2) {
                            if (saleD.get("brId").equals(item.get("brId"))
                                    && grossProfitRate.compareTo(tichengM.getMinProMll()) == 1
                                    && grossProfitRate.compareTo(tichengM.getMaxProMll()) < 1) {
                                tcPrices = tcPrices.add(baseAmount.multiply(BigDecimalUtil.toBigDecimal(tichengM.getTichengScale())));
                                // 理论上要求只能命中同一条明细
                                break;
                            }
                        }
                    }
                }
                outData.setDeductionWage(BigDecimalUtil.divide(tcPrices, 100, 4));
                outData.setDeductionWageRate(BigDecimalUtil.divide(outData.getDeductionWage(), roleBySto.get("amt"), 4));
                outDatas.add(outData);
            });

//                // 遍历map
//            stop = System.currentTimeMillis();
//            long s3 = TimeUnit.MILLISECONDS.toMillis(stop - start);
//            log.info("时间s3:" + String.valueOf(s3));
        }

        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            // 集合列的数据汇总
            PushMoneyByStoreV2OutTotal outTotal = new PushMoneyByStoreV2OutTotal();
            for (PushMoneyByStoreV2OutData monthData : outDatas) {
                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(), outTotal.getAmt()));
                if (ObjectUtil.isNotEmpty(saleDaysData) && StringUtils.isNotEmpty(String.valueOf(saleDaysData.get("days")))) {
                    outTotal.setDays(BigDecimalUtil.toBigDecimal(saleDaysData.get("days")));
                }
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
            if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(), outTotal.getAmt(), 4));
            }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;

    }

    @Override
    public PageInfo selectMonthPushByProV2(PushMoneyByStoreV2InData inData) {
//        inData.setClient("10000013");
//        inData.setDeleteFlag("0");
//        inData.setId(5);
        List<PushMoneyByStoreV2OutData> outDatas = new ArrayList<>();

        long start = 0;
        long stop = 0;
        start = System.currentTimeMillis();

        //  1.查询当前商品提成计划
        Map queryMap = new HashMap();
        queryMap.put("client", inData.getClient());
        queryMap.put("id", inData.getPlanId());
        //  提成主表
        GaiaTichengProplanZ tichengZ = tichengProplanZMapper.selectTichengProZ(queryMap);
        String startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        String endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);
        TichengProplanStoN tichengProplanStoNQuery = new TichengProplanStoN();
        tichengProplanStoNQuery.setClient(inData.getClient());
        tichengProplanStoNQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        //  查询当前商品提成门店
        List<String> tichengSto = new ArrayList<>();
        List<TichengProplanStoN> tichengProplanStoNList = tichengProplanStoMapper.select(tichengProplanStoNQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanStoNList)) {
            tichengProplanStoNList.forEach(x -> {
                tichengSto.add(x.getStoCode());
            });
        }
//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }

        //  查询当前商品提成商品
        List<Map<String, String>> tichengPros = tichengProplanProMapper.selectPro(queryMap);

        //  2、取单店日均，确定销售
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" D.GSSD_BR_ID as brId, ")
                .append(" S.STO_SHORT_NAME as brName, ")
                .append(" round(sum(GSSD_AMT),2) as amt, ") //-- 销售额
                .append(" count(distinct GSSD_KL_DATE_BR) as days ") //    销售天数
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON D.CLIENT = S.CLIENT AND D.GSSD_BR_ID = S.STO_CODE ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
        if (ObjectUtil.isNotEmpty(querySto)) {
            stoLvBuilder.append(" AND D.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(querySto));
        }
        stoLvBuilder.append(" GROUP BY D.CLIENT,D.GSSD_BR_ID,S.STO_SHORT_NAME ");
        stoLvBuilder.append(" ORDER BY D.GSSD_BR_ID ");
        log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
        List<Map<String, Object>> stoLvData = kylinJdbcTemplate.queryForList(stoLvBuilder.toString());
//            log.info("统计数据返回结果:{}", JSONObject.toJSONString(stoLvData));

        StringBuilder saleDaysBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" count(distinct D.GSSD_DATE) as days ") //    销售天数
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
        saleDaysBuilder.append(" GROUP BY D.CLIENT ");
        log.info("sql统计数据：{加盟商销售天数}:" + saleDaysBuilder.toString());
        Map<String, Object> saleDaysData = kylinJdbcTemplate.queryForMap(saleDaysBuilder.toString());


        if (ObjectUtil.isNotEmpty(tichengZ)) {

            StringBuilder proBuilder = new StringBuilder().append("SELECT D.GSSD_BR_ID as brId,")
                    .append(" STO.STO_SHORT_NAME AS brName, ")
                    .append(" D.GSSD_PRO_ID AS proId, ")
                    .append(" round( sum( GSSD_AMT ), 2 ) AS baseAmount, ")
                    .append(" round( sum( GSSD_QTY ), 2 ) AS qty ")
                    .append(" FROM GAIA_SD_SALE_D AS D ")
                    .append(" INNER JOIN GAIA_STORE_DATA STO ON STO.CLIENT = D.CLIENT ")
                    .append(" AND STO.STO_CODE = D.GSSD_BR_ID ")
                    .append(" WHERE 1=1 ")
                    .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                    .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                    .append(" AND D.CLIENT= '" + inData.getClient() + "'");
            if (ObjectUtil.isNotEmpty(querySto)) {
                proBuilder.append(" AND D.GSSD_BR_ID IN ")
                        .append(CommonUtil.queryByBatch(querySto));
            }
            proBuilder.append(" GROUP BY D.GSSD_BR_ID,STO.STO_SHORT_NAME,D.GSSD_PRO_ID ");
            log.info("sql统计数据：{取单店毛利区间，销售额，毛利额:" + "}:" + proBuilder.toString());
            List<Map<String, Object>> saleDatas = kylinJdbcTemplate.queryForList(proBuilder.toString());
//            log.info("统计数据返回结果:{}", JSONObject.toJSONString(saleData));

            for (Map sale : stoLvData) {
                PushMoneyByStoreV2OutData outData = new PushMoneyByStoreV2OutData();
                outData.setPlanName(tichengZ.getPlanName());
                outData.setStartDate(tichengZ.getPlanStartDate());
                outData.setEndtDate(tichengZ.getPlanEndDate());
                outData.setType("单品提成");
                if ("1".equals(tichengZ.getPlanStatus())) {
                    outData.setStatus("1");
                } else if ("2".equals(tichengZ.getDeleteFlag())) {
                    outData.setStatus("2");
                }
                outData.setStoCode(String.valueOf(sale.get("brId")));
                outData.setStoName(String.valueOf(sale.get("brName")));
                outData.setDays(BigDecimalUtil.toBigDecimal(sale.get("days")));
                outData.setAmt(BigDecimalUtil.toBigDecimal(sale.get("amt")));
                BigDecimal tcPrices = BigDecimal.ZERO;
                //筛选出对应门店和营业员的数据
                List<Map> saleData = saleDatas.stream().filter(s -> s.get("brId").equals(sale.get("brId"))).collect(Collectors.toList());
                //  我也不想的 怪我太菜了
                for (Map saleD : saleData) {
                    for (Map pro : tichengPros) {
                        if (saleD.get("proId").equals(pro.get("proId"))) {
                            BigDecimal tcPrice = BigDecimalUtil.multiply(saleD.get("qty"), pro.get("tcPrice"));
                            tcPrices = BigDecimalUtil.add(tcPrices, tcPrice);
                            break;
                        }
                    }
                }
                outData.setDeductionWage(tcPrices);
                outData.setDeductionWageRate(BigDecimalUtil.divide(tcPrices, sale.get("amt"), 2));
                outDatas.add(outData);
            }
        }


        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            // 集合列的数据汇总
            PushMoneyByStoreV2OutTotal outTotal = new PushMoneyByStoreV2OutTotal();
            for (PushMoneyByStoreV2OutData monthData : outDatas) {
                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(), outTotal.getAmt()));
                if (ObjectUtil.isNotEmpty(saleDaysData) && StringUtils.isNotEmpty(String.valueOf(saleDaysData.get("days")))) {
                    outTotal.setDays(BigDecimalUtil.toBigDecimal(saleDaysData.get("days")));
                }
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
            if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(), outTotal.getAmt(), 4));
            }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public PageInfo selectMonthPushSalespersonPageV2(PushMoneyBySalespersonV2InData inData) {
        List<PushMoneyByStoreSalepersonV2OutData> outDatas = new ArrayList<>();

        List<TichengZInfoBO> tichengz = new ArrayList<>();
        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate());
        tcMap.put("endDate", inData.getEndDate());
        tcMap.put("planName", inData.getPlanName());
        tcMap.put("source", inData.getSource());

        if (ObjectUtil.isEmpty(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
            tichengz.addAll(tichengProplanZMapper.selectAllByPro(tcMap));
        } else if ("1".equals(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
        } else if ("2".equals(inData.getType())) {
            tichengz.addAll(tichengProplanZMapper.selectAllByPro(tcMap));
        }
        if (CollUtil.isNotEmpty(tichengz)) {
            for (TichengZInfoBO tc : tichengz) {
                if ("1".equals(tc.getType())) {
                    PageInfo sale = selectMonthPushSalespersonBySaleV2(new PushMoneyBySalespersonV2InData(tc.getClient(), tc.getPlanId(), inData.getSalerId(), tc.getStartDate(), tc.getEndtDate(), inData.getStoArr()));
                    if (ObjectUtil.isNotEmpty(sale.getList())) {
                        outDatas.addAll(sale.getList());
                    }
                } else {
                    PageInfo sale = selectMonthPushSalespersonByProV2(new PushMoneyBySalespersonV2InData(tc.getClient(), tc.getPlanId(), inData.getSalerId(), tc.getStartDate(), tc.getEndtDate(), inData.getStoArr()));
                    if (ObjectUtil.isNotEmpty(sale.getList())) {
                        outDatas.addAll(sale.getList());
                    }
                }
            }
        }

        // 集合列的数据汇总
        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {

            // 集合列的数据汇总
            PushMoneyByStoreSalepersonV2OutTotal outTotal = new PushMoneyByStoreSalepersonV2OutTotal();
            for (PushMoneyByStoreSalepersonV2OutData monthData : outDatas) {
//                    outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(), outTotal.getAmt()));
////                    outTotal.setDays(BigDecimalUtil.add(monthData.getDays(), outTotal.getDays()));
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
//                if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
//                    outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(), outTotal.getAmt(), 4));
//                }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    @Override
    public PageInfo selectMonthPushSalespersonBySaleV2(PushMoneyBySalespersonV2InData inData) {
//        inData.setClient("10000013");
//        inData.setId(10);
        List<PushMoneyByStoreSalepersonV2OutData> outDatas = new ArrayList<>();

//        long start = 0;
//        long stop = 0;
//        start = System.currentTimeMillis();

        //  1.查询当前销售计划
        Map queryMap = new HashMap();
        queryMap.put("client", inData.getClient());
        queryMap.put("id", inData.getId());
        //  提成主表
        Example tichengZExample = new Example(GaiaTichengSaleplanZ.class);
        tichengZExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("id", inData.getId()).andNotEqualTo("deleteFlag", "1");//                .andEqualTo("planStatus","1")
//              ;
        List<GaiaTichengSaleplanZ> tichengZs = tichengSaleplanZMapper.selectByExample(tichengZExample);
        if (ObjectUtil.isEmpty(tichengZs) || tichengZs.size() > 1) {
            throw new BusinessException("销售提成设置不正确,请检查设置!");
        }
        GaiaTichengSaleplanZ tichengZ = tichengZs.get(0);
        String startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        String endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);
        //  提成门店表
        List<String> tichengSto = tichengSaleplanStoMapper.selectSto(inData.getId());


//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }
        //  提成规则表
        Example tichengMExample = new Example(GaiaTichengSaleplanM.class);
        tichengMExample.createCriteria().andEqualTo("client", inData.getClient()).andEqualTo("pid", inData.getId()).andEqualTo("deleteFlag", "0");
        List<GaiaTichengSaleplanM> tichengMs = tichengSaleplanMMapper.selectByExample(tichengMExample);


        //  剔除的商品分类
        List<String> rejectClass = tichengRejectClassMapper.selectClass(inData.getId());
        //  剔除商品
        List<String> rejectPro = tichengRejectProMapper.selectPro(tichengZ);

//        stop = System.currentTimeMillis();
//        long s1 = TimeUnit.MILLISECONDS.toMillis(stop - start);
//        log.info("时间s1:" + String.valueOf(s1));

        //  2、取单店日均，确定销售级别
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" D.GSSD_BR_ID as brId, ")
                .append(" S.STO_SHORT_NAME as brName, ")
                .append(" D.GSSD_SALER_ID salerId, ")
                .append(" U.USER_NAM salerName, ")
                .append(" round( sum( GSSD_AMT ), 2 ) AS amt, ")
                .append(" count( DISTINCT GSSD_KL_DATE_BR ) AS days, ")
                .append(" SD.AMTAVG AS amtAvg ")
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON D.CLIENT = S.CLIENT AND D.GSSD_BR_ID = S.STO_CODE ")
                .append(" LEFT JOIN GAIA_USER_DATA as U ON D.CLIENT = U.CLIENT AND D.GSSD_SALER_ID = U.USER_ID ")
                .append(" INNER JOIN ( SELECT CLIENT,GSSD_BR_ID, ")
                .append(" round( SUM( GSSD_AMT )/ COUNT( DISTINCT GSSD_KL_DATE_BR ), 2 ) AS AMTAVG ")
                .append(" FROM GAIA_SD_SALE_D ")
                .append(" WHERE 1 = 1  ")
                .append(" AND GSSD_DATE >= '" + startDate + "'")
                .append(" AND GSSD_DATE <= '" + endDate + "'")
                .append(" AND CLIENT= '" + inData.getClient() + "'");
        if (ObjectUtil.isNotEmpty(inData.getSalerId())) {
            stoLvBuilder.append(" AND GSSD_SALER_ID = '" + inData.getSalerId() + "' ");
        }
        stoLvBuilder.append(" GROUP BY CLIENT,GSSD_BR_ID ) SD ON SD.CLIENT = D.CLIENT AND SD.GSSD_BR_ID = D.GSSD_BR_ID ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
        if (ObjectUtil.isNotEmpty(inData.getSalerId())) {
            stoLvBuilder.append(" AND D.GSSD_SALER_ID = '" + inData.getSalerId() + "' ");
        }
        if (ObjectUtil.isNotEmpty(querySto)) {
            stoLvBuilder.append(" AND D.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(querySto));
        }
        stoLvBuilder.append(" GROUP BY ")
                .append(" D.CLIENT, ")
                .append(" D.GSSD_BR_ID, ")
                .append(" S.STO_SHORT_NAME, ")
                .append(" D.GSSD_SALER_ID, ")
                .append(" U.USER_NAM, ")
                .append(" SD.AMTAVG ")
                .append(" ORDER BY D.GSSD_BR_ID");
        log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
        List<Map<String, Object>> stoLvData = kylinJdbcTemplate.queryForList(stoLvBuilder.toString());
//            log.info("统计数据返回结果:{}", JSONObject.toJSONString(stoLvData));

        //  3.按照门店日均 把毛利率区间分给各个门店
        if (ObjectUtil.isNotEmpty(stoLvData)) {
            Map<String, Map> roleGroups = new HashMap<>();
            for (Map stoLv : stoLvData) {
                Map<String, Object> map = new HashMap<>();
                List<GaiaTichengSaleplanM> roles = new ArrayList<>();
                BigDecimal amtAvg = BigDecimalUtil.toBigDecimal(stoLv.get("amtAvg"));
                if (ObjectUtil.isNotEmpty(amtAvg) || ObjectUtil.isNotEmpty(tichengMs)) {
                    for (GaiaTichengSaleplanM tichengM : tichengMs) {
                        if (amtAvg.compareTo(tichengM.getMinDailySaleAmt()) == 1
                                && amtAvg.compareTo(tichengM.getMaxDailySaleAmt()) < 1) {
                            roles.add(tichengM);
                        }
                    }
                }
                map.put("brId", String.valueOf(stoLv.get("brId")));
                map.put("brName", String.valueOf(stoLv.get("brName")));
                map.put("days", String.valueOf(stoLv.get("days")));
                map.put("salerId", String.valueOf(stoLv.get("salerId")));
                map.put("salerName", String.valueOf(stoLv.get("salerName")));
                map.put("amt", String.valueOf(stoLv.get("amt")));
                map.put("amtAvg", String.valueOf(stoLv.get("amtAvg")));
                map.put("roles", roles);
                String key = String.valueOf(stoLv.get("brId")) + UtilConst.SYMBOL_TO + String.valueOf(stoLv.get("salerId"));
                roleGroups.put(key, map);
            }
//            stop = System.currentTimeMillis();
//            long s2 = TimeUnit.MILLISECONDS.toMillis(stop - start);
//            log.info("时间s2:" + String.valueOf(s2));

            //  4、取单店毛利区间，销售额，毛利额
            StringBuilder saleBuilder = new StringBuilder().append("SELECT D.GSSD_BR_ID as brId,D.GSSD_SALER_ID salerId,U.USER_NAM salerName,");
            //  提成的基础数额 提成方式: 0 按销售额提成 1 按毛利额提成
            if ("0".equals(tichengZ.getPlanAmtWay())) {
                saleBuilder.append(" round(sum(GSSD_AMT),2) as baseAmount , "); //  销售额
            } else if ("1".equals(tichengZ.getPlanAmtWay())) {
                saleBuilder.append(" round(sum(GSSD_AMT),2) - round(sum(GSSD_MOV_PRICE),2) as baseAmount, "); //  毛利额
            }
            //  毛利率: 0.商品毛利率 = 零售价*数量 -成本额  1.销售毛利率 = 实收金额-成本额
            if ("0".equals(tichengZ.getPlanRateWay())) {
                saleBuilder.append(" round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 2 )/ round(sum(GSSD_MOV_PRICE), 2 ) as  grossProfitRate "); //
            } else if ("1".equals(tichengZ.getPlanRateWay())) {
                saleBuilder.append(" GSSD_KL_MARGIN_AREA as grossProfitRate ");//    成本额
            }
            saleBuilder.append(" FROM GAIA_SD_SALE_D as D ")
                    .append(" INNER JOIN GAIA_PRODUCT_BUSINESS  B ON B.CLIENT = D.CLIENT AND B.PRO_SITE = GSSD_BR_ID AND B.PRO_SELF_CODE = D.GSSD_PRO_ID ")
                    .append(" LEFT JOIN GAIA_USER_DATA as U ON D.CLIENT = U.CLIENT AND D.GSSD_SALER_ID = U.USER_ID ")
                    .append(" WHERE 1=1 ")
                    .append(" AND D.CLIENT ='" + inData.getClient() + "'")
                    .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                    .append(" AND D.GSSD_DATE <= '" + endDate + "'");
            if (ObjectUtil.isNotEmpty(inData.getSalerId())) {
                saleBuilder.append(" AND D.GSSD_SALER_ID = '" + inData.getSalerId() + "' ");
            }
            if (ObjectUtil.isNotEmpty(querySto)) {
                saleBuilder.append(" AND D.GSSD_BR_ID IN ")
                        .append(CommonUtil.queryByBatch(querySto));
            }
            if ("0".equals(tichengZ.getPlanIfNegative())) {
                saleBuilder.append(" AND D.GSSD_KL_NEGATIVE_MARGIN >= '0' ");  //   (<='0')  不含负毛利，(<='1') 或者此系数不写为含负毛利
            }
            if (ObjectUtil.isNotEmpty(rejectClass)) {
                saleBuilder.append(" AND B.PRO_CLASS NOT IN ")
                        .append(CommonUtil.queryByBatch(rejectClass));  //  剔除的商品分类
            }
            if (ObjectUtil.isNotEmpty(rejectPro)) {
                saleBuilder.append(" AND D.GSSD_PRO_ID NOT IN ")
                        .append(CommonUtil.queryByBatch(rejectPro));  //  剔除的商品
            }

            saleBuilder.append(" GROUP BY D.GSSD_BR_ID,D.GSSD_SALER_ID,GSSD_KL_MARGIN_AREA,D.GSSD_SALER_ID,U.USER_NAM");
//                    .append(" ORDER BY D.GSSD_BR_ID,GSSD_KL_MARGIN_AREA ");
            log.info("sql统计数据：{取单店毛利区间，销售额，毛利额:}:" + saleBuilder.toString());
            List<Map<String, Object>> saleDatas = kylinJdbcTemplate.queryForList(saleBuilder.toString());
//                log.info("统计数据返回结果:{}", JSONObject.toJSONString(saleData));
//            stop = System.currentTimeMillis();
//            long s3 = TimeUnit.MILLISECONDS.toMillis(stop - start);
//            log.info("时间s3:" + String.valueOf(s3));

//                stoLvData.forEach(item -> {
            for (Map<String, Object> item : stoLvData) {

                PushMoneyByStoreSalepersonV2OutData outData = new PushMoneyByStoreSalepersonV2OutData();
                //  算提成
                String key = String.valueOf(item.get("brId")) + UtilConst.SYMBOL_TO + String.valueOf(item.get("salerId"));
                Map roleBySto = roleGroups.get(key);
                outData.setPlanName(tichengZ.getPlanName());
                outData.setStartDate(tichengZ.getPlanStartDate());
                outData.setEndtDate(tichengZ.getPlanEndDate());
                if ("1".equals(tichengZ.getPlanStatus())) {
                    outData.setStatus("1");
                } else if ("2".equals(tichengZ.getDeleteFlag())) {
                    outData.setStatus("2");
                }
                outData.setType("销售提成");
                outData.setStoCode(String.valueOf(roleBySto.get("brId")));
                outData.setStoName(String.valueOf(roleBySto.get("brName")));
                outData.setDays(BigDecimalUtil.toBigDecimal(roleBySto.get("days")));
                outData.setAmt(BigDecimalUtil.toBigDecimal(roleBySto.get("amt")));
                outData.setSalerId(String.valueOf(roleBySto.get("salerId")));
                BigDecimal tcPrices = BigDecimal.ZERO;
                //筛选出对应门店和营业员的数据
                if (ObjectUtil.isNotEmpty(tichengMs)) {
                    List<GaiaTichengSaleplanM> role2 = (List<GaiaTichengSaleplanM>) roleBySto.get("roles");

                    List<Map> saleData = saleDatas.stream().filter(s -> s.get("brId").equals(item.get("brId")) && s.get("salerId").equals(item.get("salerId"))).collect(Collectors.toList());
                    for (Map saleD : saleData) {
                        outData.setSalerName(String.valueOf(saleD.get("salerName")));

                        BigDecimal baseAmount = BigDecimalUtil.toBigDecimal(saleD.get("baseAmount"));
                        BigDecimal grossProfitRate = BigDecimalUtil.toBigDecimal(saleD.get("grossProfitRate"));

                        for (GaiaTichengSaleplanM tichengM : role2) {
                            if (
//                                    saleD.get("brId").equals(item.get("brId"))
//                                    && saleD.get("salerId").equals(item.get("salerId") )
                                    grossProfitRate.compareTo(tichengM.getMinProMll()) == 1
                                            && grossProfitRate.compareTo(tichengM.getMaxProMll()) < 1) {
                                tcPrices = tcPrices.add(baseAmount.multiply(BigDecimalUtil.toBigDecimal(tichengM.getTichengScale())));
                                // 理论上要求只能命中同一条明细
                                break;
                            }
                        }
                    }
                    outData.setDeductionWage(BigDecimalUtil.divide(tcPrices, 100, 4));
                    outData.setDeductionWageRate(BigDecimalUtil.divide(outData.getDeductionWage(), roleBySto.get("amt"), 4));
                    outDatas.add(outData);
                }

//                stop = System.currentTimeMillis();
//                long s4 = TimeUnit.MILLISECONDS.toMillis(stop - start);
//                log.info("时间s4:" + String.valueOf(s4));

            }
        }

        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            // 集合列的数据汇总
            PushMoneyByStoreSalepersonV2OutTotal outTotal = new PushMoneyByStoreSalepersonV2OutTotal();
            for (PushMoneyByStoreSalepersonV2OutData monthData : outDatas) {
//                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(), outTotal.getAmt()));
//                outTotal.setDays(BigDecimalUtil.add(monthData.getDays(), outTotal.getDays()));
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
//            if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
//                outTotal.setDeductionWageRate(BigDecimalUtil.divide(BigDecimalUtil.multiply(outTotal.getDeductionWage(), new BigDecimal(100.00)), outTotal.getAmt(), 4));
//            }
            pageInfo = new PageInfo(outDatas);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public PageInfo selectMonthPushSalespersonByProV2(PushMoneyBySalespersonV2InData inData) {
//        inData.setClient("10000013");
//        inData.setId(5);
//        inData.setSalerId("1054");
        List<PushMoneyByStoreSalepersonV2OutData> outDatas = new ArrayList<>();

        long start = 0;
        long stop = 0;
        start = System.currentTimeMillis();

        //  1.查询当前商品提成计划
        //  提成主表
        Map queryMap = new HashMap();
        queryMap.put("client", inData.getClient());
        queryMap.put("id", inData.getId());
        GaiaTichengProplanZ tichengZ = tichengProplanZMapper.selectTichengProZ(queryMap);
        if (ObjectUtil.isNotEmpty(tichengZ)) {
            String startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
            String endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);

            TichengProplanStoN tichengProplanStoNQuery = new TichengProplanStoN();
            tichengProplanStoNQuery.setClient(inData.getClient());
            tichengProplanStoNQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
            //  查询当前商品提成门店
            List<String> tichengSto = new ArrayList<>();
            List<TichengProplanStoN> tichengProplanStoNList = tichengProplanStoMapper.select(tichengProplanStoNQuery);
            if (CollectionUtil.isNotEmpty(tichengProplanStoNList)) {
                tichengProplanStoNList.forEach(x -> {
                    tichengSto.add(x.getStoCode());
                });
            }
//            List<String> tichengSto = tichengProplanStoMapper.selectSto(queryMap);
//        List<String> tichengSto = Arrays.asList("10001");
            List<String> querySto = new ArrayList<>();
            querySto.addAll(tichengSto);
            if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
                //  设置的门店和筛选的门店求交集
                querySto.retainAll(inData.getStoArr());
            }

            //  查询当前商品提成商品
            List<Map<String, String>> tichengPros = tichengProplanProMapper.selectPro(queryMap);

            //  2、取单店日均，确定销售
            StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                    .append(" D.GSSD_BR_ID as brId, ")
                    .append(" S.STO_SHORT_NAME as brName, ")
                    .append(" D.GSSD_SALER_ID as salerId, ")
                    .append(" U.USER_NAM as salerName, ")
                    .append(" round(sum(GSSD_AMT),2) as amt, ") //-- 销售额
                    .append(" count(distinct GSSD_KL_DATE_BR) as days ") //    销售天数
                    .append(" FROM GAIA_SD_SALE_D as D ")
                    .append(" INNER JOIN GAIA_STORE_DATA as S ON D.CLIENT = S.CLIENT AND D.GSSD_BR_ID = S.STO_CODE ")
                    .append(" LEFT JOIN GAIA_USER_DATA as U ON D.CLIENT = U.CLIENT AND D.GSSD_SALER_ID = U.USER_ID ")
                    .append(" WHERE 1=1 ")
                    .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                    .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                    .append(" AND D.CLIENT= '" + inData.getClient() + "'");
            if (ObjectUtil.isNotEmpty(inData.getSalerId())) {
                stoLvBuilder.append(" AND D.GSSD_SALER_ID = '" + inData.getSalerId() + "'");
            }
            if (ObjectUtil.isNotEmpty(querySto)) {
                stoLvBuilder.append(" AND D.GSSD_BR_ID IN ")
                        .append(CommonUtil.queryByBatch(querySto));
            }
            stoLvBuilder.append(" GROUP BY D.CLIENT,D.GSSD_BR_ID,S.STO_SHORT_NAME,D.GSSD_SALER_ID,U.USER_NAM");
            log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
            List<Map<String, Object>> stoLvData = kylinJdbcTemplate.queryForList(stoLvBuilder.toString());
//            log.info("统计数据返回结果:{}", JSONObject.toJSONString(stoLvData));

            StringBuilder proBuilder = new StringBuilder().append("SELECT D.GSSD_BR_ID as brId,")
                    .append(" STO.STO_SHORT_NAME AS brName, ")
                    .append(" D.GSSD_PRO_ID AS proId, ")
                    .append(" D.GSSD_SALER_ID as salerId, ")
                    .append(" round( sum( GSSD_AMT ), 2 ) AS baseAmount, ")
                    .append(" round( sum( GSSD_QTY ), 2 ) AS qty ")
                    .append(" FROM GAIA_SD_SALE_D AS D ")
                    .append(" INNER JOIN GAIA_STORE_DATA STO ON STO.CLIENT = D.CLIENT ")
                    .append(" AND STO.STO_CODE = D.GSSD_BR_ID ")
                    .append(" WHERE 1=1 ")
                    .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                    .append(" AND D.GSSD_DATE <= '" + endDate + "'")
//                    .append(" AND D.GSSD_PRO_ID = '1010772' ")
                    .append(" AND D.CLIENT= '" + inData.getClient() + "'");
            if (ObjectUtil.isNotEmpty(inData.getSalerId())) {
                stoLvBuilder.append(" AND D.GSSD_SALER_ID = '" + inData.getSalerId() + "'");
            }
            if (ObjectUtil.isNotEmpty(querySto)) {
                proBuilder.append(" AND D.GSSD_BR_ID IN ")
                        .append(CommonUtil.queryByBatch(querySto));
            }
            proBuilder.append(" GROUP BY D.GSSD_BR_ID,STO.STO_SHORT_NAME,D.GSSD_PRO_ID,D.GSSD_SALER_ID ");
            proBuilder.append(" ORDER BY D.GSSD_BR_ID,D.GSSD_SALER_ID ");

            log.info("sql统计数据：{取单店毛利区间，销售额，毛利额:" + "}:" + proBuilder.toString());
            List<Map<String, Object>> saleDatas = kylinJdbcTemplate.queryForList(proBuilder.toString());
//            log.info("统计数据返回结果:{}", JSONObject.toJSONString(saleDatas));

            for (Map sale : stoLvData) {
                PushMoneyByStoreSalepersonV2OutData outData = new PushMoneyByStoreSalepersonV2OutData();
                outData.setPlanName(tichengZ.getPlanName());
                outData.setStartDate(tichengZ.getPlanStartDate());
                outData.setEndtDate(tichengZ.getPlanEndDate());
                outData.setType("单品提成");
                if ("1".equals(tichengZ.getPlanStatus())) {
                    outData.setStatus("1");
                } else if ("2".equals(tichengZ.getDeleteFlag())) {
                    outData.setStatus("2");
                }
                outData.setStoCode(String.valueOf(sale.get("brId")));
                outData.setStoName(String.valueOf(sale.get("brName")));
                outData.setDays(BigDecimalUtil.toBigDecimal(sale.get("days")));
                outData.setAmt(BigDecimalUtil.toBigDecimal(sale.get("amt")));
                outData.setSalerId(String.valueOf(sale.get("salerId")));
                outData.setSalerName(String.valueOf(sale.get("salerName")));
                List<Map> saleData = saleDatas.stream().filter(s -> s.get("brId").equals(sale.get("brId")) && s.get("salerId").equals(sale.get("salerId"))).collect(Collectors.toList());

                BigDecimal tcPrices = BigDecimal.ZERO;
                //  我也不想的 怪我太菜了
                for (Map saleD : saleData) {
                    for (Map pro : tichengPros) {
                        if (saleD.get("proId").equals(pro.get("proId"))) {
                            BigDecimal tcPrice = BigDecimalUtil.multiply(saleD.get("qty"), pro.get("tcPrice"));
                            tcPrices = BigDecimalUtil.add(tcPrices, tcPrice);
                            break;
                        }
                    }
                }
                outData.setDeductionWage(tcPrices);
//                if(BigDecimalUtil.toBigDecimal(sale.get("amt")).compareTo(BigDecimal.ZERO) == 0 ){
                outData.setDeductionWageRate(BigDecimalUtil.divide(tcPrices, sale.get("amt"), 4));
//                }
                outDatas.add(outData);
            }
        }

        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            // 集合列的数据汇总
            PushMoneyByStoreSalepersonV2OutTotal outTotal = new PushMoneyByStoreSalepersonV2OutTotal();
            for (PushMoneyByStoreSalepersonV2OutData monthData : outDatas) {
//                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(), outTotal.getAmt()));
//                outTotal.setDays(BigDecimalUtil.add(monthData.getDays(), outTotal.getDays()));
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
//            if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
//                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(), outTotal.getAmt(), 4));
//            }
            pageInfo = new PageInfo(outDatas);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    @Override
    public PageInfo selectMonthPushListV2(PushMoneyByStoreV2InData inData) {
        List<PushMoneyListV2OutData> outDatas = new ArrayList<>();
        List<TichengZInfoBO> tichengz = new ArrayList<>();

        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate());
        tcMap.put("endDate", inData.getEndDate());
        tcMap.put("planId", inData.getPlanId());
        tcMap.put("source", inData.getSource());

        if (ObjectUtil.isEmpty(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
            tichengz.addAll(tichengProplanZMapper.selectAllByPro(tcMap));
        } else if ("1".equals(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
        } else if ("2".equals(inData.getType())) {
            tichengz.addAll(tichengProplanZMapper.selectAllByPro(tcMap));
        }
        PageInfo pageInfo;

        if (CollUtil.isNotEmpty(tichengz)) {
            for (TichengZInfoBO tc : tichengz) {
                PushMoneyListV2OutData outData = new PushMoneyListV2OutData();
                outData.setId(tc.getPlanId());
                outData.setPlanName(tc.getPlanName());
//                if("2".equals(inData.getType())){
////                    outData.setStartDate(inData.getTrialStartDate());
////                    outData.setEndtDate(inData.getTrialEndDate());
////                }else {
                outData.setStartDate(tc.getStartDate());
                outData.setEndtDate(tc.getEndtDate());
//                }

                outData.setType(tc.getType());
                outData.setStatus(tc.getStatus());
                outData.setStoArr(tc.getStoArr());


                if ("1".equals(outData.getType())) {
                    PageInfo sale = selectMonthPushBySaleV2(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), null, null, null));
                    PushMoneyByStoreV2OutTotal saleTotal = (PushMoneyByStoreV2OutTotal) sale.getListNum();
                    if (ObjectUtil.isNotEmpty(saleTotal)) {
                        List stoCount = outData.getStoArr();
                        outData.setStoCount(stoCount.size());
                        outData.setDays(saleTotal.getDays());
                        outData.setAmt(saleTotal.getAmt());
                        outData.setDeductionWage(saleTotal.getDeductionWage());
                        BigDecimal number = BigDecimalUtil.toBigDecimal(saleTotal.getDeductionWageRate());
                        outData.setDeductionWageRate(number);
                    }

                } else {
                    PageInfo sale = selectMonthPushByProV2(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), null, null, null));
                    PushMoneyByStoreV2OutTotal saleTotal = (PushMoneyByStoreV2OutTotal) sale.getListNum();
                    if (ObjectUtil.isNotEmpty(saleTotal)) {
                        List stoCount = outData.getStoArr();
                        outData.setStoCount(stoCount.size());
                        outData.setDays(saleTotal.getDays());
                        outData.setAmt(saleTotal.getAmt());
                        outData.setDeductionWage(saleTotal.getDeductionWage());
                        BigDecimal number = BigDecimalUtil.toBigDecimal(saleTotal.getDeductionWageRate());
                        outData.setDeductionWageRate(number);
                    }
                }
                outDatas.add(outData);
            }

            // 集合列的数据汇总
            PushMoneyListV2OutTotal outTotal = new PushMoneyListV2OutTotal();
            for (PushMoneyListV2OutData monthData : outDatas) {
//                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(),outTotal.getAmt()));
////                outTotal.setDays(BigDecimalUtil.add(monthData.getDays(),outTotal.getDays()));
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
//            if(outTotal.getAmt().compareTo(BigDecimal.ZERO)!=0) {
//                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(),outTotal.getAmt(),4));
//            }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    @Override
    public JsonResult getAdminFlag(GetLoginOutData userInfo) {
        int adminFlag = tichengPlanZMapper.getAdminFlag(userInfo.getClient(), userInfo.getUserId());
        return JsonResult.success(adminFlag, "success");
    }

    @Override
    public PageInfo selectMonthPushListV3Page(PushMoneyByStoreV2InData inData) {
        List<PushMoneyByStoreV2OutData> outDatas = new ArrayList<>();
        List<TichengZInfoBO> saleList = new ArrayList<>();
        List<TichengZInfoBO> proList = new ArrayList<>();
        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate());
        tcMap.put("endDate", inData.getEndDate());
        tcMap.put("planName", inData.getPlanName());
        tcMap.put("planId", inData.getPlanId());
        tcMap.put("source", inData.getSource());
        if (ObjectUtil.isEmpty(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        } else if ("1".equals(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
        } else if ("2".equals(inData.getType())) {
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        }
        if (CollUtil.isNotEmpty(saleList)) {
            for (TichengZInfoBO tc : saleList) {
                PageInfo sale = selectMonthPushBySaleV2(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), inData.getStoArr(), null, null));
                if (ObjectUtil.isNotEmpty(sale.getList())) {
                    outDatas.addAll(sale.getList());
                }
            }
        }
        if (CollUtil.isNotEmpty(proList)) {
            for (TichengZInfoBO tc : proList) {
                PageInfo sale = selectMonthPushByProV3(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), inData.getStoArr(), null, null));
                if (ObjectUtil.isNotEmpty(sale.getList())) {
                    outDatas.addAll(sale.getList());
                }
            }
        }
        // 集合列的数据汇总
        PushMoneyByStoreV2OutTotal outTotal = new PushMoneyByStoreV2OutTotal();
        for (PushMoneyByStoreV2OutData monthData : outDatas) {
//                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(),outTotal.getAmt()));
////                outTotal.setDays(BigDecimalUtil.add(monthData.getDays(),outTotal.getDays()));
            outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
        }
//            if(outTotal.getAmt().compareTo(BigDecimal.ZERO)!=0) {
//                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(),outTotal.getAmt(),4));
//            }
        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    @Override
    public PageInfo selectMonthPushSalespersonPageV3(PushMoneyBySalespersonV2InData inData) {
        List<PushMoneyByStoreSalepersonV2OutData> outDatas = new ArrayList<>();

        List<TichengZInfoBO> saleList = new ArrayList<>();
        List<TichengZInfoBO> proList = new ArrayList<>();
        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate());
        tcMap.put("endDate", inData.getEndDate());
        tcMap.put("planName", inData.getPlanName());
        tcMap.put("source", inData.getSource());

        if (ObjectUtil.isEmpty(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        } else if ("1".equals(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
        } else if ("2".equals(inData.getType())) {
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        }
        if (CollUtil.isNotEmpty(saleList)) {
            for (TichengZInfoBO tc : saleList) {
                PageInfo sale = selectMonthPushSalespersonBySaleV2(new PushMoneyBySalespersonV2InData(tc.getClient(), tc.getPlanId(), inData.getSalerId(), tc.getStartDate(), tc.getEndtDate(), inData.getStoArr()));
                if (ObjectUtil.isNotEmpty(sale.getList())) {
                    outDatas.addAll(sale.getList());
                }
            }
        }
        if (CollUtil.isNotEmpty(proList)) {
            for (TichengZInfoBO tc : proList) {
                List<PushMoneyByStoreSalepersonV2OutData> sale = selectMonthPushSalespersonByProV3(new PushMoneyBySalespersonV2InData(tc.getClient(), tc.getPlanId(), inData.getSalerId(), tc.getStartDate(), tc.getEndtDate(), inData.getStoArr()));
                if (ObjectUtil.isNotEmpty(sale)) {
                    outDatas.addAll(sale);
                }
            }
        }

        // 集合列的数据汇总
        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {

            // 集合列的数据汇总
            PushMoneyByStoreSalepersonV2OutTotal outTotal = new PushMoneyByStoreSalepersonV2OutTotal();
            for (PushMoneyByStoreSalepersonV2OutData monthData : outDatas) {
//                    outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(), outTotal.getAmt()));
////                    outTotal.setDays(BigDecimalUtil.add(monthData.getDays(), outTotal.getDays()));
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
//                if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
//                    outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(), outTotal.getAmt(), 4));
//                }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public PageInfo selectMonthPushListV5Optimize(PushMoneyByStoreV2InData inData) {
        //进行校验，时间必填
        if (StrUtil.isBlank(inData.getStartDate()) && StrUtil.isBlank(inData.getEndDate())) {
            throw new BusinessException("开始时间和结束时间必填");
        }

        List<PushMoneyByStoreV5TotalOutData> outDatas = new ArrayList<>();
        List<TichengZInfoBO> saleList = new ArrayList<>();
        List<TichengZInfoBO> tichengz = new ArrayList<>();
        List<TichengZInfoBO> proList = new ArrayList<>();
        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate().replaceAll("-", ""));
        tcMap.put("endDate", inData.getEndDate().replaceAll("-", ""));
        tcMap.put("planId", inData.getPlanId());
        tcMap.put("source", inData.getSource());
        if (StrUtil.isNotBlank(inData.getIfShowZplan()) && "1".equals(inData.getIfShowZplan()) && StrUtil.isNotBlank(inData.getCPlanName())) {
            tcMap.put("cPlanName", inData.getCPlanName());
        }
        if (StrUtil.isNotBlank(inData.getPlanName())) {
            tcMap.put("planName", inData.getPlanName());
        }

        // 1.销售 2.单品
        if (ObjectUtil.isEmpty(inData.getType())) {
            if (StrUtil.isNotBlank(inData.getCPlanName()) && !"默认".equals(inData.getCPlanName())) {
                saleList = new ArrayList<>();
            } else {
                saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
            }
            if (StrUtil.isNotBlank(inData.getIfShowZplan()) && "1".equals(inData.getIfShowZplan())) {
                proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
            } else {
                proList = tichengProplanZMapper.selectAllStoreByProTotal(tcMap);
            }
        } else if ("1".equals(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
        } else if ("2".equals(inData.getType())) {
            if (StrUtil.isNotBlank(inData.getIfShowZplan()) && "1".equals(inData.getIfShowZplan())) {
                proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
            } else {
                proList = tichengProplanZMapper.selectAllStoreByProTotal(tcMap);
            }
        }
        if (StrUtil.isNotBlank(inData.getCPlanName()) && !"默认".equals(inData.getCPlanName())) {
            saleList = new ArrayList<>();
        }
        tichengz.addAll(saleList);
        tichengz.addAll(proList);
        //sort
        tichengz.sort((o1, o2) -> {
            int cmp = o1.getStartDate().compareTo(o2.getStartDate());
            if (cmp == 0)
                cmp = o1.getEndtDate().compareTo(o2.getEndtDate());
            return cmp;
        });

        PageInfo pageInfo = new PageInfo();

        if (CollUtil.isNotEmpty(tichengz)) {
            // 遍历提成方案
            for (TichengZInfoBO tc : tichengz) {
                TiChengCaculateService caculateService = null;
                if ("2".equals(tc.getType())) {
                    // 单品提成
                    caculateService = new TiChangProCalculateServiceImpl(tichengPlanZMapper, tichengProplanBasicMapper,
                            tichengProplanStoMapper, tichengProplanSettingMapper, tichengProplanProNMapper,
                            kylinJdbcTemplate, userCommissionSummaryDetailMapper);
                } else if ("1".equals(tc.getType())) {
                    // 销售提成
                    caculateService = new TiChengSaleCaculateServiceImpl(tichengPlanZMapper, tichengSaleplanZMapper,
                            tichengSaleplanStoMapper, tichengSaleplanMMapper, tichengRejectClassMapper,
                            tichengRejectProMapper, kylinJdbcTemplate, gaiaProductBusinessMapper,
                            userCommissionSummaryDetailMapper);
                } else {
                    throw new BusinessException("非法的方案类型");
                }
                PushMoneyByStoreV5InData pushMoneyByStoreV5InData = new PushMoneyByStoreV5InData(tc.getClient(), inData.getSource(), tc.getPlanId(), null, tc.getPlanRejectDiscountRateSymbol(), tc.getPlanRejectDiscountRate());
                if ("2".equals(tc.getType())) {
                    // 只有单品提成有子方案
                    pushMoneyByStoreV5InData.setIfShowZplan(inData.getIfShowZplan());
                    pushMoneyByStoreV5InData.setSettingId(tc.getSettingId());
                }
                pushMoneyByStoreV5InData.setUserChooseEndDate(inData.getEndDate().replaceAll("-", ""));
                pushMoneyByStoreV5InData.setUserChooseStartDate(inData.getStartDate().replaceAll("-", ""));

                // 具体调用
                PageInfo sale = caculateService.caculatePlanOptimize(pushMoneyByStoreV5InData);
                List<PushMoneyByStoreV5TotalOutData> saleTotalList = sale.getList();
                if (ObjectUtil.isNotEmpty(saleTotalList)) {
                    outDatas.addAll(saleTotalList);
                }
            }

            inData.setPageNum(null);
            inData.setPageSize(null);
            if (inData.getPageNum() == null && inData.getPageSize() == null) {
                pageInfo.setList(outDatas);
                pageInfo.setTotal(outDatas.size());
                pageInfo.setPageNum(1);
                pageInfo.setPageSize(outDatas.size());
                pageInfo.setPages(1);
                PushMoneyByStoreV5TotalOutData totalOutData = handlePushMoneyByStoreV5Total(outDatas);
                pageInfo.setListNum(totalOutData);
                return pageInfo;
            }
            List list = startPage(outDatas, inData.getPageNum(), inData.getPageSize());
            pageInfo.setList(list);
            PushMoneyByStoreV5TotalOutData totalOutData = handlePushMoneyByStoreV5Total(list);
            pageInfo.setListNum(totalOutData);
            pageInfo.setTotal(outDatas.size());
            pageInfo.setPageNum(inData.getPageNum());
            pageInfo.setPageSize(inData.getPageSize());
            if (outDatas.size() / inData.getPageSize() == 0) {
                pageInfo.setPages(1);
            } else {
                pageInfo.setPages((outDatas.size() % inData.getPageSize() == 0) ? (outDatas.size() / inData.getPageSize()) : (outDatas.size() / inData.getPageSize() + 1));
            }
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    @Deprecated
    @Override
    public PageInfo selectMonthPushListV5(PushMoneyByStoreV2InData inData) {
        //进行校验，时间必填
        if (StrUtil.isBlank(inData.getStartDate()) && StrUtil.isBlank(inData.getEndDate())) {
            throw new BusinessException("开始时间和结束时间必填");
        }


        List<PushMoneyByStoreV5TotalOutData> outDatas = new ArrayList<>();
        List<TichengZInfoBO> saleList = new ArrayList<>();
        List<TichengZInfoBO> tichengz = new ArrayList<>();
        List<TichengZInfoBO> proList = new ArrayList<>();
        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate().replaceAll("-", ""));
        tcMap.put("endDate", inData.getEndDate().replaceAll("-", ""));
        tcMap.put("planId", inData.getPlanId());
        tcMap.put("source", inData.getSource());
        if (StrUtil.isNotBlank(inData.getIfShowZplan()) && "1".equals(inData.getIfShowZplan()) && StrUtil.isNotBlank(inData.getCPlanName())) {
            tcMap.put("cPlanName", inData.getCPlanName());
        }
        if (StrUtil.isNotBlank(inData.getPlanName())) {
            tcMap.put("planName", inData.getPlanName());
        }

        // 1.销售 2.单品
        if (ObjectUtil.isEmpty(inData.getType())) {
            if (StrUtil.isNotBlank(inData.getCPlanName()) && !"默认".equals(inData.getCPlanName())) {
                saleList = new ArrayList<>();
            }
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);

            if (StrUtil.isNotBlank(inData.getIfShowZplan()) && "1".equals(inData.getIfShowZplan())) {
                proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
            } else {
                proList = tichengProplanZMapper.selectAllStoreByProTotal(tcMap);
            }
        } else if ("1".equals(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
        } else if ("2".equals(inData.getType())) {
            if (StrUtil.isNotBlank(inData.getIfShowZplan()) && "1".equals(inData.getIfShowZplan())) {
                proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
            } else {
                proList = tichengProplanZMapper.selectAllStoreByProTotal(tcMap);
            }
        }
        if (StrUtil.isNotBlank(inData.getCPlanName()) && !"默认".equals(inData.getCPlanName())) {
            saleList = new ArrayList<>();
        }
        tichengz.addAll(saleList);
        tichengz.addAll(proList);
        //sort
        tichengz.sort((o1, o2) -> {
            int cmp = o1.getStartDate().compareTo(o2.getStartDate());
            if (cmp == 0)
                cmp = o1.getEndtDate().compareTo(o2.getEndtDate());
            return cmp;
        });

        PageInfo pageInfo = new PageInfo();

        if (CollUtil.isNotEmpty(tichengz)) {
            for (TichengZInfoBO tc : tichengz) {
                TiChengCaculateService caculateService = null;
                if ("2".equals(tc.getType())) {
                    caculateService = new TiChangProCalculateServiceImpl(tichengPlanZMapper, tichengProplanBasicMapper,
                            tichengProplanStoMapper, tichengProplanSettingMapper, tichengProplanProNMapper,
                            kylinJdbcTemplate);
                } else if ("1".equals(tc.getType())) {
                    caculateService = new TiChengSaleCaculateServiceImpl(tichengPlanZMapper, tichengSaleplanZMapper,
                            tichengSaleplanStoMapper, tichengSaleplanMMapper, tichengRejectClassMapper,
                            tichengRejectProMapper, kylinJdbcTemplate, gaiaProductBusinessMapper);
                } else {
                    throw new BusinessException("非法的方案类型");
                }
                PushMoneyByStoreV5InData pushMoneyByStoreV5InData = new PushMoneyByStoreV5InData(tc.getClient(), inData.getSource(), tc.getPlanId(), null, tc.getPlanRejectDiscountRateSymbol(), tc.getPlanRejectDiscountRate());
                if ("2".equals(tc.getType())) {
                    pushMoneyByStoreV5InData.setIfShowZplan(inData.getIfShowZplan());
                    pushMoneyByStoreV5InData.setSettingId(tc.getSettingId());
                }
                pushMoneyByStoreV5InData.setUserChooseEndDate(inData.getEndDate().replaceAll("-", ""));
                pushMoneyByStoreV5InData.setUserChooseStartDate(inData.getStartDate().replaceAll("-", ""));

                PageInfo sale = caculateService.caculatePlan(pushMoneyByStoreV5InData);
                List<PushMoneyByStoreV5TotalOutData> saleTotalList = sale.getList();
                if (ObjectUtil.isNotEmpty(saleTotalList)) {
                    outDatas.addAll(saleTotalList);
                }
            }

            inData.setPageNum(null);
            inData.setPageSize(null);
            if (inData.getPageNum() == null && inData.getPageSize() == null) {
                pageInfo.setList(outDatas);
                pageInfo.setTotal(outDatas.size());
                pageInfo.setPageNum(1);
                pageInfo.setPageSize(outDatas.size());
                pageInfo.setPages(1);
                PushMoneyByStoreV5TotalOutData totalOutData = handlePushMoneyByStoreV5Total(outDatas);
                pageInfo.setListNum(totalOutData);
                return pageInfo;
            }
            List list = startPage(outDatas, inData.getPageNum(), inData.getPageSize());
            pageInfo.setList(list);
            PushMoneyByStoreV5TotalOutData totalOutData = handlePushMoneyByStoreV5Total(list);
            pageInfo.setListNum(totalOutData);
            pageInfo.setTotal(outDatas.size());
            pageInfo.setPageNum(inData.getPageNum());
            pageInfo.setPageSize(inData.getPageSize());
            if (outDatas.size() / inData.getPageSize() == 0) {
                pageInfo.setPages(1);
            } else {
                pageInfo.setPages((outDatas.size() % inData.getPageSize() == 0) ? (outDatas.size() / inData.getPageSize()) : (outDatas.size() / inData.getPageSize() + 1));
            }
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    private PushMoneyByStoreV5TotalOutData handlePushMoneyByStoreV5Total(List<PushMoneyByStoreV5TotalOutData> dataList) {
        if (CollectionUtil.isEmpty(dataList)) {
            return null;
        }
        // 集合列的数据汇总
        PushMoneyByStoreV5TotalOutData outTotal = new PushMoneyByStoreV5TotalOutData();
        outTotal.setTcCostAmt(BigDecimal.ZERO);
        outTotal.setTcYsAmt(BigDecimal.ZERO);
        outTotal.setTcAmt(BigDecimal.ZERO);
        outTotal.setTcGrossProfitAmt(BigDecimal.ZERO);
        outTotal.setTiTotal(BigDecimal.ZERO);

        for (PushMoneyByStoreV5TotalOutData monthData : dataList) {
            outTotal.setTcCostAmt(monthData.getTcCostAmt().add(outTotal.getTcCostAmt()));
            outTotal.setTcAmt(monthData.getTcAmt().add(outTotal.getTcAmt()));
            outTotal.setTcGrossProfitAmt(monthData.getTcGrossProfitAmt().add(outTotal.getTcGrossProfitAmt()));
            outTotal.setTiTotal(monthData.getTiTotal().add(outTotal.getTiTotal()));
        }
        outTotal.setPlanName("合计");
        outTotal.setTcGrossProfitRate(outTotal.getTcAmt().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : outTotal.getTcGrossProfitAmt().divide(outTotal.getTcAmt(), 4, RoundingMode.HALF_UP));
        return outTotal;
    }

    @Override
    public Result exportMonthPushListV5(PushMoneyByStoreV2InData inData, HttpServletResponse response) {
        String fileName = "方案提成汇总.csv";
        ByteArrayOutputStream bos = null;
        try {
            PageInfo pageInfo = this.selectMonthPushListV5Optimize(inData);
            List<PushMoneyByStoreV5TotalOutData> list = pageInfo.getList();
            if (CollectionUtil.isNotEmpty(list)) {
                // 获取加盟商下，销售提成和单品提成关联门店
                String client = inData.getClient();
                List<StoreSimpleInfoWithPlan> storeSimpleInfoWithPlans = this.selectStoreListWithPlanCommission(client, null);

                // 根据方案id分组
                Map<Integer, List<StoreSimpleInfoWithPlan>> storeSimpleInfoByPlanMap = storeSimpleInfoWithPlans.stream()
                        .collect(Collectors.groupingBy(StoreSimpleInfoWithPlan::getPlanId));

                Map<String, String> storeInfoMapWithPlan = new HashMap<>();
                for (Map.Entry<Integer, List<StoreSimpleInfoWithPlan>> entry : storeSimpleInfoByPlanMap.entrySet()) {
                    // 将门店集合转成逗号分割字符串
                    storeInfoMapWithPlan.put(entry.getKey() + "", Joiner.on("、")
                            .join(entry.getValue().stream()
                                    .map(StoreSimpleInfoWithPlan::getStoName)
                                    .collect(Collectors.toList())));
                }

                // 获取导出文件标题
                List<String> titles = filterPlanCommissionSummaryShowField(inData);

                bos = new ByteArrayOutputStream();
                PrintWriter writer = CsvClient.renderTitle(titles, bos);
                StringJoiner stringJoiner = null;

                for (PushMoneyByStoreV5TotalOutData pushMoneyByStoreV5TotalOutData : list) {
                    stringJoiner = new StringJoiner(StrUtil.COMMA);
                    List<Field> fields = Arrays.stream(pushMoneyByStoreV5TotalOutData.getClass().getDeclaredFields())
                            .filter(item -> item.isAnnotationPresent(CommissionReportField.class))
                            .collect(Collectors.toList());
                    Integer planId = pushMoneyByStoreV5TotalOutData.getPlanId();
                    for (Field field : fields) {
                        CommissionReportField annotation = field.getAnnotation(CommissionReportField.class);
                        if (assertSatisfyCondition(inData, field, annotation)) {
                            continue;
                        }
                        String name = field.getName();
                        field.setAccessible(true);
                        Object obj;
                        try {
                            obj = Optional.ofNullable(field.get(pushMoneyByStoreV5TotalOutData)).orElse(StrUtil.EMPTY);
                        } catch (Exception e) {
                            obj = StrUtil.EMPTY;
                        }
                        String val = String.valueOf(obj);
                        if ("effectiveStore".equals(name)) {
                            // 生效门店字段，根据方案id获取对应的门店集合，逗号分割展示
                            val = storeInfoMapWithPlan.get(String.valueOf(planId));
                        } else {
                            String suffix = annotation.suffix();
                            if (StringUtils.isNotBlank(val) && StringUtils.isNotBlank(suffix)) {
                                val = BigDecimalUtil.formatWithPercent(val) + suffix;
                            }
                        }
                        stringJoiner.add(val + " ");
                    }
                    stringJoiner.add("\r ");
                    writer.print(stringJoiner);
                }
                writer.flush();

                // 渲染合计数据
                PushMoneyByStoreV5TotalOutData outTotal = (PushMoneyByStoreV5TotalOutData) pageInfo.getListNum();
                stringJoiner = new StringJoiner(StrUtil.COMMA);
                List<Field> fields = Arrays.stream(outTotal.getClass().getDeclaredFields())
                        .filter(item -> item.isAnnotationPresent(CommissionReportField.class))
                        .collect(Collectors.toList());
                for (Field field : fields) {
                    CommissionReportField annotation = field.getAnnotation(CommissionReportField.class);
                    if (assertSatisfyCondition(inData, field, annotation)) {
                        continue;
                    }
                    String name = field.getName();
                    field.setAccessible(true);
                    Object obj;
                    try {
                        obj = Optional.ofNullable(field.get(outTotal)).orElse(StrUtil.EMPTY);
                    } catch (Exception e) {
                        obj = StrUtil.EMPTY;
                    }
                    String val = String.valueOf(obj);
                    if (!annotation.isTotalItem()) {
                        val = "";
                    }
                    if ("planName".equals(name)) {
                        val = "合计";
                    }
                    String suffix = annotation.suffix();
                    if (StringUtils.isNotBlank(val) && StringUtils.isNotBlank(suffix)) {
                        val = BigDecimalUtil.formatWithPercent(val) + suffix;
                    }
                    stringJoiner.add(val + " ");
                }
                stringJoiner.add("\r ");
                writer.print(stringJoiner);
                writer.flush();
                return cosUtils.uploadFile(bos, fileName);
            }
        } catch (Exception e) {
            log.error("exportMonthPushListV5 : {}", e.getMessage());
            throw new BusinessException("下载失败!");
        } finally {
            IoUtil.close(bos);
        }
        return Result.error("下载失败!");
    }

    @Override
    public PageInfo selectMonthPushListV3(PushMoneyByStoreV2InData inData) {
        List<PushMoneyListV2OutData> outDatas = new ArrayList<>();
        List<TichengZInfoBO> tichengz = new ArrayList<>();

        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate());
        tcMap.put("endDate", inData.getEndDate());
        tcMap.put("planId", inData.getPlanId());
        tcMap.put("source", inData.getSource());

        // 1.销售 2.单品
        if (ObjectUtil.isEmpty(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
            tichengz.addAll(tichengProplanZMapper.selectAllStoreByPro(tcMap));
        } else if ("1".equals(inData.getType())) {
            tichengz.addAll(tichengSaleplanZMapper.selectAllBySale(tcMap));
        } else if ("2".equals(inData.getType())) {
            tichengz.addAll(tichengProplanZMapper.selectAllStoreByPro(tcMap));
        }
        PageInfo pageInfo;

        if (CollUtil.isNotEmpty(tichengz)) {
            for (TichengZInfoBO tc : tichengz) {
                PushMoneyListV2OutData outData = new PushMoneyListV2OutData();
                outData.setId(tc.getPlanId());
                outData.setPlanName(tc.getPlanName());
//                if("2".equals(inData.getType())){
////                    outData.setStartDate(inData.getTrialStartDate());
////                    outData.setEndtDate(inData.getTrialEndDate());
////                }else {
                outData.setStartDate(tc.getStartDate());
                outData.setEndtDate(tc.getEndtDate());
//                }

                outData.setType(tc.getType());
                outData.setStatus(tc.getStatus());
                outData.setStoArr(tc.getStoArr());
                outData.setStoCount(tc.getStoArr().size());

                if ("1".equals(outData.getType())) {
                    PageInfo sale = selectMonthPushBySaleV2(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), null, tc.getPlanRejectDiscountRateSymbol(), tc.getPlanRejectDiscountRate()));
                    PushMoneyByStoreV2OutTotal saleTotal = (PushMoneyByStoreV2OutTotal) sale.getListNum();
                    if (ObjectUtil.isNotEmpty(saleTotal)) {
                        List stoCount = outData.getStoArr();
                        outData.setStoCount(stoCount.size());
                        outData.setDays(saleTotal.getDays());
                        outData.setAmt(saleTotal.getAmt());
                        outData.setDeductionWage(saleTotal.getDeductionWage());
                        BigDecimal number = BigDecimalUtil.toBigDecimal(saleTotal.getDeductionWageRate());
                        outData.setDeductionWageRate(number);
                    }

                } else {
                    PageInfo sale = selectMonthPushByProV3(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), null, tc.getPlanRejectDiscountRateSymbol(), tc.getPlanRejectDiscountRate()));
                    PushMoneyByStoreV2OutTotal saleTotal = (PushMoneyByStoreV2OutTotal) sale.getListNum();
                    if (ObjectUtil.isNotEmpty(saleTotal)) {
                        List stoCount = outData.getStoArr();
                        outData.setStoCount(stoCount.size());
                        outData.setDays(saleTotal.getDays());
                        outData.setAmt(saleTotal.getAmt());
                        outData.setDeductionWage(saleTotal.getDeductionWage());
                        BigDecimal number = BigDecimalUtil.toBigDecimal(saleTotal.getDeductionWageRate());
                        outData.setDeductionWageRate(number);
                    }
                }
                outDatas.add(outData);
            }

            // 集合列的数据汇总
            PushMoneyListV2OutTotal outTotal = new PushMoneyListV2OutTotal();
            for (PushMoneyListV2OutData monthData : outDatas) {
//                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(),outTotal.getAmt()));
////                outTotal.setDays(BigDecimalUtil.add(monthData.getDays(),outTotal.getDays()));
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
//            if(outTotal.getAmt().compareTo(BigDecimal.ZERO)!=0) {
//                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(),outTotal.getAmt(),4));
//            }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    @Override
    public PageInfo selectPercentageStoreV3(PushMoneyByStoreV2InData inData) {
        List<PushMoneyByStoreV2OutData> outDatas = new ArrayList<>();
        // 销售提成方案
        List<TichengZInfoBO> saleList = new ArrayList<>();
        // 单品提成方案
        List<TichengZInfoBO> proList = new ArrayList<>();
        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate());
        tcMap.put("endDate", inData.getEndDate());
        tcMap.put("planName", inData.getPlanName());
        tcMap.put("planId", inData.getPlanId());
        tcMap.put("source", inData.getSource());
        tcMap.put("stoCode", inData.getStoCode());
        if (ObjectUtil.isEmpty(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        } else if ("1".equals(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
        } else if ("2".equals(inData.getType())) {
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        }
        List<String> sto = new ArrayList<>();
        sto.add(inData.getStoCode());
        inData.setStoArr(sto);
        if (CollUtil.isNotEmpty(saleList)) {
            for (TichengZInfoBO tc : saleList) {
                PageInfo sale = selectMonthPushBySaleV2(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), inData.getStoArr(), tc.getPlanRejectDiscountRateSymbol(), tc.getPlanRejectDiscountRate()));
                if (ObjectUtil.isNotEmpty(sale.getList())) {
                    outDatas.addAll(sale.getList());
                }
            }
        }
        if (CollUtil.isNotEmpty(proList)) {
            for (TichengZInfoBO tc : proList) {
                PageInfo sale = selectMonthPushByProV3(new PushMoneyByStoreV2InData(tc.getClient(), inData.getSource(), tc.getPlanId(), inData.getStoArr(), tc.getPlanRejectDiscountRateSymbol(), tc.getPlanRejectDiscountRate()));
                if (ObjectUtil.isNotEmpty(sale.getList())) {
                    outDatas.addAll(sale.getList());
                }
            }
        }
        // 集合列的数据汇总
        PushMoneyByStoreV2OutTotal outTotal = new PushMoneyByStoreV2OutTotal();
        for (PushMoneyByStoreV2OutData monthData : outDatas) {
//                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(),outTotal.getAmt()));
////                outTotal.setDays(BigDecimalUtil.add(monthData.getDays(),outTotal.getDays()));
            outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
        }
//            if(outTotal.getAmt().compareTo(BigDecimal.ZERO)!=0) {
//                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(),outTotal.getAmt(),4));
//            }
        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }


    @Override
    public PageInfo<StoreCommissionSummary> selectPercentageStoreV5Optimize(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        if (StringUtils.isBlank(storeCommissionSummaryDO.getStartDate())) {
            throw new BusinessException("起始日期不能为空!");
        }
        if (StringUtils.isBlank(storeCommissionSummaryDO.getEndDate())) {
            throw new BusinessException("结束日期不能为空!");
        }

        // 1.销售 2.单品
        String type = storeCommissionSummaryDO.getType();
        TiChengCaculateService caculateService;
        List<StoreCommissionSummary> storeCommissionSummaries = new ArrayList<>();

        Integer summaryType = storeCommissionSummaryDO.getSummaryType();
        if (summaryType == 1) {
            storeCommissionSummaryDO.setShowStore(true);
        }

        if (!storeCommissionSummaryDO.getAdmin()) {
            // 获取是否显示成本的配置
            List<Map<String, String>> costAmtShowConfig = gaiaGlobalDataMapper.getCostAmtShowConfig(storeCommissionSummaryDO.getClient());
            Map<String, Boolean> costAmtShowConfigMap = costAmtShowConfig.stream()
                    .collect(Collectors.toMap(s -> s.get("stoCode"), s -> MapUtil.getBool(s, "type")));
            storeCommissionSummaryDO.setCostAmtShowConfigMap(costAmtShowConfigMap);
        }

        if ("1".equals(type)) {
            caculateService = new TiChengSaleCaculateServiceImpl(tichengPlanZMapper, tichengSaleplanZMapper,
                    tichengSaleplanStoMapper, tichengSaleplanMMapper, tichengRejectClassMapper, tichengRejectProMapper,
                    kylinJdbcTemplate, gaiaProductBusinessMapper, userCommissionSummaryDetailMapper);
            List<StoreCommissionSummary> saleCommissionSummary =
                    caculateService.calcStoreCommissionSummaryOptimize(storeCommissionSummaryDO);
            if (CollectionUtil.isNotEmpty(saleCommissionSummary)) {
                storeCommissionSummaries.addAll(saleCommissionSummary);
            }
        } else if ("2".equals(type)) {
            caculateService = new TiChangProCalculateServiceImpl(tichengPlanZMapper, tichengProplanBasicMapper,
                    tichengProplanStoMapper, tichengProplanSettingMapper, tichengProplanProNMapper,
                    kylinJdbcTemplate, userCommissionSummaryDetailMapper);
            List<StoreCommissionSummary> proCommissionSummary = caculateService.calcStoreCommissionSummaryOptimize(storeCommissionSummaryDO);
            if (CollectionUtil.isNotEmpty(proCommissionSummary)) {
                storeCommissionSummaries.addAll(proCommissionSummary);
            }
        } else {
            String startDate = storeCommissionSummaryDO.getStartDate();
            String endDate = storeCommissionSummaryDO.getEndDate();
            caculateService = new TiChangProCalculateServiceImpl(tichengPlanZMapper, tichengProplanBasicMapper,
                    tichengProplanStoMapper, tichengProplanSettingMapper, tichengProplanProNMapper,
                    kylinJdbcTemplate, userCommissionSummaryDetailMapper);
            List<StoreCommissionSummary> proCommissionSummary = caculateService.calcStoreCommissionSummaryOptimize(storeCommissionSummaryDO);
            if (CollectionUtil.isNotEmpty(proCommissionSummary)) {
                storeCommissionSummaries.addAll(proCommissionSummary);
            }
            caculateService = new TiChengSaleCaculateServiceImpl(tichengPlanZMapper, tichengSaleplanZMapper,
                    tichengSaleplanStoMapper, tichengSaleplanMMapper, tichengRejectClassMapper,
                    tichengRejectProMapper, kylinJdbcTemplate, gaiaProductBusinessMapper,
                    userCommissionSummaryDetailMapper);
            storeCommissionSummaryDO.setStartDate(startDate);
            storeCommissionSummaryDO.setEndDate(endDate);
            List<StoreCommissionSummary> saleCommissionSummary = caculateService.calcStoreCommissionSummaryOptimize(storeCommissionSummaryDO);
            if (CollectionUtil.isNotEmpty(saleCommissionSummary)) {
                storeCommissionSummaries.addAll(saleCommissionSummary);
            }
        }

        if (CollectionUtil.isNotEmpty(storeCommissionSummaries)) {
            if (summaryType == 2) {
                storeCommissionSummaries = storeCommissionSummaries.stream()
                        .sorted(Comparator.comparing(StoreCommissionSummary::getPlanStartDate)
                                .thenComparing(StoreCommissionSummary::getPlanEndDate)
                                .thenComparing(StoreCommissionSummary::getPlanType)
                                .thenComparing(StoreCommissionSummary::getStoCode)
                                .thenComparing(StoreCommissionSummary::getSaleManCode))
                        .collect(Collectors.toList());
            } else {
                storeCommissionSummaries = storeCommissionSummaries.stream()
                        .sorted(Comparator.comparing(StoreCommissionSummary::getPlanStartDate)
                                .thenComparing(StoreCommissionSummary::getPlanEndDate)
                                .thenComparing(StoreCommissionSummary::getPlanType)
                                .thenComparing(StoreCommissionSummary::getStoCode))
                        .collect(Collectors.toList());
            }

            // 所有数据汇总
            Iterator<StoreCommissionSummary> iterator = storeCommissionSummaries.iterator();
            StoreCommissionSummary total = new StoreCommissionSummary();
            // 销售天数
            total.setPlanName("合计");

            List<StoreCommissionSummary> tempCommissionSummary = new ArrayList<>();
            while (iterator.hasNext()) {
                StoreCommissionSummary next = iterator.next();
                BigDecimal commissionAmt = BigDecimalUtil.toBigDecimal(next.getCommissionAmt());
                BigDecimal commissionCostAmt = next.getCommissionCostAmt();
                BigDecimal commissionSales = next.getCommissionSales();
                BigDecimal commissionGrossProfitAmt = next.getCommissionGrossProfitAmt();
                total.setCommissionCostAmt(BigDecimalUtil.add(total.getCommissionCostAmt(), commissionCostAmt));
                total.setCommissionSales(BigDecimalUtil.add(total.getCommissionSales(), commissionSales));
                total.setCommissionGrossProfitAmt(BigDecimalUtil.add(total.getCommissionGrossProfitAmt(), commissionGrossProfitAmt));
                total.setCommissionAmt(BigDecimalUtil.add(total.getCommissionAmt(), commissionAmt));
                BigDecimal grossProfitAmt = next.getGrossProfitAmt();
                if (commissionAmt.compareTo(BigDecimal.ZERO) != 0) {
                    tempCommissionSummary.add(next);
                } else if (commissionAmt.compareTo(BigDecimal.ZERO) == 0 && grossProfitAmt != null && grossProfitAmt.compareTo(BigDecimal.ZERO) == 0) {
                    tempCommissionSummary.add(next);
                }

                // 小数位格式化
                next.setCommissionAmt(BigDecimalUtil.format(commissionAmt, 2));
                next.setCommissionSales(BigDecimalUtil.format(commissionSales, 2));
                next.setCommissionCostAmt(BigDecimalUtil.format(commissionCostAmt, 2));
                next.setCommissionGrossProfitAmt(BigDecimalUtil.format(commissionGrossProfitAmt, 2));
            }

            if (CollectionUtil.isNotEmpty(tempCommissionSummary)) {
                // 提成商品毛利率 提成商品毛利额/提成商品实收金额
                total.setCommissionGrossProfitRate(BigDecimalUtil.divide(total.getCommissionGrossProfitAmt(), total.getCommissionSales(), 4));

                // 小数位格式化
                total.setCommissionAmt(BigDecimalUtil.format(total.getCommissionAmt(), 2));
                total.setCommissionSales(BigDecimalUtil.format(total.getCommissionSales(), 2));
                total.setCommissionCostAmt(BigDecimalUtil.format(total.getCommissionCostAmt(), 2));
                total.setCommissionGrossProfitAmt(BigDecimalUtil.format(total.getCommissionGrossProfitAmt(), 2));

                PageInfo<StoreCommissionSummary> pageInfo = new PageInfo<>();
//                Integer pageNum = storeCommissionSummaryDO.getPageNum();
//                Integer pageSize = storeCommissionSummaryDO.getPageSize();
//                if (pageNum != null && pageSize != null) {
//                    int size = tempCommissionSummary.size();
//                    // 分页
//                    pageInfo.setTotal(size);
//                    pageInfo.setPageNum(pageNum);
//                    pageInfo.setPageSize(pageSize);
//                    pageInfo.setPages((size % pageSize == 0) ? (size / pageSize) : (size / pageSize + 1));
//                    pageInfo.setListNum(size == 0 ? null : total);
//                    //计算开始索引
//                    int fromIndex = (pageNum - 1) * pageSize;
//                    //计算结束索引
//                    int toIndex = (size - fromIndex) > pageSize ? fromIndex + pageSize : size;
//                    pageInfo.setList(tempCommissionSummary.subList(fromIndex, toIndex));
//                } else {
                    int size = tempCommissionSummary.size();
                    // 分页
                    pageInfo.setTotal(size);
                    pageInfo.setListNum(size == 0 ? null : total);
                    pageInfo.setList(tempCommissionSummary);
//                }
                return pageInfo;
            }
        }
        return new PageInfo();
    }

    @Override
    public PageInfo<StoreCommissionSummary> selectPercentageStoreV5(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        if (StringUtils.isBlank(storeCommissionSummaryDO.getStartDate())) {
            throw new BusinessException("起始日期不能为空!");
        }
        if (StringUtils.isBlank(storeCommissionSummaryDO.getEndDate())) {
            throw new BusinessException("结束日期不能为空!");
        }

        // 1.销售 2.单品
        String type = storeCommissionSummaryDO.getType();
        TiChengCaculateService caculateService;
        List<StoreCommissionSummary> storeCommissionSummaries = new ArrayList<>();
        Set<String> totalSaleDays = new HashSet<>();

        if (!storeCommissionSummaryDO.getAdmin()) {
            // 获取是否显示成本的配置
            List<Map<String, String>> costAmtShowConfig = gaiaGlobalDataMapper.getCostAmtShowConfig(storeCommissionSummaryDO.getClient());
            Map<String, Boolean> costAmtShowConfigMap = costAmtShowConfig.stream()
                    .collect(Collectors.toMap(s -> s.get("stoCode"), s -> MapUtil.getBool(s, "type")));
            storeCommissionSummaryDO.setCostAmtShowConfigMap(costAmtShowConfigMap);
        }

        if ("1".equals(type)) {
            caculateService = new TiChengSaleCaculateServiceImpl(tichengPlanZMapper, tichengSaleplanZMapper,
                    tichengSaleplanStoMapper, tichengSaleplanMMapper, tichengRejectClassMapper, tichengRejectProMapper,
                    kylinJdbcTemplate, gaiaProductBusinessMapper);
            List<StoreCommissionSummary> saleCommissionSummary = caculateService.calcStoreCommissionSummary(storeCommissionSummaryDO);
            if (CollectionUtil.isNotEmpty(saleCommissionSummary)) {
                totalSaleDays.addAll(saleCommissionSummary.get(0).getTotalSaleDays());
                storeCommissionSummaries.addAll(saleCommissionSummary);
            }
        } else if ("2".equals(type)) {
            caculateService = new TiChangProCalculateServiceImpl(tichengPlanZMapper, tichengProplanBasicMapper,
                    tichengProplanStoMapper, tichengProplanSettingMapper, tichengProplanProNMapper,
                    kylinJdbcTemplate);
            List<StoreCommissionSummary> proCommissionSummary = caculateService.calcStoreCommissionSummary(storeCommissionSummaryDO);
            if (CollectionUtil.isNotEmpty(proCommissionSummary)) {
                totalSaleDays.addAll(proCommissionSummary.get(0).getTotalSaleDays());
                storeCommissionSummaries.addAll(proCommissionSummary);
            }
        } else {
            String startDate = storeCommissionSummaryDO.getStartDate();
            String endDate = storeCommissionSummaryDO.getEndDate();
            caculateService = new TiChangProCalculateServiceImpl(tichengPlanZMapper, tichengProplanBasicMapper,
                    tichengProplanStoMapper, tichengProplanSettingMapper, tichengProplanProNMapper,
                    kylinJdbcTemplate);
            List<StoreCommissionSummary> proCommissionSummary = caculateService.calcStoreCommissionSummary(storeCommissionSummaryDO);
            if (CollectionUtil.isNotEmpty(proCommissionSummary)) {
                totalSaleDays.addAll(proCommissionSummary.get(0).getTotalSaleDays());
                storeCommissionSummaries.addAll(proCommissionSummary);
            }
            caculateService = new TiChengSaleCaculateServiceImpl(tichengPlanZMapper, tichengSaleplanZMapper,
                    tichengSaleplanStoMapper, tichengSaleplanMMapper, tichengRejectClassMapper,
                    tichengRejectProMapper, kylinJdbcTemplate, gaiaProductBusinessMapper);
            storeCommissionSummaryDO.setStartDate(startDate);
            storeCommissionSummaryDO.setEndDate(endDate);
            List<StoreCommissionSummary> saleCommissionSummary = caculateService.calcStoreCommissionSummary(storeCommissionSummaryDO);
            if (CollectionUtil.isNotEmpty(saleCommissionSummary)) {
                totalSaleDays.addAll(saleCommissionSummary.get(0).getTotalSaleDays());
                storeCommissionSummaries.addAll(saleCommissionSummary);
            }
        }

        if (CollectionUtil.isNotEmpty(storeCommissionSummaries)) {
            Integer summaryType = storeCommissionSummaryDO.getSummaryType();

            if (summaryType == 2) {
                storeCommissionSummaries = storeCommissionSummaries.stream()
                        .sorted(Comparator.comparing(StoreCommissionSummary::getPlanStartDate)
                                .thenComparing(StoreCommissionSummary::getPlanEndDate)
                                .thenComparing(StoreCommissionSummary::getPlanType)
                                .thenComparing(StoreCommissionSummary::getStoCode)
                                .thenComparing(StoreCommissionSummary::getSaleManCode))
                        .collect(Collectors.toList());
            } else {
                storeCommissionSummaries = storeCommissionSummaries.stream()
                        .sorted(Comparator.comparing(StoreCommissionSummary::getPlanStartDate)
                                .thenComparing(StoreCommissionSummary::getPlanEndDate)
                                .thenComparing(StoreCommissionSummary::getPlanType)
                                .thenComparing(StoreCommissionSummary::getStoCode))
                        .collect(Collectors.toList());
            }

            // 所有数据汇总
            Iterator<StoreCommissionSummary> iterator = storeCommissionSummaries.iterator();
            StoreCommissionSummary total = new StoreCommissionSummary();
            // 销售天数
            total.setPlanName("合计");

            List<StoreCommissionSummary> tempCommissionSummary = new ArrayList<>();
            while (iterator.hasNext()) {
                StoreCommissionSummary next = iterator.next();
                BigDecimal commissionAmt = BigDecimalUtil.toBigDecimal(next.getCommissionAmt());
                BigDecimal commissionCostAmt = next.getCommissionCostAmt();
                BigDecimal commissionSales = next.getCommissionSales();
                BigDecimal commissionGrossProfitAmt = next.getCommissionGrossProfitAmt();
                total.setCommissionCostAmt(BigDecimalUtil.add(total.getCommissionCostAmt(), commissionCostAmt));
                total.setCommissionSales(BigDecimalUtil.add(total.getCommissionSales(), commissionSales));
                total.setCommissionGrossProfitAmt(BigDecimalUtil.add(total.getCommissionGrossProfitAmt(), commissionGrossProfitAmt));
                total.setCommissionAmt(BigDecimalUtil.add(total.getCommissionAmt(), commissionAmt));
                BigDecimal grossProfitAmt = next.getGrossProfitAmt();
                if (commissionAmt.compareTo(BigDecimal.ZERO) != 0) {
                    tempCommissionSummary.add(next);
                } else if (commissionAmt.compareTo(BigDecimal.ZERO) == 0 && grossProfitAmt != null && grossProfitAmt.compareTo(BigDecimal.ZERO) == 0) {
                    tempCommissionSummary.add(next);
                }

                // 小数位格式化
                next.setCommissionAmt(BigDecimalUtil.format(commissionAmt, 2));
                next.setCommissionSales(BigDecimalUtil.format(commissionSales, 2));
                next.setCommissionCostAmt(BigDecimalUtil.format(commissionCostAmt, 2));
                next.setCommissionGrossProfitAmt(BigDecimalUtil.format(commissionGrossProfitAmt, 2));
            }

            if (CollectionUtil.isNotEmpty(tempCommissionSummary)) {
                // 提成商品毛利率 提成商品毛利额/提成商品实收金额
                total.setCommissionGrossProfitRate(BigDecimalUtil.divide(total.getCommissionGrossProfitAmt(), total.getCommissionSales(), 4));

                // 小数位格式化
                total.setCommissionAmt(BigDecimalUtil.format(total.getCommissionAmt(), 2));
                total.setCommissionSales(BigDecimalUtil.format(total.getCommissionSales(), 2));
                total.setCommissionCostAmt(BigDecimalUtil.format(total.getCommissionCostAmt(), 2));
                total.setCommissionGrossProfitAmt(BigDecimalUtil.format(total.getCommissionGrossProfitAmt(), 2));

                storeCommissionSummaryDO.setPageNum(null);
                storeCommissionSummaryDO.setPageSize(null);
                PageInfo<StoreCommissionSummary> pageInfo = new PageInfo<>();
                Integer pageNum = storeCommissionSummaryDO.getPageNum();
                Integer pageSize = storeCommissionSummaryDO.getPageSize();
                if (pageNum != null && pageSize != null) {
                    int size = tempCommissionSummary.size();
                    // 分页
                    pageInfo.setTotal(size);
                    pageInfo.setPageNum(pageNum);
                    pageInfo.setPageSize(pageSize);
                    pageInfo.setPages((size % pageSize == 0) ? (size / pageSize) : (size / pageSize + 1));
                    pageInfo.setListNum(size == 0 ? null : total);
                    //计算开始索引
                    int fromIndex = (pageNum - 1) * pageSize;
                    //计算结束索引
                    int toIndex = (size - fromIndex) > pageSize ? fromIndex + pageSize : size;
                    pageInfo.setList(tempCommissionSummary.subList(fromIndex, toIndex));
                } else {
                    int size = tempCommissionSummary.size();
                    // 分页
                    pageInfo.setTotal(size);
                    pageInfo.setListNum(size == 0 ? null : total);
                    pageInfo.setList(tempCommissionSummary);
                }
                return pageInfo;
            }
        }
        return new PageInfo();
    }

    @Override
    public Result exportPercentageStoreV5(StoreCommissionSummaryDO storeCommissionSummaryDO, HttpServletResponse response) {
        String fileName;
        if (1 == storeCommissionSummaryDO.getSummaryType()) {
            fileName = "门店提成汇总.csv";
        } else {
            fileName = "员工提成汇总.csv";
        }
        ByteArrayOutputStream bos = null;
        try {
            Boolean admin = storeCommissionSummaryDO.getAdmin();
            Boolean showStore = storeCommissionSummaryDO.getShowStore();
            Integer summaryType = storeCommissionSummaryDO.getSummaryType();
            // 是否员工提成汇总
            final boolean isSaleManSummary = 2 == summaryType;
            storeCommissionSummaryDO.setPageNum(null);
            storeCommissionSummaryDO.setPageSize(null);
            PageInfo pageInfo = this.selectPercentageStoreV5Optimize(storeCommissionSummaryDO);
            List<StoreCommissionSummary> list = pageInfo.getList();
            if (CollectionUtil.isNotEmpty(list)) {
                // 获取导出文件标题
                List<String> titles = filterCommissionSummaryShowField(storeCommissionSummaryDO);
                bos = new ByteArrayOutputStream();
                PrintWriter writer = CsvClient.renderTitle(titles, bos);
                StringJoiner stringJoiner = null;
                for (StoreCommissionSummary storeCommissionSummary : list) {
                    stringJoiner = new StringJoiner(StrUtil.COMMA);
                    List<Field> fields = Arrays.stream(storeCommissionSummary.getClass().getDeclaredFields())
                            .filter(item -> item.isAnnotationPresent(CommissionReportField.class))
                            .collect(Collectors.toList());

                    for (Field field : fields) {
                        CommissionReportField annotation = field.getAnnotation(CommissionReportField.class);
                        if (assertSatisfyCondition(storeCommissionSummaryDO, field, annotation)) {
                            continue;
                        }

                        String name = field.getName();
                        field.setAccessible(true);
                        // 如果不是管理员不展示成本额，毛利额，毛利率
                        if (!admin && CommonConstant.COMMISSION_NOT_SHOW_FIELD.contains(name)) {
                            continue;
                        }

                        Object obj;
                        try {
                            obj = Optional.ofNullable(field.get(storeCommissionSummary)).orElse(StrUtil.EMPTY);
                        } catch (Exception e) {
                            obj = StrUtil.EMPTY;
                        }
                        String val = String.valueOf(obj);
                        String suffix = annotation.suffix();
                        if (StringUtils.isNotBlank(val) && StringUtils.isNotBlank(suffix)) {
                            val = BigDecimalUtil.formatWithPercent(val) + suffix;
                        }
                        stringJoiner.add(val + " ");
                    }
                    stringJoiner.add("\r ");
                    writer.print(stringJoiner);
                }
                writer.flush();

                // 渲染合计数据
                StoreCommissionSummary storeCommissionSummaryTotal = (StoreCommissionSummary) pageInfo.getListNum();
                stringJoiner = new StringJoiner(StrUtil.COMMA);
                List<Field> fields = Arrays.stream(storeCommissionSummaryTotal.getClass().getDeclaredFields())
                        .filter(item -> item.isAnnotationPresent(CommissionReportField.class))
                        .collect(Collectors.toList());
                for (Field field : fields) {
                    CommissionReportField annotation = field.getAnnotation(CommissionReportField.class);
                    if (assertSatisfyCondition(storeCommissionSummaryDO, field, annotation)) {
                        continue;
                    }
                    String name = field.getName();
                    field.setAccessible(true);
                    // 如果不是管理员不展示成本额，毛利额，毛利率
                    if (!admin && CommonConstant.COMMISSION_NOT_SHOW_FIELD.contains(name)) {
                        continue;
                    }
                    Object obj;
                    try {
                        obj = Optional.ofNullable(field.get(storeCommissionSummaryTotal)).orElse(StrUtil.EMPTY);
                    } catch (Exception e) {
                        obj = StrUtil.EMPTY;
                    }
                    String val = String.valueOf(obj);
                    if (!annotation.isTotalItem()) {
                        val = "";
                    }
                    if ("planName".equals(name)) {
                        val = "合计";
                    }
                    String suffix = annotation.suffix();
                    if (StringUtils.isNotBlank(val) && StringUtils.isNotBlank(suffix)) {
                        val = BigDecimalUtil.formatWithPercent(val) + suffix;
                    }
                    stringJoiner.add(val + " ");
                }
                stringJoiner.add("\r ");
                writer.print(stringJoiner);
                writer.flush();
                return cosUtils.uploadFile(bos, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("exportPercentageStoreV5 {}", e.getMessage());
            throw new BusinessException("下载失败!");
        } finally {
            if (bos != null) {
                IoUtil.close(bos);
            }
        }
        return Result.errorMessage("暂无数据!");
    }

    /**
     * 过滤导出汇总报表展示字段
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @return List<String>
     */
    private List<String> filterCommissionSummaryShowField(StoreCommissionSummaryDO storeCommissionSummaryDO) {
        List<String> titles = new ArrayList<>();
        Class<StoreCommissionSummary> storeCommissionSummaryClass = StoreCommissionSummary.class;
        List<Field> collect = Arrays.stream(storeCommissionSummaryClass.getDeclaredFields()).filter(item -> item.isAnnotationPresent(CommissionReportField.class)).collect(Collectors.toList());
        for (Field field : collect) {
            CommissionReportField annotation = field.getAnnotation(CommissionReportField.class);
            if (assertSatisfyCondition(storeCommissionSummaryDO, field, annotation)) {
                continue;
            }
            if (!storeCommissionSummaryDO.getAdmin()) {
                // 如果不是管理员不展示成本额，毛利额，毛利率
                if (CommonConstant.COMMISSION_NOT_SHOW_FIELD.contains(field.getName())) {
                    continue;
                }
            }
            titles.add(annotation.fieldName());
        }
        return titles;
    }

    /**
     * 方案提成汇总过滤导出汇总报表展示字段
     *
     * @param inData inData
     * @return List<String>
     */
    private List<String> filterPlanCommissionSummaryShowField(PushMoneyByStoreV2InData inData) {
        List<String> titles = new ArrayList<>();
        Class<PushMoneyByStoreV5TotalOutData> pushMoneyByStoreV5TotalOutDataClass = PushMoneyByStoreV5TotalOutData.class;
        List<Field> collect = Arrays.stream(pushMoneyByStoreV5TotalOutDataClass.getDeclaredFields()).filter(item -> item.isAnnotationPresent(CommissionReportField.class)).collect(Collectors.toList());
        for (Field field : collect) {
            CommissionReportField annotation = field.getAnnotation(CommissionReportField.class);
            if (assertSatisfyCondition(inData, field, annotation)) {
                continue;
            }
            titles.add(annotation.fieldName());
        }
        return titles;
    }

    /**
     * 方案提成汇总判断是否满足展示条件
     *
     * @param pushMoneyByStoreV2InData pushMoneyByStoreV2InData
     * @param field                    field
     * @param annotation               annotation
     * @return boolean
     */
    private boolean assertSatisfyCondition(PushMoneyByStoreV2InData pushMoneyByStoreV2InData, Field field, CommissionReportField annotation) {
        if ("0".equals(pushMoneyByStoreV2InData.getIfShowZplan())) {
            // 如果展示子方案，把 showSubPlan 属性为 true 的过滤
            if (annotation.showSubPlan()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 门店、员工提成导出判断是否满足展示条件
     *
     * @param storeCommissionSummaryDO storeCommissionSummaryDO
     * @param field                    field
     * @param annotation               annotation
     * @return boolean
     */
    private boolean assertSatisfyCondition(StoreCommissionSummaryDO storeCommissionSummaryDO, Field field, CommissionReportField annotation) {
        if (!storeCommissionSummaryDO.getShowSubPlan()) {
            // 如果展示子方案，把 showSubPlan 属性为 true 的过滤
            if (annotation.showSubPlan()) {
                return true;
            }
        }
        if (1 == storeCommissionSummaryDO.getSummaryType()) {
            // 门店提成不展示营业员信息
            if (annotation.type() == 2) {
                return true;
            }
        }
        if (1 == storeCommissionSummaryDO.getDisplayGranularity()) {
            // 显示粒度为期间汇总 showSaleDate 属性为 true 的过滤
            if (annotation.showSaleDate()) {
                return true;
            }
        }
        if (2 == storeCommissionSummaryDO.getSummaryType()) {
            // 员工提成汇总时，不展示门店
            if (!storeCommissionSummaryDO.getShowStore()) {
                if (annotation.showStore()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PageInfo selectPercentageStoreSalerV3(PushMoneyBySalespersonV2InData inData) {
        List<PushMoneyByStoreSalepersonV2OutData> outDatas = new ArrayList<>();

        List<TichengZInfoBO> saleList = new ArrayList<>();
        List<TichengZInfoBO> proList = new ArrayList<>();
        Map tcMap = new HashMap();
        tcMap.put("client", inData.getClient());
        tcMap.put("startDate", inData.getStartDate());
        tcMap.put("endDate", inData.getEndDate());
        tcMap.put("planName", inData.getPlanName());
        tcMap.put("source", inData.getSource());
        tcMap.put("stoCode", inData.getStoCode());
        tcMap.put("planId", inData.getPlanId());
        List<String> sto = new ArrayList<>();
        sto.add(inData.getStoCode());
        inData.setStoArr(sto);
        if (ObjectUtil.isEmpty(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        } else if ("1".equals(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
        } else if ("2".equals(inData.getType())) {
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        }
        if (CollUtil.isNotEmpty(saleList)) {
            for (TichengZInfoBO tc : saleList) {
                PageInfo sale = selectMonthPushSalespersonBySaleV2(new PushMoneyBySalespersonV2InData(tc.getClient(), tc.getPlanId(), inData.getSalerId(), tc.getStartDate(), tc.getEndtDate(), inData.getStoArr()));
                if (ObjectUtil.isNotEmpty(sale.getList())) {
                    outDatas.addAll(sale.getList());
                }
            }
        }
        if (CollUtil.isNotEmpty(proList)) {
            for (TichengZInfoBO tc : proList) {
                List<PushMoneyByStoreSalepersonV2OutData> sale = selectMonthPushSalespersonByProV3(new PushMoneyBySalespersonV2InData(tc.getClient(), tc.getPlanId(), inData.getSalerId(), tc.getStartDate(), tc.getEndtDate(), inData.getStoArr()));
                if (ObjectUtil.isNotEmpty(sale)) {
                    outDatas.addAll(sale);
                }
            }
        }

        // 集合列的数据汇总
        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {

            // 集合列的数据汇总
            PushMoneyByStoreSalepersonV2OutTotal outTotal = new PushMoneyByStoreSalepersonV2OutTotal();
            for (PushMoneyByStoreSalepersonV2OutData monthData : outDatas) {
//                    outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(), outTotal.getAmt()));
////                    outTotal.setDays(BigDecimalUtil.add(monthData.getDays(), outTotal.getDays()));
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
//                if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
//                    outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(), outTotal.getAmt(), 4));
//                }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    private List<EmpSaleDetailResVo> decorateListToActualData(List<EmpSaleDetailResVo> kylinData, EmpSaleDetailInData inData) {
        List<EmpSaleDetailResVo> resList = new ArrayList<>();
        EmpSaleDetailInData inDataQuery = new EmpSaleDetailInData();
        BeanUtils.copyProperties(inData, inDataQuery);
        //此方法的目的是将ktlin中涉及到今日数据进行处理，将查询kylin存在延时的数据去除，替换成从数据库中取出的实时数据
        String today = DateUtil.format(new Date(), "yyyyMMdd");
        String kyToday = DateUtil.format(new Date(), "yyyy-MM-dd");
        inDataQuery.setToday(today);
        if (Integer.parseInt(inData.getStartDate().replaceAll("-", "")) <= Integer.parseInt(today) && Integer.parseInt(inData.getEndDate().replaceAll("-", "")) >= Integer.parseInt(today)) {
            //表示需要查询实时的数据
            List<EmpSaleDetailResVo> toDayAllEmpSaleDetailList = this.getToDayAllEmpSaleDetailList(inDataQuery);
            if (CollectionUtil.isNotEmpty(toDayAllEmpSaleDetailList)) {
                toDayAllEmpSaleDetailList.forEach(x -> x.setSaleDate(kyToday));
            }
            //过滤kylin中今天的数据
            if (CollectionUtil.isNotEmpty(kylinData)) {
                List<EmpSaleDetailResVo> filterDate = kylinData.stream().filter(x -> StrUtil.isNotBlank(x.getSaleDate()) && !x.getSaleDate().replaceAll("-", "").equals(today)).collect(Collectors.toList());
                filterDate.addAll(toDayAllEmpSaleDetailList);
                resList = filterDate;
            } else {
                if (CollectionUtil.isNotEmpty(toDayAllEmpSaleDetailList)) {
                    resList.addAll(toDayAllEmpSaleDetailList);
                }
            }
        } else {
            resList = kylinData;
        }
        return resList;
    }

    @Deprecated
    @Override
    public PageInfo selectEmpSaleDetailList(EmpSaleDetailInData inData, GetLoginOutData userInfo) {
        List<EmpSaleDetailResVo> resList = new ArrayList<>();

        List<TichengZInfoBO> saleList = new ArrayList<>();
        List<TichengZInfoBO> proList = new ArrayList<>();
        List<TichengZInfoBO> finalProList = new ArrayList<>();
        List<TichengZInfoBO> tichengz = new ArrayList<>();
        List<EmpSaleDetailResVo> outDatas = new ArrayList<>();
        //首先定位出所有时间段内的方案

        //构建查询条件
        Map tcMap = new HashMap();
        tcMap.put("client", userInfo.getClient());
        if (StrUtil.isNotBlank(inData.getStartDate())) {
            tcMap.put("startDate", inData.getStartDate().replaceAll("-", ""));
        }
        if (StrUtil.isNotBlank(inData.getStartDate())) {
            tcMap.put("endDate", inData.getEndDate().replaceAll("-", ""));
        }
        if (CollectionUtil.isNotEmpty(inData.getPlanDate())) {
            tcMap.put("startDate", inData.getPlanDate().get(0).replaceAll("-", ""));
            tcMap.put("endDate", inData.getPlanDate().get(1).replaceAll("-", ""));

        }

        tcMap.put("planName", inData.getPlanName());
        tcMap.put("cPlanName", inData.getCPlanName());
        tcMap.put("stoArr", inData.getStoArr());

        if (ObjectUtil.isEmpty(inData.getType())) {

            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);

        } else if ("1".equals(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
        } else if ("2".equals(inData.getType())) {
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        }
        if (StrUtil.isNotBlank(inData.getCPlanName()) && !"默认".equals(inData.getCPlanName())) {
            saleList = new ArrayList<>();
        }
        tichengz.addAll(saleList);
        tichengz.addAll(proList);

        tichengz.sort((o1, o2) -> {
            int cmp = o1.getStartDate().compareTo(o2.getStartDate());
            if (cmp == 0)
                cmp = o1.getEndtDate().compareTo(o2.getEndtDate());
            return cmp;
        });

        if (CollUtil.isNotEmpty(tichengz)) {

            //获取初始化的一些补充参数
            Map<String, Object> userIdNameMap = new HashMap<>();
            List<CommonVo> saleUserInfoList = tichengPlanZMapper.getSaleUserInfo(userInfo.getClient(), inData.getStartDate().replaceAll("-", ""), inData.getEndDate().replaceAll("-", ""));
            List<CommonVo> doctorUserInfoList = tichengPlanZMapper.getDoctorUserInfo(userInfo.getClient(), inData.getStartDate().replaceAll("-", ""), inData.getEndDate().replaceAll("-", ""));
            List<CommonVo> empUserInfoList = tichengPlanZMapper.getEmpUserInfo(userInfo.getClient(), inData.getStartDate().replaceAll("-", ""), inData.getEndDate().replaceAll("-", ""));
            if (CollectionUtil.isNotEmpty(saleUserInfoList)) {
                saleUserInfoList.forEach(x -> {
                    userIdNameMap.put(x.getLable(), x.getValue());
                });
            }
            if (CollectionUtil.isNotEmpty(doctorUserInfoList)) {
                doctorUserInfoList.forEach(x -> {
                    userIdNameMap.put(x.getLable(), x.getValue());
                });
            }
            if (CollectionUtil.isNotEmpty(empUserInfoList)) {
                empUserInfoList.forEach(x -> {
                    userIdNameMap.put(x.getLable(), x.getValue());
                });
            }

            Map<String, Boolean> costAmtShowConfigMap = null;
            if (!inData.getAdmin()) {
                // 获取是否显示成本的配置
                List<Map<String, String>> costAmtShowConfig = gaiaGlobalDataMapper.getCostAmtShowConfig(inData.getClient());
                costAmtShowConfigMap = costAmtShowConfig.stream()
                        .collect(Collectors.toMap(s -> s.get("stoCode"), s -> MapUtil.getBool(s, "type")));
            }

            for (TichengZInfoBO tc : tichengz) {
                EmpSaleDetailInData queryInData = new EmpSaleDetailInData();
                BeanUtils.copyProperties(inData, queryInData);
                TiChengCaculateService caculateService = null;
                if ("2".equals(tc.getType())) {
                    queryInData.setSettingId(tc.getSettingId());
                    caculateService = new TiChangProCalculateServiceImpl(tichengPlanZMapper, tichengProplanBasicMapper,
                            tichengProplanStoMapper, tichengProplanSettingMapper, tichengProplanProNMapper,
                            kylinJdbcTemplate);
                } else if ("1".equals(tc.getType())) {
                    caculateService = new TiChengSaleCaculateServiceImpl(tichengPlanZMapper, tichengSaleplanZMapper,
                            tichengSaleplanStoMapper, tichengSaleplanMMapper, tichengRejectClassMapper,
                            tichengRejectProMapper, kylinJdbcTemplate, gaiaProductBusinessMapper);
                } else {
                    throw new BusinessException("非法的方案类型");
                }
                //每个方案的开始时间和结束时间是不一样的，需要区别查询


                String startDate = null;
                String endDate = null;
//                startDate = DateUtil.format(DateUtil.parse(inData.getStartDate()), DatePattern.NORM_DATE_PATTERN);
//                endDate = DateUtil.format(DateUtil.parse(inData.getEndDate()), DatePattern.NORM_DATE_PATTERN);
                startDate = DateUtil.format(DateUtil.parse(tc.getStartDate()), DatePattern.NORM_DATE_PATTERN);
                endDate = DateUtil.format(DateUtil.parse(tc.getEndtDate()), DatePattern.NORM_DATE_PATTERN);
                if (Integer.parseInt(inData.getEndDate().replaceAll("-", "")) <= Integer.parseInt(tc.getEndtDate().replaceAll("-", ""))) {
                    endDate = DateUtil.format(DateUtil.parse(inData.getEndDate()), DatePattern.NORM_DATE_PATTERN);
                }
                if (Integer.parseInt(inData.getStartDate().replaceAll("-", "")) >= Integer.parseInt(tc.getStartDate().replaceAll("-", ""))) {
                    startDate = DateUtil.format(DateUtil.parse(inData.getStartDate()), DatePattern.NORM_DATE_PATTERN);
                }
                queryInData.setClient(userInfo.getClient());
                queryInData.setPlanId(tc.getPlanId());
                queryInData.setStartDate(startDate);
                queryInData.setEndDate(endDate);

                List<String> querySto = new ArrayList<>();
                if ("2".equals(tc.getType())) {
                    // 单品提成门店
                    TichengProplanStoN proplanStoQuery = new TichengProplanStoN();
                    proplanStoQuery.setClient(inData.getClient());
                    proplanStoQuery.setPid(Long.parseLong(tc.getPlanId() + ""));
                    //  查询当前商品提成门店
                    List<String> tichengSto = new ArrayList<>();
                    List<TichengProplanStoN> tichengProplanStos = tichengProplanStoMapper.select(proplanStoQuery);
                    if (CollectionUtil.isNotEmpty(tichengProplanStos)) {
                        for (TichengProplanStoN sto : tichengProplanStos) {
                            tichengSto.add(sto.getStoCode());
                        }
                        querySto.addAll(tichengSto);
                    }
                } else if ("1".equals(tc.getType())) {
                    // 销售提成门店
                    List<String> strings = tichengSaleplanStoMapper.selectSto(tc.getPlanId());
                    if (CollectionUtil.isNotEmpty(strings)) {
                        querySto.addAll(strings);
                    }
                }

                if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
                    //  设置的门店和筛选的门店求交集
                    querySto.retainAll(inData.getStoArr());
                }

                queryInData.setStoArr(querySto);
                List<EmpSaleDetailResVo> empSaleDetailResVoList = getAllEmpSaleDetailList(queryInData);//查询所有时间段内的数据
                empSaleDetailResVoList = decorateListToActualData(empSaleDetailResVoList, queryInData);//处理成实时数据
                Set<String> proCodeSet = new HashSet<>();
                Set<String> proClassCodeSet = new HashSet<>();
                if (CollectionUtil.isNotEmpty(empSaleDetailResVoList)) {
                    empSaleDetailResVoList.forEach(x -> {
                        proCodeSet.add(x.getProSelfCode());
                        proClassCodeSet.add(x.getProClassCode());
                    });
                }
                Map<String, GaiaProductBusiness> productBusinessInfoMap = new HashMap<>();//用于后期补充商品数据
                Map<String, GaiaProductClass> productClassMap = new HashMap<>();//用于后期补充商品分类信息
                if (CollectionUtil.isNotEmpty(proClassCodeSet)) {
                    List<String> proClassCodes = new ArrayList<>();
                    List<String> proClassList = new ArrayList<>();
                    if (CollectionUtil.isNotEmpty(proClassCodeSet)) {
                        proClassCodeSet.forEach(x -> {
                            proClassList.add(x);
                        });
                        Example queryProductClass = new Example(GaiaProductClass.class);
                        Example.Criteria criteriaProductClass = queryProductClass.createCriteria();
                        criteriaProductClass.andIn("proClassCode", proClassList);
                        List<GaiaProductClass> gaiaProductClasses = gaiaProductClassMapper.selectByExample(queryProductClass);
                        if (CollectionUtil.isNotEmpty(gaiaProductClasses)) {
                            gaiaProductClasses.forEach(x -> {
                                productClassMap.put(x.getProClassCode(), x);
                            });
                        }
                    }

                }

                PageInfo sale = caculateService.empSaleDetailList(queryInData, empSaleDetailResVoList, productBusinessInfoMap, userIdNameMap, productClassMap, costAmtShowConfigMap);
                resList.addAll(sale.getList());
                resList.sort((o1, o2) -> {
                    int cmp = o1.getSaleDate().compareTo(o2.getSaleDate());
                    if (cmp == 0)
                        cmp = o1.getSalerId().compareTo(o2.getSalerId());
                    return cmp;
                });
            }


        }

        inData.setPageNum(null);
        inData.setPageSize(null);
        PageInfo pageInfo = new PageInfo();
        if (inData.getPageNum() == null && inData.getPageSize() == null) {
            pageInfo.setList(resList);
            pageInfo.setTotal(resList.size());
            pageInfo.setPageNum(1);
            pageInfo.setPageSize(resList.size());
            pageInfo.setPages(1);

            pageInfo.setListNum(handleEmpSaleDetailTotal(resList));
            return pageInfo;
        }

        if (CollectionUtil.isNotEmpty(resList)) {
            List list = startPage(resList, inData.getPageNum(), inData.getPageSize());
            pageInfo.setList(list);
            pageInfo.setListNum(handleEmpSaleDetailTotal(list));
        }

        pageInfo.setTotal(resList.size());
        pageInfo.setPageNum(inData.getPageNum());
        pageInfo.setPageSize(inData.getPageSize());
        if (resList.size() / inData.getPageSize() == 0) {
            pageInfo.setPages(1);
        } else {
            pageInfo.setPages((resList.size() % inData.getPageSize() == 0) ? (resList.size() / inData.getPageSize()) : (resList.size() / inData.getPageSize() + 1));
        }
        return pageInfo;
    }


    @Override
    public PageInfo selectEmpSaleDetailListOptimize(EmpSaleDetailInData inData, GetLoginOutData userInfo) {
        List<EmpSaleDetailResVo> resList = new ArrayList<>();

        List<TichengZInfoBO> saleList = new ArrayList<>();
        List<TichengZInfoBO> proList = new ArrayList<>();
        List<TichengZInfoBO> finalProList = new ArrayList<>();
        List<TichengZInfoBO> tichengz = new ArrayList<>();
        List<EmpSaleDetailResVo> outDatas = new ArrayList<>();
        //首先定位出所有时间段内的方案

        //构建查询条件
        Map tcMap = new HashMap();
        tcMap.put("client", userInfo.getClient());
        if (StrUtil.isNotBlank(inData.getStartDate())) {
            tcMap.put("startDate", inData.getStartDate().replaceAll("-", ""));
        }
        if (StrUtil.isNotBlank(inData.getStartDate())) {
            tcMap.put("endDate", inData.getEndDate().replaceAll("-", ""));
        }
        if (CollectionUtil.isNotEmpty(inData.getPlanDate())) {
            tcMap.put("startDate", inData.getPlanDate().get(0).replaceAll("-", ""));
            tcMap.put("endDate", inData.getPlanDate().get(1).replaceAll("-", ""));

        }

        tcMap.put("planName", inData.getPlanName());
        tcMap.put("cPlanName", inData.getCPlanName());
        tcMap.put("stoArr", inData.getStoArr());

        if (ObjectUtil.isEmpty(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        } else if ("1".equals(inData.getType())) {
            saleList = tichengSaleplanZMapper.selectAllBySale(tcMap);
        } else if ("2".equals(inData.getType())) {
            proList = tichengProplanZMapper.selectAllStoreByPro(tcMap);
        }
        if (StrUtil.isNotBlank(inData.getCPlanName()) && !"默认".equals(inData.getCPlanName())) {
            saleList = new ArrayList<>();
        }
        tichengz.addAll(saleList);
        tichengz.addAll(proList);

        // 方案排序
        tichengz.sort((o1, o2) -> {
            int cmp = o1.getStartDate().compareTo(o2.getStartDate());
            if (cmp == 0)
                cmp = o1.getEndtDate().compareTo(o2.getEndtDate());
            return cmp;
        });

        if (CollUtil.isNotEmpty(tichengz)) {

            //获取初始化的一些补充参数
            Map<String, Object> userIdNameMap = new HashMap<>();
            List<CommonVo> saleUserInfoList = tichengPlanZMapper.getSaleUserInfo(userInfo.getClient(), inData.getStartDate().replaceAll("-", ""), inData.getEndDate().replaceAll("-", ""));
            List<CommonVo> doctorUserInfoList = tichengPlanZMapper.getDoctorUserInfo(userInfo.getClient(), inData.getStartDate().replaceAll("-", ""), inData.getEndDate().replaceAll("-", ""));
            List<CommonVo> empUserInfoList = tichengPlanZMapper.getEmpUserInfo(userInfo.getClient(), inData.getStartDate().replaceAll("-", ""), inData.getEndDate().replaceAll("-", ""));
            if (CollectionUtil.isNotEmpty(saleUserInfoList)) {
                saleUserInfoList.forEach(x -> {
                    userIdNameMap.put(x.getLable(), x.getValue());
                });
            }
            if (CollectionUtil.isNotEmpty(doctorUserInfoList)) {
                doctorUserInfoList.forEach(x -> {
                    userIdNameMap.put(x.getLable(), x.getValue());
                });
            }
            if (CollectionUtil.isNotEmpty(empUserInfoList)) {
                empUserInfoList.forEach(x -> {
                    userIdNameMap.put(x.getLable(), x.getValue());
                });
            }

            Map<String, Boolean> costAmtShowConfigMap = null;
            if (!inData.getAdmin()) {
                // 获取是否显示成本的配置
                List<Map<String, String>> costAmtShowConfig = gaiaGlobalDataMapper.getCostAmtShowConfig(inData.getClient());
                costAmtShowConfigMap = costAmtShowConfig.stream()
                        .collect(Collectors.toMap(s -> s.get("stoCode"), s -> MapUtil.getBool(s, "type")));
            }

            for (TichengZInfoBO tc : tichengz) {
                EmpSaleDetailInData queryInData = new EmpSaleDetailInData();
                BeanUtils.copyProperties(inData, queryInData);
                TiChengCaculateService caculateService = null;
                if ("2".equals(tc.getType())) {
                    queryInData.setSettingId(tc.getSettingId());
                    caculateService = new TiChangProCalculateServiceImpl(tichengPlanZMapper, tichengProplanBasicMapper,
                            tichengProplanStoMapper, tichengProplanSettingMapper, tichengProplanProNMapper,
                            kylinJdbcTemplate, userCommissionSummaryDetailMapper);
                } else if ("1".equals(tc.getType())) {
                    caculateService = new TiChengSaleCaculateServiceImpl(tichengPlanZMapper, tichengSaleplanZMapper,
                            tichengSaleplanStoMapper, tichengSaleplanMMapper, tichengRejectClassMapper,
                            tichengRejectProMapper, kylinJdbcTemplate, gaiaProductBusinessMapper, userCommissionSummaryDetailMapper);
                } else {
                    throw new BusinessException("非法的方案类型");
                }
                //每个方案的开始时间和结束时间是不一样的，需要区别查询

                String startDate = null;
                String endDate = null;
                startDate = DateUtil.format(DateUtil.parse(tc.getStartDate()), DatePattern.NORM_DATE_PATTERN);
                endDate = DateUtil.format(DateUtil.parse(tc.getEndtDate()), DatePattern.NORM_DATE_PATTERN);
                if (Integer.parseInt(inData.getEndDate().replaceAll("-", "")) <= Integer.parseInt(tc.getEndtDate().replaceAll("-", ""))) {
                    endDate = DateUtil.format(DateUtil.parse(inData.getEndDate()), DatePattern.NORM_DATE_PATTERN);
                }
                if (Integer.parseInt(inData.getStartDate().replaceAll("-", "")) >= Integer.parseInt(tc.getStartDate().replaceAll("-", ""))) {
                    startDate = DateUtil.format(DateUtil.parse(inData.getStartDate()), DatePattern.NORM_DATE_PATTERN);
                }
                queryInData.setClient(userInfo.getClient());
                queryInData.setPlanId(tc.getPlanId());
                queryInData.setStartDate(startDate);
                queryInData.setEndDate(endDate);

                List<String> querySto = new ArrayList<>();
                if ("2".equals(tc.getType())) {
                    // 单品提成门店
                    TichengProplanStoN proplanStoQuery = new TichengProplanStoN();
                    proplanStoQuery.setClient(inData.getClient());
                    proplanStoQuery.setPid(Long.parseLong(tc.getPlanId() + ""));
                    //  查询当前商品提成门店
                    List<String> tichengSto = new ArrayList<>();
                    List<TichengProplanStoN> tichengProplanStos = tichengProplanStoMapper.select(proplanStoQuery);
                    if (CollectionUtil.isNotEmpty(tichengProplanStos)) {
                        for (TichengProplanStoN sto : tichengProplanStos) {
                            tichengSto.add(sto.getStoCode());
                        }
                        querySto.addAll(tichengSto);
                    }
                } else if ("1".equals(tc.getType())) {
                    // 销售提成门店
                    List<String> strings = tichengSaleplanStoMapper.selectSto(tc.getPlanId());
                    if (CollectionUtil.isNotEmpty(strings)) {
                        querySto.addAll(strings);
                    }
                }

                if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
                    //  设置的门店和筛选的门店求交集
                    querySto.retainAll(inData.getStoArr());
                }

                queryInData.setStoArr(querySto);

                // 查询历史提成数据
                CommissionSummaryDetailDTO commissionSummaryDetailDTO = new CommissionSummaryDetailDTO();
                BeanUtils.copyProperties(queryInData, commissionSummaryDetailDTO);
                commissionSummaryDetailDTO.setStoCodes(querySto);
                commissionSummaryDetailDTO.setPlanId(String.valueOf(tc.getPlanId()));
                commissionSummaryDetailDTO.setStartDate(startDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY));
                commissionSummaryDetailDTO.setEndDate(endDate.replaceAll(StrUtil.DASHED, StrUtil.EMPTY));
                List<EmpSaleDetailResVo> userCommissionSummaryDetails = userCommissionSummaryDetailMapper.selectCommissionDetailByCondition(commissionSummaryDetailDTO);

                List<EmpSaleDetailResVo> empSaleDetailResVoList = new ArrayList<>();
                //getAllEmpSaleDetailList(queryInData);

                //处理成实时数据
                empSaleDetailResVoList = decorateListToActualData(empSaleDetailResVoList, queryInData);

                if (userCommissionSummaryDetails.isEmpty() && empSaleDetailResVoList.isEmpty()) {
                    continue;
                }

                Set<String> proCodeSet = new HashSet<>();
                Set<String> proClassCodeSet = new HashSet<>();
                if (CollectionUtil.isNotEmpty(empSaleDetailResVoList)) {
                    empSaleDetailResVoList.forEach(x -> {
                        proCodeSet.add(x.getProSelfCode());
                        proClassCodeSet.add(x.getProClassCode());
                    });
                }
                Map<String, GaiaProductBusiness> productBusinessInfoMap = new HashMap<>();//用于后期补充商品数据
                Map<String, GaiaProductClass> productClassMap = new HashMap<>();//用于后期补充商品分类信息
                if (CollectionUtil.isNotEmpty(proClassCodeSet)) {
                    List<String> proClassCodes = new ArrayList<>();
                    List<String> proClassList = new ArrayList<>();
                    if (CollectionUtil.isNotEmpty(proClassCodeSet)) {
                        proClassCodeSet.forEach(x -> {
                            proClassList.add(x);
                        });
                        Example queryProductClass = new Example(GaiaProductClass.class);
                        Example.Criteria criteriaProductClass = queryProductClass.createCriteria();
                        criteriaProductClass.andIn("proClassCode", proClassList);
                        List<GaiaProductClass> gaiaProductClasses = gaiaProductClassMapper.selectByExample(queryProductClass);
                        if (CollectionUtil.isNotEmpty(gaiaProductClasses)) {
                            gaiaProductClasses.forEach(x -> {
                                productClassMap.put(x.getProClassCode(), x);
                            });
                        }
                    }

                }

                PageInfo sale = caculateService.empSaleDetailListOptimize(queryInData,
                        userCommissionSummaryDetails,
                        empSaleDetailResVoList,
                        productBusinessInfoMap,
                        userIdNameMap,
                        productClassMap,
                        costAmtShowConfigMap);
                resList.addAll(sale.getList());
                resList.sort((o1, o2) -> {
                    int cmp = o1.getSaleDate().compareTo(o2.getSaleDate());
                    if (cmp == 0)
                        cmp = o1.getSalerId().compareTo(o2.getSalerId());
                    return cmp;
                });
            }


        }

        inData.setPageNum(null);
        inData.setPageSize(null);
        PageInfo pageInfo = new PageInfo();
        if (inData.getPageNum() == null && inData.getPageSize() == null) {
            pageInfo.setList(resList);
            pageInfo.setTotal(resList.size());
            pageInfo.setPageNum(1);
            pageInfo.setPageSize(resList.size());
            pageInfo.setPages(1);

            pageInfo.setListNum(handleEmpSaleDetailTotal(resList));
            return pageInfo;
        }

        if (CollectionUtil.isNotEmpty(resList)) {
            List list = startPage(resList, inData.getPageNum(), inData.getPageSize());
            pageInfo.setList(list);
            pageInfo.setListNum(handleEmpSaleDetailTotal(list));
        }

        pageInfo.setTotal(resList.size());
        pageInfo.setPageNum(inData.getPageNum());
        pageInfo.setPageSize(inData.getPageSize());
        if (resList.size() / inData.getPageSize() == 0) {
            pageInfo.setPages(1);
        } else {
            pageInfo.setPages((resList.size() % inData.getPageSize() == 0) ? (resList.size() / inData.getPageSize()) : (resList.size() / inData.getPageSize() + 1));
        }
        return pageInfo;
    }


    private EmpSaleDetailResVo handleEmpSaleDetailTotal(List<EmpSaleDetailResVo> dataList) {
        // 集合列的数据汇总
        EmpSaleDetailResVo outTotal = new EmpSaleDetailResVo();
        BigDecimal qyt = BigDecimal.ZERO;
        BigDecimal costAmt = BigDecimal.ZERO;
        BigDecimal ysAmt = BigDecimal.ZERO;
        BigDecimal amt = BigDecimal.ZERO;
        BigDecimal grossProfitAmt = BigDecimal.ZERO;
        BigDecimal grossProfitRate = BigDecimal.ZERO;
        BigDecimal zkAmt = BigDecimal.ZERO;
        BigDecimal zkl = BigDecimal.ZERO;
        BigDecimal tiTotal = BigDecimal.ZERO;
        BigDecimal deductionWageAmtRate = BigDecimal.ZERO;
        BigDecimal deductionWageGrossProfitRate = BigDecimal.ZERO;

        if (CollectionUtil.isNotEmpty(dataList)) {
            for (EmpSaleDetailResVo resVo : dataList) {
                qyt = qyt.add(StrUtil.isBlank(resVo.getQyt()) ? BigDecimal.ZERO : new BigDecimal(resVo.getQyt()));
                costAmt = costAmt.add(StrUtil.isBlank(resVo.getCostAmt()) ? BigDecimal.ZERO : new BigDecimal(resVo.getCostAmt()));
                ysAmt = ysAmt.add(StrUtil.isBlank(resVo.getYsAmt()) ? BigDecimal.ZERO : new BigDecimal(resVo.getYsAmt()));
                amt = amt.add(StrUtil.isBlank(resVo.getAmt()) ? BigDecimal.ZERO : new BigDecimal(resVo.getAmt()));
                grossProfitAmt = grossProfitAmt.add(StrUtil.isBlank(resVo.getGrossProfitAmt()) ? BigDecimal.ZERO : new BigDecimal(resVo.getGrossProfitAmt()));
                zkAmt = zkAmt.add(StrUtil.isBlank(resVo.getZkAmt()) ? BigDecimal.ZERO : new BigDecimal(resVo.getZkAmt()));
                tiTotal = tiTotal.add(StrUtil.isBlank(resVo.getTiTotal()) ? BigDecimal.ZERO : new BigDecimal(resVo.getTiTotal()));
            }
        }

        outTotal.setPlanName("合计");
        outTotal.setQyt(qyt.toPlainString());
        outTotal.setCostAmt(costAmt.toPlainString());
        outTotal.setAmt(amt.toPlainString());
        outTotal.setYsAmt(ysAmt.toPlainString());
        outTotal.setGrossProfitAmt(grossProfitAmt.toPlainString());
        outTotal.setGrossProfitRate(amt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.toPlainString() : grossProfitAmt.divide(amt, 4, RoundingMode.HALF_UP).toPlainString());
        outTotal.setZkAmt(zkAmt.toPlainString());
        outTotal.setZkl(ysAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.toPlainString() : zkAmt.divide(ysAmt, 4, RoundingMode.HALF_UP).toPlainString());
        outTotal.setTiTotal(tiTotal.toPlainString());
        outTotal.setDeductionWageAmtRate(ysAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.toPlainString() : tiTotal.divide(ysAmt, 4, RoundingMode.HALF_UP).toPlainString());
        outTotal.setDeductionWageGrossProfitRate(grossProfitAmt.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.toPlainString() : tiTotal.divide(grossProfitAmt, 4, RoundingMode.HALF_UP).toPlainString());
        return outTotal;
    }


    private List startPage(List list, Integer pageNum, Integer pageSize) {
        if (list == null) {
            return null;
        }
        if (list.size() == 0) {
            return null;
        }
        if (pageNum == 0) {
            List pageList = null;
            if (list.size() <= pageSize) {
                pageList = list.subList(0, list.size());
            } else {
                pageList = list.subList(0, pageSize);
            }
            return pageList;
        }

        Integer count = list.size(); // 记录总数
        Integer pageCount = 0; // 页数
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }

        int fromIndex = 0; // 开始索引
        int toIndex = 0; // 结束索引

        if (pageNum != pageCount) {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = count;
        }

        List pageList = list.subList(fromIndex, toIndex);
        return pageList;
    }

    private List<EmpSaleDetailResVo> getToDayAllEmpSaleDetailList(EmpSaleDetailInData inData) {
        List<EmpSaleDetailResVo> resList = new ArrayList<>();
        List<EmpSaleDetailResVo> dbList = tichengPlanZMapper.getToDayAllEmpSaleDetailList(inData);
        if (CollectionUtil.isNotEmpty(dbList)) {
            resList = dbList;
        }
        return resList;
    }

    private List<EmpSaleDetailResVo> getAllEmpSaleDetailList(EmpSaleDetailInData inData) {
        List<EmpSaleDetailResVo> resList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder().append("");


        queryBuilder.append("SELECT " +
                " sds.*, " +
                " batchs.GSBC_BATCH AS GSBC_BATCH, " +
                " batchs.BAT_EXPIRY_DATE AS batExpiryDate, " +
                " batchs.BAT_SUPPLIER_SALESMAN AS batSupplierSalesman, " +
                " batchs.BAT_SUPPLIER_CODE AS batSupplierCode, " +
                " batchs.SUP_NAME AS batSupplierName, " +
                " ssh.GSSH_EMP AS gsshEmpId FROM ");


        queryBuilder.append(" ( SELECT " +
                "  sd.CLIENT AS client, " +
                "  sd.GSSD_BR_ID AS stoCode, " +
                "  MAX( dd.STO_SHORT_NAME ) AS stoName, " +
                "  sd.GSSD_DATE AS saleDate, " +
                "  sd.GSSD_BILL_NO AS gssdBillNo, " +
                "  sd.GSSD_PRO_ID AS proSelfCode, " +
                "  sd.GSSD_SERIAL AS serial, " +
                "  sd.GSSD_BATCH_NO AS batBatchNo, " +
                "  MAX( sd.GSSD_SALER_ID ) AS salerId, " +
                "  MAX( pb.PRO_NAME ) AS proName, " +
                "  MAX( pb.PRO_COMMONNAME ) AS proCommonName, " +
                "  MAX( pb.PRO_SPECS ) AS proSpecs, " +
                "  MAX( pb.PRO_UNIT ) AS proUnit, " +
                "  MAX( pb.PRO_FACTORY_CODE ) AS PRO_FACTORY_CODE, " +
                "  MAX( pb.PRO_FACTORY_NAME ) AS factoryName, " +
                "  MAX( pb.PRO_LSJ ) AS proLsj, " +
                "  MAX( sd.GSSD_VALID_DATE ) AS gssdValidDate, " +
                "  MAX( sd.GSSD_DOCTOR_ID ) AS doctorId, " +
                "  round( SUM( sd.GSSD_AMT ) + SUM( sd.GSSD_ZK_AMT ), 2 ) AS ysAmt, " +
                "  round( SUM( sd.GSSD_AMT ), 2 ) AS amt, " +
                "  round(round( SUM( sd.GSSD_AMT ), 2 ) - round( SUM( sd.GSSD_MOV_PRICE ), 4 ), 2 ) AS grossProfitAmt, " +
                "  round( " +
                "  CASE " +
                "     " +
                "    WHEN SUM( sd.GSSD_AMT ) = 0 THEN " +
                "    0 ELSE ( " +
                "    SUM( sd.GSSD_AMT ) - SUM( sd.GSSD_MOV_PRICE )) / SUM( sd.GSSD_AMT )  " +
                "   END, " +
                "   4  " +
                "  ) AS grossProfitRate, " +
                "  round(SUM( sd.GSSD_ZK_AMT ), 2 ) AS zkAmt, " +
                "  SUM( sd.GSSD_QTY ) AS qyt, " +
                "  round( " +
                "  CASE " +
                "     " +
                "    WHEN SUM( sd.GSSD_AMT ) + SUM( sd.GSSD_ZK_AMT ) = 0 THEN " +
                "    0 ELSE SUM( sd.GSSD_ZK_AMT ) / ( " +
                "    SUM( sd.GSSD_AMT ) + SUM( sd.GSSD_ZK_AMT ))  " +
                "   END, " +
                "   4  " +
                "  ) AS zkl, " +
                "  round( SUM( sd.GSSD_MOV_PRICE ), 4 ) AS costAmt, " +
                "  MAX( pb.PRO_CLASS ) AS proClassCode, " +
                "  MAX( pb.PRO_SLAE_CLASS ) AS proSlaeClass, " +
                "  MAX( pb.PRO_POSITION ) AS proPosition  " +
                " FROM " +
                "  GAIA_SD_SALE_D sd " +
                "  INNER JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT  " +
                "  AND sd.GSSD_BR_ID = pb.PRO_SITE  " +
                "  AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE " +
                "  INNER JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT  " +
                "  AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                " WHERE 1=1 ");


        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");
        //门店编码
        if (CollectionUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID in ");
            queryBuilder.append(CommonUtil.queryByBatch(inData.getStoArr()));
//            String str = StringUtils.join(inData.getStoArr().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
        }
        //营业员，收银员，医生
        if (StrUtil.isNotBlank(inData.getNameSearchType()) && CollectionUtil.isNotEmpty(inData.getNameSearchIdList())) {
            //1表示营业员 2表示收银员 3表示医生
            if ("1".equals(inData.getNameSearchType())) {
                queryBuilder.append(" AND sd.GSSD_SALER_ID in  ");
                queryBuilder.append(CommonUtil.queryByBatch(inData.getNameSearchIdList()));
//                String str = StringUtils.join(inData.getNameSearchIdList().toArray(), ",");
//                queryBuilder.append(str);
//                queryBuilder.append(" )");
            } else if ("3".equals(inData.getNameSearchType())) {
                queryBuilder.append(" AND sd.GSSD_DOCTOR_ID in  ");
                queryBuilder.append(CommonUtil.queryByBatch(inData.getNameSearchIdList()));
//                String str = StringUtils.join(inData.getNameSearchIdList().toArray(), ",");
//                queryBuilder.append(str);
//                queryBuilder.append(" )");
            }
        }
        //商品编码
        if (StrUtil.isNotBlank(inData.getProCode())) {
            queryBuilder.append(" AND (pb.PRO_NAME like '%" + inData.getProCode() + "%'");
            queryBuilder.append("  or pb.PRO_COMMONNAME like '%" + inData.getProCode() + "%'");
            queryBuilder.append("  or pb.PRO_SELF_CODE like '%" + inData.getProCode() + "%')");
        }
        //生产厂家
        if (StrUtil.isNotBlank(inData.getFactoryName())) {
            queryBuilder.append(" AND pb.PRO_FACTORY_NAME like '%" + inData.getFactoryName() + "%'");
        }
        //商品分类
        if (CollectionUtil.isNotEmpty(inData.getProClass())) {
            queryBuilder.append(" AND pb.PRO_CLASS in  ");
            queryBuilder.append(CommonUtil.queryByBatch(inData.getProClass()));
//            String str = StringUtils.join(inData.getProClass().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
        }

        //商品定位
        if (CollectionUtil.isNotEmpty(inData.getProPosition())) {
            queryBuilder.append(" AND pb.PRO_POSITION in  ");
            queryBuilder.append(CommonUtil.queryByBatch(inData.getProPosition()));
//            String str = StringUtils.join(inData.getProPosition().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
        }

        //销售级别
        if (CollectionUtil.isNotEmpty(inData.getSaleClass())) {
            queryBuilder.append(" AND pb.PRO_SLAE_CLASS in  ");
            queryBuilder.append(CommonUtil.queryByBatch(inData.getSaleClass()));
//            String str = StringUtils.join(inData.getSaleClass().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
        }


        queryBuilder.append(" GROUP BY " +
                "  sd.CLIENT, " +
                "  sd.GSSD_BR_ID, " +
                "  dd.STO_NAME, " +
                "  sd.GSSD_DATE, " +
                "  sd.GSSD_BILL_NO, " +
                "  sd.GSSD_PRO_ID, " +
                "  sd.GSSD_BATCH_NO, " +
                "  sd.GSSD_SERIAL " +
                "   " +
                "  order by sd.CLIENT, " +
                "  sd.GSSD_BR_ID, " +
                "  dd.STO_NAME, " +
                "  sd.GSSD_DATE, " +
                "  sd.GSSD_BILL_NO, " +
                "  sd.GSSD_PRO_ID, " +
                "  sd.GSSD_BATCH_NO " +
                "  ) sds ");
        queryBuilder.append(" LEFT JOIN ");
        queryBuilder.append("(  SELECT " +
                "  sbc.CLIENT, " +
                "  sbc.GSBC_BR_ID, " +
                "  sbc.GSBC_VOUCHER_ID, " +
                "  sbc.GSBC_SERIAL, " +
                "  sbc.GSBC_PRO_ID, " +
                "  sbc.GSBC_BATCH_NO, " +
                "  MAX( sbc.GSBC_BATCH ) AS GSBC_BATCH, " +
                "  MAX( sbc.GSBC_DATE ), " +
                "  MAX( bi.BAT_EXPIRY_DATE ) AS BAT_EXPIRY_DATE, " +
                "  MAX( bi.BAT_SUPPLIER_SALESMAN ) AS BAT_SUPPLIER_SALESMAN, " +
                "  MAX( bi.BAT_SUPPLIER_CODE ) AS BAT_SUPPLIER_CODE, " +
                "  MAX( sb.SUP_NAME ) AS SUP_NAME  " +
                " FROM " +
                "  GAIA_SD_BATCH_CHANGE sbc " +
                "  LEFT JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT  " +
                "  AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE  " +
                "  AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO  " +
                "  AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE  " +
                "  AND sbc.GSBC_BATCH = bi.BAT_BATCH " +
                "  LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE  " +
                "  AND bi.CLIENT = sb.CLIENT  " +
                "  AND bi.BAT_SITE_CODE = sb.SUP_SITE  " +
                " WHERE 1=1 ");
        queryBuilder
                .append(" AND sbc.GSBC_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sbc.GSBC_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sbc.CLIENT= '" + inData.getClient() + "'");
        //门店编码
        if (CollectionUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sbc.GSBC_BR_ID in  ");
            queryBuilder.append(CommonUtil.queryByBatch(inData.getStoArr()));
//            String str = StringUtils.join(inData.getStoArr().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
        }
        queryBuilder.append(" GROUP BY " +
                "  sbc.CLIENT, " +
                "  sbc.GSBC_BR_ID, " +
                "  sbc.GSBC_VOUCHER_ID, " +
                "  sbc.GSBC_PRO_ID, " +
                "  sbc.GSBC_BATCH_NO, " +
                "  sbc.GSBC_SERIAL ");
        queryBuilder.append(" ) batchs ");
        queryBuilder.append("ON sds.client = batchs.CLIENT  " +
                " AND sds.stoCode = batchs.GSBC_BR_ID  " +
                " AND sds.gssdBillNo = batchs.GSBC_VOUCHER_ID  " +
                " AND sds.serial = batchs.GSBC_SERIAL  " +
                " AND sds.proSelfCode = batchs.GSBC_PRO_ID  " +
                " AND sds.batBatchNo = batchs.GSBC_BATCH_NO ");
        queryBuilder.append(" LEFT JOIN ");
        queryBuilder.append(" ( SELECT CLIENT, GSSH_BR_ID, GSSH_BILL_NO, GSSH_DATE, GSSH_EMP FROM GAIA_SD_SALE_H " +
                "WHERE 1=1 ");
        queryBuilder
                .append(" AND GSSH_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND GSSH_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND CLIENT= '" + inData.getClient() + "'");
        //门店编码
        if (CollectionUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND GSSH_BR_ID in  ");
            queryBuilder.append(CommonUtil.queryByBatch(inData.getStoArr()));
//            String str = StringUtils.join(inData.getStoArr().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" ) ");
        }
        queryBuilder.append(") ssh ON sds.client = ssh.CLIENT  " +
                " AND sds.stoCode = ssh.GSSH_BR_ID  " +
                " AND sds.saleDate = ssh.GSSH_DATE   " +
                " AND sds.gssdBillNo = ssh.GSSH_BILL_NO ");
        //改造后需要在此处添加一些查询的过滤条件
        queryBuilder.append(" WHERE 1=1 ");
        if (StrUtil.isNotBlank(inData.getGssName())) {
            queryBuilder.append(" AND batchs.BAT_SUPPLIER_SALESMAN like '%" + inData.getGssName() + "%");
        }

        //供应商
        if (StrUtil.isNotBlank(inData.getSupSelfCode())) {
            queryBuilder.append(" AND batchs.BAT_SUPPLIER_CODE in ( '" + inData.getSupSelfCode() + "' )");
        }
        if (StrUtil.isNotBlank(inData.getNameSearchType()) && CollectionUtil.isNotEmpty(inData.getNameSearchIdList())) {
            //1表示营业员 2表示收银员 3表示医生
            if ("2".equals(inData.getNameSearchType())) {
                queryBuilder.append(" AND ssh.GSSH_EMP in  ");
                queryBuilder.append(CommonUtil.queryByBatch(inData.getNameSearchIdList()));
//                String str = StringUtils.join(inData.getNameSearchIdList().toArray(), ",");
//                queryBuilder.append(arrStr);
//                queryBuilder.append(" )");
            }

        }
        resList = kylinJdbcTemplate.query(queryBuilder.toString(), RowMapper.getDefault(EmpSaleDetailResVo.class));
        return resList;

    }


//    private List<EmpSaleDetailResVo> getAllEmpSaleDetailList(EmpSaleDetailInData inData) {
//        List<EmpSaleDetailResVo> resList = new ArrayList<>();
//        StringBuilder queryBuilder = new StringBuilder().append(" SELECT  sd.CLIENT AS client, " +
//                "  sd.GSSD_BR_ID AS stoCode, " +
//                "  max( dd.STO_SHORT_NAME ) AS stoName, " +
//                "  sd.GSSD_DATE AS saleDate, " +
//                "  sd.GSSD_BILL_NO AS gssdBillNo, " +
//                "  sd.GSSD_PRO_ID AS proSelfCode, " +
//                "  sd.GSSD_SERIAL AS serial, " +
//                "  sd.GSSD_BATCH_NO AS batBatchNo, " +
//                "  max(sd.GSSD_SALER_ID) AS salerId, " +
//                "  max(pb.PRO_NAME) as proName, " +
//                "  max(pb.PRO_COMMONNAME) as proCommonName, " +
//                "  max(pb.PRO_SPECS) as proSpecs, " +
//                "  max(pb.PRO_UNIT) as proUnit, " +
//                "  max(pb.PRO_FACTORY_CODE) as PRO_FACTORY_CODE, " +
//                "  max(pb.PRO_FACTORY_NAME) as factoryName, " +
//                "  max(pb.PRO_LSJ) as proLsj, " +
//                "  max(sbc.GSBC_BATCH) as GSBC_BATCH, " +
//                "  max( bi.BAT_EXPIRY_DATE ) AS batExpiryDate, " +
//                "  max( sd.GSSD_VALID_DATE ) AS gssdValidDate, " +
//                "  round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 4 ) AS ysAmt, " +
//                "  round( sum( GSSD_AMT ), 4 ) AS amt, " +
//                "  round( sum( GSSD_AMT ), 4 ) - round( sum( GSSD_MOV_PRICE ), 4 ) AS grossProfitAmt, " +
//                "  round(( CASE WHEN sum( GSSD_AMT )= 0 THEN 0 ELSE ( sum( GSSD_AMT ) - sum( GSSD_MOV_PRICE ))/ sum( GSSD_AMT ) END ), 4 ) AS grossProfitRate, " +
//                "  sum( GSSD_ZK_AMT ) AS zkAmt, " +
//                "  sum( GSSD_QTY ) AS qyt, " +
//                "  round(( CASE WHEN sum( GSSD_AMT )= 0 THEN 0 ELSE sum( GSSD_ZK_AMT )/( sum( GSSD_AMT ) + sum( GSSD_ZK_AMT )) END ), 4 ) AS zkl, " +
//                "  round( sum( GSSD_MOV_PRICE ), 4 ) AS costAmt, " +
//                "  max(sh.GSSH_EMP  ) AS gsshEmpId, " +
//                "  max(sd.GSSD_DOCTOR_ID) as doctorId, " +
//                "  max(pb.PRO_CLASS) as proClassCode, " +
//                "  max(pb.PRO_SLAE_CLASS) as proSlaeClass, " +
//                "  max(pb.PRO_POSITION) as proPosition, " +
//                "  max( bi.BAT_SUPPLIER_SALESMAN ) AS batSupplierSalesman, " +
//                "  max( bi.BAT_SUPPLIER_CODE ) AS batSupplierCode, " +
//                "  max( sb.SUP_NAME ) AS batSupplierName");
//
//        queryBuilder.append(" FROM " +
//                "  GAIA_SD_SALE_D sd " +
//                "  LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT  " +
//                "  AND sh.GSSH_BR_ID = sd.GSSD_BR_ID  " +
//                "  AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO " +
//                "  LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT  " +
//                "  AND sd.GSSD_BR_ID = pb.PRO_SITE  " +
//                "  AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE " +
//                "  LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT  " +
//                "  AND sd.GSSD_BR_ID = dd.STO_CODE " +
//                "  LEFT JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT  " +
//                "  AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID  " +
//                "  AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID  " +
//                "  AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL  " +
//                "  AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID  " +
//                "  AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO " +
//                "  LEFT JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT  " +
//                "  AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE  " +
//                "  AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO  " +
//                "  AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE  " +
//                "  AND sbc.GSBC_BATCH = bi.BAT_BATCH " +
//                "  LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE  " +
//                "  AND bi.CLIENT = sb.CLIENT  " +
//                "  AND bi.BAT_SITE_CODE = sb.SUP_SITE  " +
//                " WHERE " +
//                "  1 = 1 ");
//
//        queryBuilder
//                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
//                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
//                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");
//        //门店编码
//        if (CollectionUtil.isNotEmpty(inData.getStoArr())) {
//            queryBuilder.append(" AND sd.GSSD_BR_ID in ( ");
//            String str = StringUtils.join(inData.getStoArr().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
//        }
//        //营业员，收银员，医生
//        if (StrUtil.isNotBlank(inData.getNameSearchType()) && CollectionUtil.isNotEmpty(inData.getNameSearchIdList())) {
//            //1表示营业员 2表示收银员 3表示医生
//            if ("1".equals(inData.getNameSearchType())) {
//                queryBuilder.append(" AND sd.GSSD_SALER_ID in ( ");
//            } else if ("2".equals(inData.getNameSearchType())) {
//                queryBuilder.append(" AND sh.GSSH_EMP in ( ");
//            } else {
//                queryBuilder.append(" AND sd.GSSD_DOCTOR_ID in ( ");
//            }
//            String str = StringUtils.join(inData.getNameSearchIdList().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
//        }
//        //商品编码
//        if (StrUtil.isNotBlank(inData.getProCode())) {
//            queryBuilder.append(" AND (pb.PRO_NAME like '%" + inData.getProCode() + "%'");
//            queryBuilder.append("  or pb.PRO_COMMONNAME like '%" + inData.getProCode() + "%'");
//            queryBuilder.append("  or pb.PRO_CODE like '%" + inData.getProCode() + "%')");
//        }
//        //生产厂家
//        if (StrUtil.isNotBlank(inData.getFactoryName())) {
//            queryBuilder.append(" AND pb.PRO_FACTORY_NAME like '%" + inData.getFactoryName() + "%'");
//        }
//        //商品分类
//        if (CollectionUtil.isNotEmpty(inData.getProClass())) {
//            queryBuilder.append(" AND pb.PRO_CLASS in ( ");
//            String str = StringUtils.join(inData.getProClass().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
//        }
//
//        //商品定位
//        if (CollectionUtil.isNotEmpty(inData.getProPosition())) {
//            queryBuilder.append(" AND pb.PRO_POSITION in ( ");
//            String str = StringUtils.join(inData.getProPosition().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
//        }
//
//        //销售级别
//        if (CollectionUtil.isNotEmpty(inData.getSaleClass())) {
//            queryBuilder.append(" AND pb.PRO_SLAE_CLASS in ( ");
//            String str = StringUtils.join(inData.getSaleClass().toArray(), ",");
//            queryBuilder.append(str);
//            queryBuilder.append(" )");
//        }
//
//        //业务员
//        if (StrUtil.isNotBlank(inData.getGssName())) {
//            queryBuilder.append(" AND bi.BAT_SUPPLIER_SALESMAN like '%" + inData.getGssName() + "%'");
//        }
//
//        //供应商
//        if (StrUtil.isNotBlank(inData.getSupSelfCode())) {
//            queryBuilder.append(" AND bi.BAT_SUPPLIER_CODE in ( '" + inData.getSupSelfCode() + "' )");
////            String str = StringUtils.join(inData.getSaleClass().toArray(), ",");
////            queryBuilder.append(str);
////            queryBuilder.append(" )");
//        }
//
//        queryBuilder.append(" GROUP BY " +
//                "  sd.CLIENT, " +
//                "  sd.GSSD_BR_ID, " +
//                "  dd.STO_NAME, " +
//                "  sd.GSSD_DATE, " +
//                "  sd.GSSD_BILL_NO, " +
//                "  sd.GSSD_PRO_ID, " +
//                "  sd.GSSD_BATCH_NO, " +
//                "  sd.GSSD_SERIAL " +
//                "   " +
//                "  order by sd.CLIENT, " +
//                "  sd.GSSD_BR_ID, " +
//                "  dd.STO_NAME, " +
//                "  sd.GSSD_DATE, " +
//                "  sd.GSSD_BILL_NO, " +
//                "  sd.GSSD_PRO_ID, " +
//                "  sd.GSSD_BATCH_NO " +
//                "  ");
//
//        resList = kylinJdbcTemplate.query(queryBuilder.toString(), RowMapper.getDefault(EmpSaleDetailResVo.class));
//        return resList;
//
//    }

    private List<EmpSaleDetailResVo> getAllEmpSaleDetailListOLD(EmpSaleDetailInData inData) {
        List<EmpSaleDetailResVo> resList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder().append("SELECT  t1.CLIENT as client,  " +
                " t1.GSSD_BR_ID as stoCode,  " +
                " t1.STO_NAME as stoName,  " +
                " t1.GSSD_DATE as saleDate,  " +
                " t1.GSSD_BILL_NO as gssdBillNo,  " +
                " t1.GSSD_PRO_ID as proSelfCode,  " +
                " t1.GSSD_BATCH_NO as batBatchNo ,t1.GSSD_SALER_ID as gssdBillNo,  " +
                " t2.GSSD_DOCTOR_ID as doctorId,  " +
                " t3.GSSH_EMP as gsshEmpId,  " +
                " t4.BAT_EXPIRY_DATE as gssdValidDate,  " +
                " t10.GSSD_VALID_DATE AS validDate,  " +
                " t5.PRO_SLAE_CLASS as proClassCode,  " +
                " t6.PRO_POSITION as proPosition,  " +
                " t7.BAT_SUPPLIER_SALESMAN as batSupplierSalesman,  " +
                " t8.BAT_SUPPLIER_CODE as batSupplierCode,  " +
                " t9.SUP_NAME as batSupplierName,  " +
                " t1.ysAmt as ysAmt,  " +
                " t1.amt as amt,  " +
                " t1.grossProfitAmt as grossProfitAmt,  " +
                " t1.grossProfitRate as grossProfitRate,  " +
                " t1.zkAmt as zkAmt,  " +
                " t1.zkl as zkl,  " +
                " t1.qyt AS qyt,  " +
                " t1.costAmt as costAmt FROM ");

        queryBuilder.append(" ( " +
                " SELECT " +
                "  sd.CLIENT AS CLIENT, " +
                "  sd.GSSD_BR_ID AS GSSD_BR_ID, " +
                "  dd.STO_NAME AS STO_NAME, " +
                "  sd.GSSD_DATE AS GSSD_DATE, " +
                "  sd.GSSD_BILL_NO AS GSSD_BILL_NO, " +
                "  sd.GSSD_PRO_ID AS GSSD_PRO_ID, " +
                "  sd.GSSD_BATCH_NO AS GSSD_BATCH_NO, " +
                "  sd.GSSD_SALER_ID AS GSSD_SALER_ID, " +
                "  round( sum( GSSD_AMT )+ sum( GSSD_ZK_AMT ), 2 ) AS ysAmt, " +
                "  round( sum( GSSD_AMT ), 2 ) AS amt, " +
                "  round( sum( GSSD_AMT ), 2 ) - round( sum( GSSD_MOV_PRICE ), 2 ) AS grossProfitAmt, " +
                "  round(( " +
                "   CASE " +
                "     WHEN sum( GSSD_AMT )= 0 THEN " +
                "     0 ELSE (sum( GSSD_AMT ) - sum( GSSD_MOV_PRICE ))/ sum( GSSD_AMT ) " +
                "    END  " +
                "    ),2) AS grossProfitRate, " +
                "  sum( GSSD_ZK_AMT ) AS zkAmt, " +
                "  sum( GSSD_QTY ) AS qyt, " +
                "  round(( " +
                "   CASE " +
                "     WHEN sum( GSSD_AMT )= 0 THEN " +
                "     0 ELSE sum( GSSD_ZK_AMT )/( " +
                "     sum( GSSD_AMT ) + sum( GSSD_ZK_AMT ))  " +
                "    END  " +
                "    ), " +
                "    2  " +
                "    ) AS zkl, " +
                "   round( sum( GSSD_MOV_PRICE ), 2 ) AS costAmt  " +
                "  FROM " +
                "   GAIA_SD_SALE_D sd " +
                "   LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT  " +
                "   AND sh.GSSH_BR_ID = sd.GSSD_BR_ID  " +
                "   AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO " +
                "   LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT  " +
                "   AND sd.GSSD_BR_ID = pb.PRO_SITE  " +
                "   AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE " +
                "   LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT  " +
                "   AND sd.GSSD_BR_ID = dd.STO_CODE " +
                "   left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT  " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID  " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID  " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL  " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID  " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT  " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE  " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO  " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE  " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH " +
                "   LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE  " +
                "   AND bi.CLIENT = sb.CLIENT  " +
                "   AND bi.BAT_SITE_CODE = sb.SUP_SITE  " +
                "  WHERE " +
                "   1 = 1");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");
        //门店编码
        if (CollectionUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID in ( ");
            String str = StringUtils.join(inData.getStoArr().toArray(), ",");
            queryBuilder.append(str);
            queryBuilder.append(" )");
        }
        //营业员，收银员，医生
        if (StrUtil.isNotBlank(inData.getNameSearchType()) && CollectionUtil.isNotEmpty(inData.getNameSearchIdList())) {
            //1表示营业员 2表示收银员 3表示医生
            if ("1".equals(inData.getNameSearchType())) {
                queryBuilder.append(" AND sd.GSSD_SALER_ID in ( ");
            } else if ("2".equals(inData.getNameSearchType())) {
                queryBuilder.append(" AND sh.GSSH_EMP in ( ");
            } else {
                queryBuilder.append(" AND sd.GSSD_DOCTOR_ID in ( ");
            }
            String str = StringUtils.join(inData.getNameSearchIdList().toArray(), ",");
            queryBuilder.append(str);
            queryBuilder.append(" )");
        }
        //商品编码
        if (StrUtil.isNotBlank(inData.getProCode())) {
            queryBuilder.append(" AND (pb.PRO_NAME like '%" + inData.getProCode() + "%'");
            queryBuilder.append("  or pb.PRO_COMMONNAME like '%" + inData.getProCode() + "%'");
            queryBuilder.append("  or pb.PRO_CODE like '%" + inData.getProCode() + "%')");
        }
        //生产厂家
        if (StrUtil.isNotBlank(inData.getFactoryName())) {
            queryBuilder.append(" AND pb.PRO_FACTORY_NAME like '%" + inData.getFactoryName() + "%'");
        }
        //商品分类
        if (CollectionUtil.isNotEmpty(inData.getProClass())) {
            queryBuilder.append(" AND pb.PRO_CLASS in ( ");
            String str = StringUtils.join(inData.getProClass().toArray(), ",");
            queryBuilder.append(str);
            queryBuilder.append(" )");
        }

        //商品定位
        if (CollectionUtil.isNotEmpty(inData.getProPosition())) {
            queryBuilder.append(" AND pb.PRO_POSITION in ( ");
            String str = StringUtils.join(inData.getProPosition().toArray(), ",");
            queryBuilder.append(str);
            queryBuilder.append(" )");
        }

        //销售级别
        if (CollectionUtil.isNotEmpty(inData.getSaleClass())) {
            queryBuilder.append(" AND pb.PRO_SLAE_CLASS in ( ");
            String str = StringUtils.join(inData.getSaleClass().toArray(), ",");
            queryBuilder.append(str);
            queryBuilder.append(" )");
        }

        //业务员
        if (StrUtil.isNotBlank(inData.getGssName())) {
            queryBuilder.append(" AND bi.BAT_SUPPLIER_SALESMAN like '%" + inData.getGssName() + "%'");
        }

        //供应商
        if (StrUtil.isNotBlank(inData.getSupSelfCode())) {
            queryBuilder.append(" AND sb.SUP_NAME like '%" + inData.getSupSelfCode() + "%'");
        }

//        //门店编码
//        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
//            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
//                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
//        }

        queryBuilder.append(" GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  sd.GSSD_SALER_ID   " +
                " ) t1 ");
        //========================================================
        queryBuilder.append(" LEFT JOIN (  " +
                "    SELECT  " +
                "      sd.CLIENT,  " +
                "      sd.GSSD_BR_ID,  " +
                "      dd.STO_NAME,  " +
                "      sd.GSSD_DATE,  " +
                "      sd.GSSD_BILL_NO,  " +
                "      sd.GSSD_PRO_ID,  " +
                "      sd.GSSD_BATCH_NO,  " +
                "      sd.GSSD_DOCTOR_ID   " +
                "    FROM  " +
                "      GAIA_SD_SALE_D sd  " +
                "      LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "      AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "      AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "      LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "      AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "      AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "      LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "      AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "      left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "      LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "      AND bi.CLIENT = sb.CLIENT   " +
                "      AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "    WHERE  " +
                "      1 = 1 ");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }
        queryBuilder.append("GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  sd.GSSD_DOCTOR_ID   " +
                " ) t2 ON t1.CLIENT = t2.CLIENT   " +
                " AND t1.GSSD_BR_ID = t2.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t2.STO_NAME   " +
                " AND t1.GSSD_DATE = t2.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t2.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t2.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t2.GSSD_BATCH_NO");
        //========================================================
        queryBuilder.append(" LEFT JOIN (  " +
                "    SELECT  " +
                "      sd.CLIENT AS CLIENT,  " +
                "      sd.GSSD_BR_ID AS GSSD_BR_ID,  " +
                "      dd.STO_NAME AS STO_NAME,  " +
                "      sd.GSSD_DATE AS GSSD_DATE,  " +
                "      sd.GSSD_BILL_NO AS GSSD_BILL_NO,  " +
                "      sd.GSSD_PRO_ID AS GSSD_PRO_ID,  " +
                "      sd.GSSD_BATCH_NO AS GSSD_BATCH_NO,  " +
                "      sh.GSSH_EMP AS GSSH_EMP  " +
                "      FROM  " +
                "        GAIA_SD_SALE_D sd  " +
                "        LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "        AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "        AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "        AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "        AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "        LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "        AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "        left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "        AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "        AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "        AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "        AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "        AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "        left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "        AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "        AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "        AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "        AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "        LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "        AND bi.CLIENT = sb.CLIENT   " +
                "        AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "      WHERE  " +
                "        1 = 1 ");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }
        queryBuilder.append(" GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  sh.GSSH_EMP   " +
                " ) t3 ON t1.CLIENT = t3.CLIENT   " +
                " AND t1.GSSD_BR_ID = t3.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t3.STO_NAME   " +
                " AND t1.GSSD_DATE = t3.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t3.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t3.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t3.GSSD_BATCH_NO");
        //========================================================

        queryBuilder.append(" LEFT JOIN (  " +
                "      SELECT  " +
                "        sd.CLIENT AS CLIENT,  " +
                "        sd.GSSD_BR_ID AS GSSD_BR_ID,  " +
                "        dd.STO_NAME AS STO_NAME,  " +
                "        sd.GSSD_DATE AS GSSD_DATE,  " +
                "        sd.GSSD_BILL_NO AS GSSD_BILL_NO,  " +
                "        sd.GSSD_PRO_ID AS GSSD_PRO_ID,  " +
                "        sd.GSSD_BATCH_NO AS GSSD_BATCH_NO,  " +
                "        bi.BAT_EXPIRY_DATE   " +
                "      FROM  " +
                "        GAIA_SD_SALE_D sd  " +
                "        LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "        AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "        AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "        AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "        AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "        LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "        AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "        left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "        LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "        AND bi.CLIENT = sb.CLIENT   " +
                "        AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "      WHERE  " +
                "        1 = 1 ");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }

        queryBuilder.append(" GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  bi.BAT_EXPIRY_DATE   " +
                " ) t4 ON t1.CLIENT = t4.CLIENT   " +
                " AND t1.GSSD_BR_ID = t4.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t4.STO_NAME   " +
                " AND t1.GSSD_DATE = t4.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t4.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t4.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t4.GSSD_BATCH_NO");
        //========================================================
        queryBuilder.append(" LEFT JOIN (  " +
                "      SELECT  " +
                "        sd.CLIENT,  " +
                "        sd.GSSD_BR_ID,  " +
                "        dd.STO_NAME,  " +
                "        sd.GSSD_DATE,  " +
                "        sd.GSSD_BILL_NO,  " +
                "        sd.GSSD_PRO_ID,  " +
                "        sd.GSSD_BATCH_NO,  " +
                "        pb.PRO_SLAE_CLASS   " +
                "      FROM  " +
                "        GAIA_SD_SALE_D sd  " +
                "        LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "        AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "        AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "        AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "        AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "        LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "        AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "              left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "        LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "        AND bi.CLIENT = sb.CLIENT   " +
                "        AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "      WHERE  " +
                "        1 = 1");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }

        queryBuilder.append(" GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  pb.PRO_SLAE_CLASS   " +
                " ) t5 ON t1.CLIENT = t5.CLIENT   " +
                " AND t1.GSSD_BR_ID = t5.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t5.STO_NAME   " +
                " AND t1.GSSD_DATE = t5.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t5.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t5.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t5.GSSD_BATCH_NO");
        //========================================================

        queryBuilder.append(" LEFT JOIN (  " +
                "      SELECT  " +
                "        sd.CLIENT,  " +
                "        sd.GSSD_BR_ID,  " +
                "        dd.STO_NAME,  " +
                "        sd.GSSD_DATE,  " +
                "        sd.GSSD_BILL_NO,  " +
                "        sd.GSSD_PRO_ID,  " +
                "        sd.GSSD_BATCH_NO,  " +
                "        pb.PRO_POSITION   " +
                "      FROM  " +
                "        GAIA_SD_SALE_D sd  " +
                "        LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "        AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "        AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "        AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "        AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "        LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "        AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "              left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "        LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "        AND bi.CLIENT = sb.CLIENT   " +
                "        AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "      WHERE  " +
                "        1 = 1");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }

        queryBuilder.append(" GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  pb.PRO_POSITION   " +
                " ) t6 ON t1.CLIENT = t6.CLIENT   " +
                " AND t1.GSSD_BR_ID = t6.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t6.STO_NAME   " +
                " AND t1.GSSD_DATE = t6.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t6.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t6.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t6.GSSD_BATCH_NO");

        //========================================================
        queryBuilder.append(" LEFT JOIN (  " +
                "      SELECT  " +
                "        sd.CLIENT,  " +
                "        sd.GSSD_BR_ID,  " +
                "        dd.STO_NAME,  " +
                "        sd.GSSD_DATE,  " +
                "        sd.GSSD_BILL_NO,  " +
                "        sd.GSSD_PRO_ID,  " +
                "        sd.GSSD_BATCH_NO,  " +
                "        bi.BAT_SUPPLIER_SALESMAN   " +
                "      FROM  " +
                "        GAIA_SD_SALE_D sd  " +
                "        LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "        AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "        AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "        AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "        AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "        LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "        AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "              left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "        LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "        AND bi.CLIENT = sb.CLIENT   " +
                "        AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "      WHERE  " +
                "        1 = 1");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }
        queryBuilder.append(" GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  pb.PRO_POSITION,  " +
                "  bi.BAT_SUPPLIER_SALESMAN   " +
                " ) t7 ON t1.CLIENT = t6.CLIENT   " +
                " AND t1.GSSD_BR_ID = t7.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t7.STO_NAME   " +
                " AND t1.GSSD_DATE = t7.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t7.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t7.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t7.GSSD_BATCH_NO");
        //========================================================

        queryBuilder.append(" LEFT JOIN (  " +
                "      SELECT  " +
                "        sd.CLIENT,  " +
                "        sd.GSSD_BR_ID,  " +
                "        dd.STO_NAME,  " +
                "        sd.GSSD_DATE,  " +
                "        sd.GSSD_BILL_NO,  " +
                "        sd.GSSD_PRO_ID,  " +
                "        sd.GSSD_BATCH_NO,  " +
                "        bi.BAT_SUPPLIER_CODE   " +
                "      FROM  " +
                "        GAIA_SD_SALE_D sd  " +
                "        LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "        AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "        AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "        AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "        AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "        LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "        AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "              left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "        LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "        AND bi.CLIENT = sb.CLIENT   " +
                "        AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "      WHERE  " +
                "        1 = 1");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }
        queryBuilder.append(" GROUP BY  " +
                " sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  pb.PRO_POSITION,  " +
                "  bi.BAT_SUPPLIER_CODE   " +
                " ) t8 ON t1.CLIENT = t8.CLIENT   " +
                " AND t1.GSSD_BR_ID = t8.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t8.STO_NAME   " +
                " AND t1.GSSD_DATE = t8.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t8.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t8.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t8.GSSD_BATCH_NO");

        queryBuilder.append(" LEFT JOIN (  " +
                "      SELECT  " +
                "        sd.CLIENT,  " +
                "        sd.GSSD_BR_ID,  " +
                "        dd.STO_NAME,  " +
                "        sd.GSSD_DATE,  " +
                "        sd.GSSD_BILL_NO,  " +
                "        sd.GSSD_PRO_ID,  " +
                "        sd.GSSD_BATCH_NO,  " +
                "        sb.SUP_NAME   " +
                "      FROM  " +
                "        GAIA_SD_SALE_D sd  " +
                "        LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "        AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "        AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "        AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "        AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "        LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "        AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "              left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "        LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "        AND bi.CLIENT = sb.CLIENT   " +
                "        AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "      WHERE  " +
                "        1 = 1");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }
        queryBuilder.append("GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  pb.PRO_POSITION,  " +
                "  sb.SUP_NAME  " +
                "  ) t9 ON t1.CLIENT = t9.CLIENT   " +
                " AND t1.GSSD_BR_ID = t9.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t9.STO_NAME   " +
                " AND t1.GSSD_DATE = t9.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t9.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t9.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t9.GSSD_BATCH_NO");

        queryBuilder.append(" LEFT JOIN (  " +
                "      SELECT  " +
                "        sd.CLIENT,  " +
                "        sd.GSSD_BR_ID,  " +
                "        dd.STO_NAME,  " +
                "        sd.GSSD_DATE,  " +
                "        sd.GSSD_BILL_NO,  " +
                "        sd.GSSD_PRO_ID,  " +
                "        sd.GSSD_BATCH_NO,  " +
                "        max(GSSD_VALID_DATE) AS GSSD_VALID_DATE   " +
                "      FROM  " +
                "        GAIA_SD_SALE_D sd  " +
                "        LEFT JOIN GAIA_SD_SALE_H sh ON sh.CLIENT = sd.CLIENT   " +
                "        AND sh.GSSH_BR_ID = sd.GSSD_BR_ID   " +
                "        AND sh.GSSH_BILL_NO = sd.GSSD_BILL_NO  " +
                "        LEFT JOIN GAIA_PRODUCT_BUSINESS pb ON sd.CLIENT = pb.CLIENT   " +
                "        AND sd.GSSD_BR_ID = pb.PRO_SITE   " +
                "        AND sd.GSSD_PRO_ID = pb.PRO_SELF_CODE  " +
                "        LEFT JOIN GAIA_STORE_DATA dd ON dd.CLIENT = sd.CLIENT   " +
                "        AND sd.GSSD_BR_ID = dd.STO_CODE  " +
                "              left JOIN GAIA_SD_BATCH_CHANGE sbc ON sd.CLIENT = sbc.CLIENT   " +
                "      AND sd.GSSD_BR_ID = sbc.GSBC_BR_ID   " +
                "      AND sd.GSSD_BILL_NO = sbc.GSBC_VOUCHER_ID   " +
                "      AND sd.GSSD_SERIAL = sbc.GSBC_SERIAL   " +
                "      AND sd.GSSD_PRO_ID = sbc.GSBC_PRO_ID   " +
                "      AND sd.GSSD_BATCH_NO = sbc.GSBC_BATCH_NO  " +
                "      left JOIN GAIA_BATCH_INFO bi ON sbc.CLIENT = bi.CLIENT   " +
                "      AND sbc.GSBC_BR_ID = bi.BAT_SITE_CODE   " +
                "      AND sbc.GSBC_BATCH_NO = bi.BAT_BATCH_NO   " +
                "      AND sbc.GSBC_PRO_ID = bi.BAT_PRO_CODE   " +
                "      AND sbc.GSBC_BATCH = bi.BAT_BATCH  " +
                "        LEFT JOIN GAIA_SUPPLIER_BUSINESS sb ON bi.BAT_SUPPLIER_CODE = sb.SUP_SELF_CODE   " +
                "        AND bi.CLIENT = sb.CLIENT   " +
                "        AND bi.BAT_SITE_CODE = sb.SUP_SITE   " +
                "      WHERE  " +
                "        1 = 1 ");
        queryBuilder
                .append(" AND sd.GSSD_DATE >= '" + inData.getStartDate() + "'")
                .append(" AND sd.GSSD_DATE <= '" + inData.getEndDate() + "'")
                .append(" AND sd.CLIENT= '" + inData.getClient() + "'");

        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            queryBuilder.append(" AND sd.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(inData.getStoArr()));
        }
        queryBuilder.append("GROUP BY  " +
                "  sd.CLIENT,  " +
                "  sd.GSSD_BR_ID,  " +
                "  dd.STO_NAME,  " +
                "  sd.GSSD_DATE,  " +
                "  sd.GSSD_BILL_NO,  " +
                "  sd.GSSD_PRO_ID,  " +
                "  sd.GSSD_BATCH_NO,  " +
                "  pb.PRO_POSITION " +
                "  ) t10 ON t1.CLIENT = t10.CLIENT   " +
                " AND t1.GSSD_BR_ID = t10.GSSD_BR_ID   " +
                " AND t1.STO_NAME = t10.STO_NAME   " +
                " AND t1.GSSD_DATE = t10.GSSD_DATE   " +
                " AND t1.GSSD_BILL_NO = t10.GSSD_BILL_NO   " +
                " AND t1.GSSD_PRO_ID = t10.GSSD_PRO_ID   " +
                " AND t1.GSSD_BATCH_NO = t10.GSSD_BATCH_NO");
        queryBuilder.append(" order by t1.CLIENT,t1.GSSD_BR_ID,t1.STO_NAME,t1.GSSD_DATE,t1.GSSD_BILL_NO");
        if (inData.getPageSize() != null && inData.getPageNum() != null) {
            Integer num = 0;
            if (inData.getPageNum() == 0) {
                queryBuilder.append(" limit " + inData.getPageSize() + " offset " + 0);
            } else {
                num = inData.getPageNum() - 1;
                queryBuilder.append(" limit " + inData.getPageSize() + " offset " + num * inData.getPageSize());
            }

        }
        resList = kylinJdbcTemplate.query(queryBuilder.toString(), RowMapper.getDefault(EmpSaleDetailResVo.class));
        return resList;
    }


    @Override
    public Map<String, Object> selectEmpSaleDetailListInit(GetLoginOutData userInfo) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> saleClassList = tichengPlanZMapper.getSaleClass(userInfo.getClient());
        List<String> proPostionList = tichengPlanZMapper.getProPosition(userInfo.getClient());
        List<CommonVo> saleUserInfoList = tichengPlanZMapper.getSaleUserInfo(userInfo.getClient(), null, null);
        List<CommonVo> doctorUserInfoList = tichengPlanZMapper.getDoctorUserInfo(userInfo.getClient(), null, null);
        List<CommonVo> empUserInfoList = tichengPlanZMapper.getEmpUserInfo(userInfo.getClient(), null, null);
        List<CommonVo> stoInfoList = tichengPlanZMapper.selectStoInfo(userInfo.getClient());
        if (CollectionUtil.isNotEmpty(saleClassList)) {
            List<CommonVo> resList = new ArrayList<>();
            saleClassList.forEach(x -> {
                if (StrUtil.isNotBlank(x)) {
                    CommonVo res = new CommonVo(x, x);
                    resList.add(res);
                }
            });
            resultMap.put("saleClass", resList);
        }
        if (CollectionUtil.isNotEmpty(proPostionList)) {
            List<CommonVo> resList = new ArrayList<>();
            proPostionList.forEach(x -> {
                if (StrUtil.isNotBlank(x)) {
                    CommonVo res = new CommonVo(x, x);
                    resList.add(res);
                }
            });
            resultMap.put("proPostion", resList);
        }
        //1表示营业员 2表示收银员 3表示医生
        resultMap.put("1", saleUserInfoList);
        resultMap.put("2", empUserInfoList);
        resultMap.put("3", doctorUserInfoList);
        resultMap.put("stoInfo", stoInfoList);
        return resultMap;
    }

    @Override
    public List<StoreSimpleInfoWithPlan> selectStoreListWithPlanCommission(String client, Integer planId) {
        List<StoreSimpleInfoWithPlan> storeSimpleInfoWithPlans = new ArrayList<>();
        // 获取销售提成关联的门店
        List<StoreSimpleInfoWithPlan> saleCommissionStores = tichengSaleplanStoMapper.selectStoreByPlanIdAndClient(client, planId);
        saleCommissionStores = saleCommissionStores.stream().filter(x -> StrUtil.isNotBlank(x.getStoCode())).collect(Collectors.toList());
        storeSimpleInfoWithPlans.addAll(saleCommissionStores);
        // 获取单品提成关联门店
        List<StoreSimpleInfoWithPlan> proCommissionStores = tichengProplanStoMapper.selectStoreByPlanIdAndClient(client, planId);
        proCommissionStores = proCommissionStores.stream().filter(x -> StrUtil.isNotBlank(x.getStoCode())).collect(Collectors.toList());
        storeSimpleInfoWithPlans.addAll(proCommissionStores);
        return storeSimpleInfoWithPlans;
    }

    @Override
    public Result exportEmpSaleDetailListV5(EmpSaleDetailInData inData, GetLoginOutData userInfo, HttpServletResponse response) {
        String fileName = "员工提成明细";
        Result result = null;
        try {
            Boolean admin = inData.getAdmin();
            inData.setPageNum(null);
            inData.setPageSize(null);
            PageInfo pageInfo = this.selectEmpSaleDetailListOptimize(inData, userInfo);
            List<EmpSaleDetailResVo> list = pageInfo.getList();
            if (CollectionUtil.isNotEmpty(list)) {
                BigDecimal tiTotal = BigDecimal.ZERO;
                list.forEach(x -> {
                    x.setTypeValue(x.getTypeValue());
                    x.setGrossProfitRate(StrUtil.isNotBlank(x.getGrossProfitRate()) ? new BigDecimal(x.getGrossProfitRate()).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%" : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                    x.setZkl(StrUtil.isNotBlank(x.getZkl()) ? new BigDecimal(x.getZkl()).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%" : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                    x.setCostAmt(BigDecimalUtil.toBigDecimal(x.getCostAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setYsAmt(new BigDecimal(x.getYsAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setAmt(new BigDecimal(x.getAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(x.getGrossProfitAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setZkAmt(new BigDecimal(x.getZkAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setQyt(new BigDecimal(x.getQyt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setTiTotal(new BigDecimal(x.getTiTotal()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setDeductionWageAmtRate(new BigDecimal(x.getDeductionWageAmtRate()).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                    x.setDeductionWageGrossProfitRate(new BigDecimal(x.getDeductionWageGrossProfitRate()).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");

                });
                for (EmpSaleDetailResVo x : list) {
                    tiTotal = tiTotal.add(new BigDecimal(x.getTiTotal()));
                }
                if (pageInfo.getListNum() != null) {
                    EmpSaleDetailResVo x = (EmpSaleDetailResVo) pageInfo.getListNum();
                    x.setGrossProfitRate(StrUtil.isNotBlank(x.getGrossProfitRate()) ? new BigDecimal(x.getGrossProfitRate()).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%" : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                    x.setZkl(StrUtil.isNotBlank(x.getZkl()) ? new BigDecimal(x.getZkl()).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%" : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                    x.setCostAmt(BigDecimalUtil.toBigDecimal(x.getCostAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setYsAmt(new BigDecimal(x.getYsAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setAmt(new BigDecimal(x.getAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setGrossProfitAmt(BigDecimalUtil.toBigDecimal(x.getGrossProfitAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setZkAmt(new BigDecimal(x.getZkAmt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setQyt(new BigDecimal(x.getQyt()).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setTiTotal(tiTotal.setScale(2, RoundingMode.HALF_UP).toPlainString());
                    x.setDeductionWageAmtRate(new BigDecimal(x.getDeductionWageAmtRate()).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                    x.setDeductionWageGrossProfitRate(new BigDecimal(x.getDeductionWageGrossProfitRate()).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
                }
                list.add((EmpSaleDetailResVo) pageInfo.getListNum());
                CsvFileInfo csvInfo = null;
                // 导出
                if (CollectionUtil.isNotEmpty(list)) {

                    csvInfo = CustomCommissionCsvClient.getCsvByte(list, "员工提成明细", Collections.singletonList((short) 1), !admin);
                } else {
                    throw new BusinessException("提示:员工提成明细为空！");
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                try {
                    bos.write(csvInfo.getFileContent());
                    result = cosUtils.uploadFile(bos, csvInfo.getFileName());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    bos.flush();
                    bos.close();
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("exportEmpSaleDetailListV5 {}", e.getMessage());
            throw new BusinessException("下载失败!");
        }
        return Result.errorMessage("暂无数据!");
    }

    @Override
    public Map<String, Object> selectAssistantByCondition(SelectAssistantDTO selectAssistantDTO) {
        Map<String, Object> resultMap = new HashMap<>();
        //1表示营业员 2表示收银员 3表示医生
        Integer staffType = selectAssistantDTO.getStaffType();
        if (null == staffType) {
            // 全部类型
            selectAssistantDTO.setStaff(StaffTypeEnum.getValue("1"));
            List<Map<String, Object>> maps = tichengPlanZMapper.selectAssistantByCondition(selectAssistantDTO);
            resultMap.put("1", maps);
            selectAssistantDTO.setStaff(StaffTypeEnum.getValue("2"));
            maps = tichengPlanZMapper.selectAssistantByCondition(selectAssistantDTO);
            resultMap.put("2", maps);
            selectAssistantDTO.setStaff(StaffTypeEnum.getValue("3"));
            maps = tichengPlanZMapper.selectAssistantByCondition(selectAssistantDTO);
            resultMap.put("3", maps);
        } else {
            selectAssistantDTO.setStaff(StaffTypeEnum.getValue(String.valueOf(staffType)));
            List<Map<String, Object>> maps = tichengPlanZMapper.selectAssistantByCondition(selectAssistantDTO);
            resultMap.put(String.valueOf(staffType), maps);
        }

        String client = selectAssistantDTO.getClient();
        List<String> saleClassList = tichengPlanZMapper.getSaleClass(client);
        List<String> proPostionList = tichengPlanZMapper.getProPosition(client);
        List<CommonVo> stoInfoList = tichengPlanZMapper.selectStoInfo(client);
        if (CollectionUtil.isNotEmpty(saleClassList)) {
            List<CommonVo> resList = new ArrayList<>();
            saleClassList.forEach(x -> {
                if (StrUtil.isNotBlank(x)) {
                    CommonVo res = new CommonVo(x, x);
                    resList.add(res);
                }
            });
            resultMap.put("saleClass", resList);
        }
        if (CollectionUtil.isNotEmpty(proPostionList)) {
            List<CommonVo> resList = new ArrayList<>();
            proPostionList.forEach(x -> {
                if (StrUtil.isNotBlank(x)) {
                    CommonVo res = new CommonVo(x, x);
                    resList.add(res);
                }
            });
            resultMap.put("proPostion", resList);
        }
        resultMap.put("stoInfo", stoInfoList);
        return resultMap;
    }

    public PageInfo selectMonthPushByProV3(PushMoneyByStoreV2InData inData) {
        List<PushMoneyByStoreV2OutData> outDatas = new ArrayList<>();

        //  1.查询当前商品提成计划
        Map queryMap = new HashMap();
        queryMap.put("client", inData.getClient());
        queryMap.put("id", inData.getPlanId());
        //  提成主表
        GaiaTichengProplanZN tichengZ = tichengProplanZNMapper.selectTichengProZ(queryMap);
        String startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        String endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);

        TichengProplanStoN tichengProplanStoNQuery = new TichengProplanStoN();
        tichengProplanStoNQuery.setClient(inData.getClient());
        tichengProplanStoNQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        //  查询当前商品提成门店
        List<String> tichengSto = new ArrayList<>();
        List<TichengProplanStoN> tichengProplanStoNList = tichengProplanStoMapper.select(tichengProplanStoNQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanStoNList)) {
            tichengProplanStoNList.forEach(x -> {
                tichengSto.add(x.getStoCode());
            });
        }
//        List<String> tichengSto = tichengProplanStoMapper.selectStoV3(queryMap);
//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }

        //  查询当前商品提成商品
        List<String> proCodes = new ArrayList<>();
        List<GaiaTichengProplanProV3> tichengPros = tichengProplanProMapper.selectProV3(queryMap);
        Map<String, GaiaTichengProplanProV3> map = tichengPros.stream().collect(Collectors.toMap(GaiaTichengProplanProV3::getProCode, o -> o));
        if (CollUtil.isNotEmpty(tichengPros)) {
            for (int i = 0; i < tichengPros.size(); i++) {
                proCodes.add(tichengPros.get(i).getProCode());
            }
        }
        //销售额、销售天数
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT clientId, ")
                .append(" D.GSSD_BR_ID as brId, ")
                .append(" S.STO_SHORT_NAME as brName, ")
                .append(" round(sum(GSSD_AMT),2) as amt, ") //-- 销售额
                .append(" count(distinct GSSD_KL_DATE_BR) as days ") //    销售天数
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON D.CLIENT = S.CLIENT AND D.GSSD_BR_ID = S.STO_CODE ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
        if (ObjectUtil.isNotEmpty(querySto)) {
            stoLvBuilder.append(" AND D.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(querySto));
        }
        stoLvBuilder.append(" GROUP BY D.CLIENT,D.GSSD_BR_ID,S.STO_SHORT_NAME ");
        stoLvBuilder.append(" ORDER BY D.GSSD_BR_ID ");
        log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
        List<Map<String, Object>> stoLvData = kylinJdbcTemplate.queryForList(stoLvBuilder.toString());

        StringBuilder saleDaysBuilder = new StringBuilder().append("SELECT D.CLIENT, ")
                .append(" count(distinct D.GSSD_DATE) as days ") //    销售天数
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
        saleDaysBuilder.append(" GROUP BY D.CLIENT ");
        log.info("sql统计数据：{加盟商销售天数}:" + saleDaysBuilder.toString());
//        List<Map<String, Object>> saleDaysData = kylinJdbcTemplate.queryForList(saleDaysBuilder.toString());
        Map<String, Object> saleDaysData = kylinJdbcTemplate.queryForMap(saleDaysBuilder.toString());

        //  2、取单笔单品提成销售
        StringBuilder saleBuilder = new StringBuilder().append("SELECT d.CLIENT clientId,d.GSSD_BR_ID as brId,")
                .append(" S.STO_SHORT_NAME as brName,")
                .append(" GSSD_BILL_NO billNo,GSSD_PRO_ID proCode,sum(GSSD_QTY) saleQty,sum(GSSD_AMT) saleAmt,min(GSSD_PRC1) proPrice,d.GSSD_SALER_ID salerId, ");

        // 计算毛利额(汇总应收金额 - 加点后金额（加点后未税成本）- 加点后税金（加点后成本税额）)
        saleBuilder.append("round(sum(GSSD_AMT)-sum(GSSD_MOV_PRICE),2) as saleGrossProfit");
        // 如果设置了剔除折扣率
        if (StringUtils.isNotEmpty(inData.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(inData.getPlanRejectDiscountRate())) {
            saleBuilder.append("  , ABS(case when min( GSSD_PRC1 ) = 0 or sum( GSSD_QTY )=0 then 0 else round(SUM( GSSD_AMT )/(min( GSSD_PRC1 )* sum( GSSD_QTY )), 4 ) end ) as ZKL ");
        }

        saleBuilder.append(" from GAIA_SD_SALE_D d ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON d.CLIENT = S.CLIENT AND d.GSSD_BR_ID = S.STO_CODE ")
                .append(" WHERE d.GSSD_DATE >= '" + startDate + "'")
                .append(" AND d.GSSD_DATE <= '" + endDate + "'")
                .append(" AND d.CLIENT= '" + inData.getClient() + "'");
        if (ObjectUtil.isNotEmpty(querySto)) {
            saleBuilder.append(" AND d.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(querySto));
        }
        if (ObjectUtil.isNotEmpty(proCodes)) {
            saleBuilder.append(" AND d.GSSD_PRO_ID IN ")
                    .append(CommonUtil.queryByBatch(proCodes));
        }
        saleBuilder.append(" GROUP BY d.CLIENT,d.GSSD_BR_ID,S.STO_SHORT_NAME,d.GSSD_BILL_NO,d.GSSD_PRO_ID,d.GSSD_SALER_ID ");
        // 如果设置了剔除折扣率
        if (StringUtils.isNotEmpty(inData.getPlanRejectDiscountRateSymbol()) && StringUtils.isNotEmpty(inData.getPlanRejectDiscountRate())) {
            switch (inData.getPlanRejectDiscountRateSymbol()) {
                case "=":
                    inData.setPlanRejectDiscountRateSymbol("!=");
                    break;
                case ">":
                    inData.setPlanRejectDiscountRateSymbol("<=");
                    break;
                case ">=":
                    inData.setPlanRejectDiscountRateSymbol("<");
                    break;
                case "<":
                    inData.setPlanRejectDiscountRateSymbol(">=");
                    break;
                case "<=":
                    inData.setPlanRejectDiscountRateSymbol(">");
                    break;
            }
            saleBuilder.append("having ZKL").append(inData.getPlanRejectDiscountRateSymbol()).append(inData.getPlanRejectDiscountRate());
        }
        saleBuilder.append(" ORDER BY d.GSSD_BR_ID ");
        log.info("sql统计数据：{查询小票中参与提成商品销售信息}:" + saleBuilder.toString());
        List<ProductSaleAmt> saleData = kylinJdbcTemplate.query(saleBuilder.toString(), RowMapper.getDefault(ProductSaleAmt.class));
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(stoLvData));

        for (int i = 0; i < saleData.size(); i++) {
            ProductSaleAmt a = saleData.get(i);
            GaiaTichengProplanProV3 proV3 = map.get(a.getProCode());
            if (ObjectUtil.isNotEmpty(proV3)) {
                if ("1".equals(proV3.getTichengLevel())) {
                    if (ObjectUtil.isNotEmpty(proV3.getTichengAmt())) {
                        if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty()) > -1) {
                            a.setTcAmt(proV3.getTichengAmt().toString());
                        } else {
                            a.setTcAmt("0");
                        }
                    } else {
                        if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty()) > -1) {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("1".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("3".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));   // 毛利
                            }
                            a.setTcAmt(d.toString());
                        } else {
                            a.setTcAmt("0");
                        }
                    }
                } else if ("2".equals(proV3.getTichengLevel())) {
                    //先判断数量是否大于二级达成数量
                    if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty2()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt2())) {
                            a.setTcAmt(proV3.getTichengAmt2().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("1".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("3".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));   // 毛利
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt())) {
                            a.setTcAmt(proV3.getTichengAmt().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("1".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("3".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }

                            a.setTcAmt(d.toString());
                        }
                    } else {
                        a.setTcAmt("0");
                    }
                } else if ("3".equals(proV3.getTichengLevel())) {
                    //先判断数量是否大于三级达成数量
                    if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty3()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt3())) {
                            a.setTcAmt(proV3.getTichengAmt3().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate3().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("1".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate3().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("3".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate3().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty2()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt2())) {
                            a.setTcAmt(proV3.getTichengAmt2().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("1".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("3".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt())) {
                            a.setTcAmt(proV3.getTichengAmt().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("1".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if ("3".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else {
                        a.setTcAmt("0");
                    }
                }
            }
        }
        //门店提成汇总
        for (Map sale : stoLvData) {
            PushMoneyByStoreV2OutData outData = new PushMoneyByStoreV2OutData();
            outData.setPlanName(tichengZ.getPlanName());
            outData.setStartDate(tichengZ.getPlanStartDate());
            outData.setEndtDate(tichengZ.getPlanEndDate());
            outData.setPlanId(inData.getPlanId());
            outData.setType("单品提成");
            outData.setTypeValue("2");
            if ("1".equals(tichengZ.getPlanStatus())) {
                outData.setStatus("1");
            } else if ("2".equals(tichengZ.getDeleteFlag())) {
                outData.setStatus("2");
            }
            outData.setStoCode(String.valueOf(sale.get("brId")));
            outData.setStoName(String.valueOf(sale.get("brName")));
            outData.setDays(BigDecimalUtil.toBigDecimal(sale.get("days")));
            outData.setAmt(BigDecimalUtil.toBigDecimal(sale.get("amt")));
            BigDecimal c = BigDecimal.ZERO;
            //筛选出对应门店和营业员的数据
            for (int i = 0; i < saleData.size(); i++) {
                ProductSaleAmt a = saleData.get(i);
                if (sale.get("clientId").equals(a.getClientId()) && sale.get("brId").equals(a.getBrId())) {
                    if (StringUtils.isEmpty(a.getTcAmt())) {
                        a.setTcAmt("0");
                    }
                    c = c.add(new BigDecimal(a.getTcAmt()));
                }
            }
            outData.setDeductionWage(c);
            outData.setDeductionWageRate(BigDecimalUtil.divide(c, sale.get("amt"), 4));
            outDatas.add(outData);
        }
        PageInfo pageInfo;
        if (CollUtil.isNotEmpty(outDatas)) {
            // 集合列的数据汇总
            PushMoneyByStoreV2OutTotal outTotal = new PushMoneyByStoreV2OutTotal();
            for (PushMoneyByStoreV2OutData monthData : outDatas) {
                outTotal.setAmt(BigDecimalUtil.add(monthData.getAmt(), outTotal.getAmt()));
                if (ObjectUtil.isNotEmpty(saleDaysData) && StringUtils.isNotEmpty(String.valueOf(saleDaysData.get("days")))) {
                    outTotal.setDays(BigDecimalUtil.toBigDecimal(saleDaysData.get("days")));
                }
                outTotal.setDeductionWage(BigDecimalUtil.add(monthData.getDeductionWage(), outTotal.getDeductionWage()));
            }
            if (outTotal.getAmt().compareTo(BigDecimal.ZERO) != 0) {
                outTotal.setDeductionWageRate(BigDecimalUtil.divide(outTotal.getDeductionWage(), outTotal.getAmt(), 4));
            }
            pageInfo = new PageInfo(outDatas, outTotal);
        } else {
            pageInfo = new PageInfo();
        }
        return pageInfo;
    }

    public List<PushMoneyByStoreSalepersonV2OutData> selectMonthPushSalespersonByProV3(PushMoneyBySalespersonV2InData inData) {
        List<PushMoneyByStoreSalepersonV2OutData> outDatas = new ArrayList<>();

        //  1.查询当前商品提成计划
        Map queryMap = new HashMap();
        queryMap.put("client", inData.getClient());
        queryMap.put("id", inData.getId());
        //  提成主表
        GaiaTichengProplanZN tichengZ = tichengProplanZNMapper.selectTichengProZ(queryMap);
        String startDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanStartDate()), DatePattern.NORM_DATE_PATTERN);
        String endDate = DateUtil.format(DateUtil.parse(tichengZ.getPlanEndDate()), DatePattern.NORM_DATE_PATTERN);

        //  查询当前商品提成门店
        TichengProplanStoN tichengProplanStoNQuery = new TichengProplanStoN();
        tichengProplanStoNQuery.setClient(inData.getClient());
        tichengProplanStoNQuery.setPid(Long.parseLong(inData.getPlanId() + ""));
        //  查询当前商品提成门店
        List<String> tichengSto = new ArrayList<>();
        List<TichengProplanStoN> tichengProplanStoNList = tichengProplanStoMapper.select(tichengProplanStoNQuery);
        if (CollectionUtil.isNotEmpty(tichengProplanStoNList)) {
            tichengProplanStoNList.forEach(x -> {
                tichengSto.add(x.getStoCode());
            });
        }
//        List<String> tichengSto = tichengProplanStoMapper.selectStoV3(queryMap);
//        List<String> tichengSto = Arrays.asList("10001");
        List<String> querySto = new ArrayList<>();
        querySto.addAll(tichengSto);
        if (ObjectUtil.isNotEmpty(inData.getStoArr())) {
            //  设置的门店和筛选的门店求交集
            querySto.retainAll(inData.getStoArr());
        }

        //  查询当前商品提成商品
        List<String> proCodes = new ArrayList<>();
        List<GaiaTichengProplanProV3> tichengPros = tichengProplanProMapper.selectProV3(queryMap);
        Map<String, GaiaTichengProplanProV3> map = tichengPros.stream().collect(Collectors.toMap(GaiaTichengProplanProV3::getProCode, o -> o));
        if (CollUtil.isNotEmpty(tichengPros)) {
            for (int i = 0; i < tichengPros.size(); i++) {
                proCodes.add(tichengPros.get(i).getProCode());
            }
        }
        //销售额、销售天数
        StringBuilder stoLvBuilder = new StringBuilder().append("SELECT D.CLIENT clientId, ")
                .append(" D.GSSD_BR_ID as brId, ")
                .append(" S.STO_SHORT_NAME as brName, ")
                .append(" D.GSSD_SALER_ID as salerId, ")
                .append(" U.USER_NAM as salerName, ")
                .append(" round(sum(GSSD_AMT),2) as amt, ") //-- 销售额
                .append(" count(distinct GSSD_KL_DATE_BR) as days ") //    销售天数
                .append(" FROM GAIA_SD_SALE_D as D ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON D.CLIENT = S.CLIENT AND D.GSSD_BR_ID = S.STO_CODE ")
                .append(" LEFT JOIN GAIA_USER_DATA as U ON D.CLIENT = U.CLIENT AND D.GSSD_SALER_ID = U.USER_ID ")
                .append(" WHERE 1=1 ")
                .append(" AND D.GSSD_DATE >= '" + startDate + "'")
                .append(" AND D.GSSD_DATE <= '" + endDate + "'")
                .append(" AND D.CLIENT= '" + inData.getClient() + "'");
        if (ObjectUtil.isNotEmpty(inData.getSalerId())) {
            stoLvBuilder.append(" AND D.GSSD_SALER_ID = '" + inData.getSalerId() + "'");
        }
        if (ObjectUtil.isNotEmpty(querySto)) {
            stoLvBuilder.append(" AND D.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(querySto));
        }
        stoLvBuilder.append(" GROUP BY D.CLIENT,D.GSSD_BR_ID,S.STO_SHORT_NAME,D.GSSD_SALER_ID,U.USER_NAM");
        log.info("sql统计数据：{单店日均，确定销售级别}:" + stoLvBuilder.toString());
        List<Map<String, Object>> stoLvData = kylinJdbcTemplate.queryForList(stoLvBuilder.toString());

        //  2、取单笔单品提成销售
        StringBuilder saleBuilder = new StringBuilder().append("SELECT d.CLIENT clientId,d.GSSD_BR_ID as brId,")
                .append(" S.STO_SHORT_NAME as brName,")
                .append(" GSSD_BILL_NO billNo,GSSD_PRO_ID proCode,sum(GSSD_QTY) saleQty,sum(GSSD_AMT) saleAmt,min(GSSD_PRC1) proPrice,d.GSSD_SALER_ID salerId ")
                .append(" from GAIA_SD_SALE_D d ")
                .append(" INNER JOIN GAIA_STORE_DATA as S ON d.CLIENT = S.CLIENT AND d.GSSD_BR_ID = S.STO_CODE ")
                .append(" WHERE d.GSSD_DATE >= '" + startDate + "'")
                .append(" AND d.GSSD_DATE <= '" + endDate + "'")
                .append(" AND d.CLIENT= '" + inData.getClient() + "'");
        if (ObjectUtil.isNotEmpty(querySto)) {
            saleBuilder.append(" AND d.GSSD_BR_ID IN ")
                    .append(CommonUtil.queryByBatch(querySto));
        }
        if (ObjectUtil.isNotEmpty(proCodes)) {
            saleBuilder.append(" AND d.GSSD_PRO_ID IN ")
                    .append(CommonUtil.queryByBatch(proCodes));
        }
        saleBuilder.append(" GROUP BY d.CLIENT,d.GSSD_BR_ID,S.STO_SHORT_NAME,d.GSSD_BILL_NO,d.GSSD_PRO_ID,d.GSSD_SALER_ID ");
        saleBuilder.append(" ORDER BY d.GSSD_BR_ID ");
        log.info("sql统计数据：{查询小票中参与提成商品销售信息}:" + stoLvBuilder.toString());
        List<ProductSaleAmt> saleData = kylinJdbcTemplate.query(saleBuilder.toString(), RowMapper.getDefault(ProductSaleAmt.class));
        log.info("统计数据返回结果:{}", JSONObject.toJSONString(stoLvData));

        for (int i = 0; i < saleData.size(); i++) {
            ProductSaleAmt a = saleData.get(i);
            GaiaTichengProplanProV3 proV3 = map.get(a.getProCode());
            if (ObjectUtil.isNotEmpty(proV3)) {
                if ("1".equals(proV3.getTichengLevel())) {
                    if (ObjectUtil.isNotEmpty(proV3.getTichengAmt())) {
                        if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty()) > -1) {
                            a.setTcAmt(proV3.getTichengAmt().toString());
                        } else {
                            a.setTcAmt("0");
                        }
                    } else {
                        if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty()) > -1) {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("1".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("3".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        } else {
                            a.setTcAmt("0");
                        }
                    }
                } else if ("2".equals(proV3.getTichengLevel())) {
                    //先判断数量是否大于二级达成数量
                    if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty2()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt2())) {
                            a.setTcAmt(proV3.getTichengAmt2().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("1".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("3".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt())) {
                            a.setTcAmt(proV3.getTichengAmt().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("1".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("3".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else {
                        a.setTcAmt("0");
                    }
                } else if ("3".equals(proV3.getTichengLevel())) {
                    //先判断数量是否大于三级达成数量
                    if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty3()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt3())) {
                            a.setTcAmt(proV3.getTichengAmt3().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate3().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("1".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate3().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("3".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate3().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty2()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt2())) {
                            a.setTcAmt(proV3.getTichengAmt2().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("1".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("3".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate2().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else if (new BigDecimal(a.getSaleQty()).compareTo(proV3.getSaleQty()) > -1) {
                        if (ObjectUtil.isNotEmpty(proV3.getTichengAmt())) {
                            a.setTcAmt(proV3.getTichengAmt().toString());
                        } else {
                            BigDecimal d = BigDecimal.ZERO;
                            if ("2".equals(proV3.getPlanPercentageType())) {
                                d = new BigDecimal(a.getSaleAmt()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("1".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getProPrice()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            } else if (("3".equals(proV3.getPlanPercentageType()))) {
                                d = new BigDecimal(a.getSaleGrossProfit()).multiply(proV3.getTichengRate().divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
                            }
                            a.setTcAmt(d.toString());
                        }
                    } else {
                        a.setTcAmt("0");
                    }
                }
            }
        }
        //门店提成汇总
        for (Map sale : stoLvData) {
            PushMoneyByStoreSalepersonV2OutData outData = new PushMoneyByStoreSalepersonV2OutData();
            outData.setPlanName(tichengZ.getPlanName());
            outData.setStartDate(tichengZ.getPlanStartDate());
            outData.setEndtDate(tichengZ.getPlanEndDate());
            outData.setType("单品提成");
            if ("1".equals(tichengZ.getPlanStatus())) {
                outData.setStatus("1");
            } else if ("2".equals(tichengZ.getDeleteFlag())) {
                outData.setStatus("2");
            }
            outData.setStoCode(String.valueOf(sale.get("brId")));
            outData.setStoName(String.valueOf(sale.get("brName")));
            outData.setDays(BigDecimalUtil.toBigDecimal(sale.get("days")));
            outData.setAmt(BigDecimalUtil.toBigDecimal(sale.get("amt")));
            outData.setSalerId(String.valueOf(sale.get("salerId")));
            outData.setSalerName(String.valueOf(sale.get("salerName")));
            BigDecimal c = BigDecimal.ZERO;
            //筛选出对应门店和营业员的数据
            for (int i = 0; i < saleData.size(); i++) {
                ProductSaleAmt a = saleData.get(i);
                if (sale.get("clientId").equals(a.getClientId()) && sale.get("brId").equals(a.getBrId()) && sale.get("salerId").equals(a.getSalerId())) {
                    if (StringUtils.isEmpty(a.getTcAmt())) {
                        a.setTcAmt("0");
                    }
                    c = c.add(new BigDecimal(a.getTcAmt()));
                }
            }
            outData.setDeductionWage(c);
            outData.setDeductionWageRate(BigDecimalUtil.divide(c, sale.get("amt"), 4));
            outDatas.add(outData);
        }
        outDatas = outDatas.stream()
                .sorted(Comparator.comparing(PushMoneyByStoreSalepersonV2OutData::getStoCode))
                .sorted(Comparator.comparing(PushMoneyByStoreSalepersonV2OutData::getSalerId))
                .collect(Collectors.toList());
        return outDatas;
    }

}




