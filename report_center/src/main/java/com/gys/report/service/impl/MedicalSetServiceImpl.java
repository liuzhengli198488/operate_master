package com.gys.report.service.impl;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.mapper.MedicalSetMapper;
import com.gys.report.entity.*;
import com.gys.report.service.MedicalSetService;
import com.gys.util.CommonUtil;
import com.gys.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MedicalSetServiceImpl implements MedicalSetService{

    @Resource
    private MedicalSetMapper medicalSetMapper;

    @Override
    public PageInfo<MedicalSetOutData> selectMedicalSetList(MedicalSetInData inData) {

        if(null == inData.getPageNum()){
            inData.setPageNum(1);
        }

        if(null == inData.getPageSize()){
            inData.setPageSize(10);
        }
        if(null != inData.getProId()&&inData.getProId().size() > 0){
            inData.setProId(Arrays.asList(inData.getProId().get(0).split(",")));
        }
        PageHelper.startPage(inData.getPageNum(),inData.getPageSize());
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyyMMdd" );
        if(StringUtils.isNotEmpty(inData.getStartDate())){
            try {
                 long startDate = sdf.parse(inData.getStartDate()).getTime();
                 inData.setStartDate(Long.toString(startDate));
            } catch (ParseException e) {
                throw new BusinessException("开始日期格式转换异常");
            }
        }else{
            throw new BusinessException("开始日期必选！");
        }
        if(StringUtils.isNotEmpty(inData.getEndDate())){
            try {
                //查询结束日期加一天
                long endDate = DateUtil.addDay(sdf.parse(inData.getEndDate()),1).getTime();
                inData.setEndDate(Long.toString(endDate));
            } catch (ParseException e) {
                throw new BusinessException("结束日期格式转换异常");
            }
        }else{
            throw new BusinessException("结束日期必选！");
        }
        List<MedicalSetOutData> list = medicalSetMapper.selectMedicalSetList(inData);
        MedicalSetOutTotal medicalSetOutTotal = new MedicalSetOutTotal();
        for(MedicalSetOutData medicalSetOutData: list){
            medicalSetOutTotal.setMedfeeSumant(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getMedfeeSumant()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getMedfeeSumant())));
            medicalSetOutTotal.setFulamtOwnpayAmt(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getFulamtOwnpayAmt()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getFulamtOwnpayAmt())));
            medicalSetOutTotal.setOverlmtSelfpay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getOverlmtSelfpay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getOverlmtSelfpay())));
            medicalSetOutTotal.setPreselfpayAmt(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getPreselfpayAmt()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getPreselfpayAmt())));

            medicalSetOutTotal.setInscpScpAmt(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getInscpScpAmt()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getInscpScpAmt())));
            medicalSetOutTotal.setActPayDedc(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getActPayDedc()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getActPayDedc())));
            medicalSetOutTotal.setHifpPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getHifpPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getHifpPay())));
            medicalSetOutTotal.setCvlservPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getCvlservPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getCvlservPay())));
            medicalSetOutTotal.setHifesPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getHifesPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getHifesPay())));
            medicalSetOutTotal.setHifmiPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getHifmiPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getHifmiPay())));

            medicalSetOutTotal.setHifobPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getHifobPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getHifobPay())));
            medicalSetOutTotal.setMafPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getMafPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getMafPay())));

            medicalSetOutTotal.setOthPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getOthPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getOthPay())));
            medicalSetOutTotal.setFundPaySumamt(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getFundPaySumamt()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getFundPaySumamt())));
            medicalSetOutTotal.setPsnPartAmt(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getPsnPartAmt()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getPsnPartAmt())));
            medicalSetOutTotal.setAcctPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getAcctPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getAcctPay())));

            medicalSetOutTotal.setPsnCashPayamt(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getPsnCashPayamt()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getPsnCashPayamt())));
            medicalSetOutTotal.setBalc(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getBalc()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getBalc())));


            medicalSetOutTotal.setAcctMulaidPay(CommonUtil.stripTrailingZeros(medicalSetOutTotal.getAcctMulaidPay()).add(CommonUtil.stripTrailingZeros(medicalSetOutData.getAcctMulaidPay())));



        }
        PageInfo pageInfo = new PageInfo<>(list);
        pageInfo.setListNum(medicalSetOutTotal);
        return pageInfo;
    }

    @Override
    public List<MedicalSalesOutData> selectMedicalSalesList(MedicalSalesInData inData) {
        List<MedicalSalesOutData> list = medicalSetMapper.selectMedicalSalesList(inData);;
        return list;
    }

    @Override
    public void exportMedicalSetList(MedicalSetInData inData, HttpServletResponse response) {
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyyMMdd" );
        if(StringUtils.isNotEmpty(inData.getStartDate())){
            try {
                long startDate = sdf.parse(inData.getStartDate()).getTime();
                inData.setStartDate(Long.toString(startDate));
            } catch (ParseException e) {
                throw new BusinessException("开始日期格式转换异常");
            }
        }else{
            throw new BusinessException("开始日期必选！");
        }
        if(StringUtils.isNotEmpty(inData.getEndDate())){
            try {
                //查询结束日期加一天
                long endDate = DateUtil.addDay(sdf.parse(inData.getEndDate()),1).getTime();
                inData.setEndDate(Long.toString(endDate));
            } catch (ParseException e) {
                throw new BusinessException("结束日期格式转换异常");
            }
        }else{
            throw new BusinessException("结束日期必选！");
        }
        if(null != inData.getProId()&&inData.getProId().size() > 0){
            inData.setProId(Arrays.asList(inData.getProId().get(0).split(",")));
        }
        List<MedicalSetOutData> list = medicalSetMapper.selectMedicalSetList(inData);
        String fileName = "医保结算信息";
        if (list.size() > 0){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");

            try {
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
                EasyExcel.write(response.getOutputStream(),MedicalSetOutData.class)
                        .sheet("0").doWrite(list);
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException("下载失败");
            }
        }else{
            throw new BusinessException("提示：数据为空！");
        }
    }

    /**
     * 医保销售对账汇总(对总账)
     * @param inData 入参
     * @return
     */
    @Override
    public List<SelectMedicalSummaryDTO> selectMedicalSummaryList(SelectMedicalSummaryData inData) {
        formatDate(inData);
        List<SelectMedicalSummaryDTO> summaryDTOList =  this.medicalSetMapper.selectMedicalSummaryList(inData);
        return summaryDTOList;
    }
    private void formatDate(SelectMedicalSummaryData inData){
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyyMMdd" );
        if(StringUtils.isNotEmpty(inData.getStartDate())){
            try {
                long startDate = sdf.parse(inData.getStartDate()).getTime();
                inData.setStartDate(Long.toString(startDate));
            } catch (ParseException e) {
                throw new BusinessException("开始日期格式转换异常");
            }
        }else{
            throw new BusinessException("开始日期必选！");
        }
        if(StringUtils.isNotEmpty(inData.getEndDate())){
            try {
                //查询结束日期加一天
                long endDate = DateUtil.addDay(sdf.parse(inData.getEndDate()),1).getTime();
                inData.setEndDate(Long.toString(endDate));
            } catch (ParseException e) {
                throw new BusinessException("结束日期格式转换异常");
            }
        }else{
            throw new BusinessException("结束日期必选！");
        }
    }
    /**
     * 医保销售对账汇总(按照机构(名称))
     * @param inData
     * @return
     */
    @Override
    public List<SelectMedicalSummaryDTO> selectMedicalStoreSummaryList(SelectMedicalSummaryData inData) {
        formatDate(inData);
        return this.medicalSetMapper.selectMedicalStoreSummaryList(inData);
    }

    /**
     * 获取险种列表
     * @param userInfo
     * @return
     */
    @Override
    public List<Map<String, String>> selectInSuTypesList(GetLoginOutData userInfo) {

        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("InSuType","310");
        map.put("InSuTypeName","职工基本医疗保险");
        map.put("InSuTypeStr","310-职工基本医疗保险");
        list.add(map);

        map = new HashMap<>();
        map.put("InSuType","320");
        map.put("InSuTypeName","公务员医疗补助");
        map.put("InSuTypeStr","320-公务员医疗补助");
        list.add(map);

        map = new HashMap<>();
        map.put("InSuType","330");
        map.put("InSuTypeName","大额医疗费用补助");
        map.put("InSuTypeStr","330-大额医疗费用补助");
        list.add(map);

        map = new HashMap<>();
        map.put("InSuType","340");
        map.put("InSuTypeName","离休人员医疗保障");
        map.put("InSuTypeStr","340-离休人员医疗保障");
        list.add(map);

        map = new HashMap<>();
        map.put("InSuType","390");
        map.put("InSuTypeName","城乡居民基本医疗保险");
        map.put("InSuTypeStr","390-城乡居民基本医疗保险");
        list.add(map);

        map = new HashMap<>();
        map.put("InSuType","392");
        map.put("InSuTypeName","城乡居民大病医疗保险");
        map.put("InSuTypeStr","392-城乡居民大病医疗保险");
        list.add(map);

        map = new HashMap<>();
        map.put("InSuType","510");
        map.put("InSuTypeName","生育保险");
        map.put("InSuTypeStr","510-生育保险");
        list.add(map);
        return list;
    }
}
