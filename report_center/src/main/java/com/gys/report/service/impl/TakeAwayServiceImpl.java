//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gys.report.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.gys.common.data.JsonResult;
import com.gys.common.data.PageInfo;
import com.gys.common.enums.O2OorderStatusEnum;
import com.gys.common.exception.BusinessException;
import com.gys.mapper.GaiaTOrderInfoMapper;
import com.gys.report.entity.WebOrderDataDto;
import com.gys.report.entity.WebOrderDetailDataDto;
import com.gys.report.entity.WebOrderQueryBean;
import com.gys.report.service.TakeAwayService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class TakeAwayServiceImpl implements TakeAwayService {
    private static final Logger logger = LoggerFactory.getLogger(TakeAwayServiceImpl.class);
    @Resource
    private GaiaTOrderInfoMapper gaiaTOrderInfoMapper;

    public TakeAwayServiceImpl() {
    }

    public JsonResult orderQuery(WebOrderQueryBean bean) {
        List<WebOrderDataDto> dtoList = this.getOrderQueryList(bean);
        WebOrderDataDto webOrderDataDto;
        if (CollUtil.isEmpty(dtoList)) {
            webOrderDataDto = new WebOrderDataDto();
        } else {
            webOrderDataDto = this.getSumDto(dtoList);
        }

        PageInfo<WebOrderDataDto> pageInfo = new PageInfo(dtoList, webOrderDataDto);
        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    public void orderQueryOutput(WebOrderQueryBean bean, HttpServletRequest request, HttpServletResponse response) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String format = formatter.format(now);
        String file = "O2O订单汇总" + format + ".CSV";
        response.setContentType("application/vnd.ms-excel");

        try {
            response.setHeader("Content-disposition", "attachment;filename=" + file + ";filename*=utf-8''" + file);
            bean.setPageSize(-1);
            List<WebOrderDataDto> dtoList = this.getOrderQueryList(bean);
            WebOrderDataDto webOrderDataDto;
            if (CollUtil.isEmpty(dtoList)) {
                webOrderDataDto = new WebOrderDataDto();
            } else {
                webOrderDataDto = this.getSumDto(dtoList);
            }

            webOrderDataDto.setIndex("合计");
            dtoList.add(webOrderDataDto);
            EasyExcel.write(response.getOutputStream(), WebOrderDataDto.class).sheet("sheet1").doWrite(dtoList);
        } catch (Exception var10) {
            logger.error("O2O订单汇总导出失败：" + var10.getMessage());
            throw new BusinessException("导出失败");
        }
    }

    public JsonResult orderDetailQuery(WebOrderQueryBean bean) {
        List<WebOrderDetailDataDto> dtoList = this.getOrderDetailQuery(bean);
        WebOrderDetailDataDto dataDto;
        if (CollUtil.isEmpty(dtoList)) {
            dataDto = new WebOrderDetailDataDto();
        } else {
            dataDto = this.getDetailSumDto(dtoList);
        }

        PageInfo<WebOrderDetailDataDto> pageInfo = new PageInfo(dtoList, dataDto);
        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    public void orderDetailQueryOutput(WebOrderQueryBean bean, HttpServletRequest request, HttpServletResponse response) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String format = formatter.format(now);
        String file = "O2O订单明细" + format + ".CSV";
        response.setContentType("application/vnd.ms-excel");

        try {
            response.setHeader("Content-disposition", "attachment;filename=" + file + ";filename*=utf-8''" + file);
            bean.setPageSize(-1);
            List<WebOrderDetailDataDto> dtoList = this.getOrderDetailQuery(bean);
            WebOrderDetailDataDto dataDto;
            if (CollUtil.isEmpty(dtoList)) {
                dataDto = new WebOrderDetailDataDto();
            } else {
                dataDto = this.getDetailSumDto(dtoList);
            }

            dataDto.setIndex("合计");
            dtoList.add(dataDto);
            EasyExcel.write(response.getOutputStream(), WebOrderDetailDataDto.class).sheet("sheet1").doWrite(dtoList);
        } catch (Exception var10) {
            logger.error("O2O订单明细导出失败：" + var10.getMessage());
            throw new BusinessException("导出失败");
        }
    }

    private List<WebOrderDataDto> getOrderQueryList(WebOrderQueryBean bean) {
        if (-1 != bean.getPageSize()) {
            PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        }

        String[] stoCodeOrName = bean.getStoCodeOrName();
        if (stoCodeOrName != null && stoCodeOrName.length != 0) {
            List<String> stoCodeList = Lists.newArrayList();
            String[] var4 = stoCodeOrName;
            int var5 = stoCodeOrName.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String s = var4[var6];
                stoCodeList.add(s);
            }

            bean.setStoCodeList(stoCodeList);
        }

        List<WebOrderDataDto> dtoList = this.gaiaTOrderInfoMapper.orderQuery(bean);

        for(int i = 0; i < dtoList.size(); ++i) {
            WebOrderDataDto dto = (WebOrderDataDto)dtoList.get(i);
            dto.setIndex(String.valueOf(i + 1));
            dto.setStatus(O2OorderStatusEnum.getStatus(Integer.valueOf(dto.getStatus())));
        }

        return dtoList;
    }

    private WebOrderDataDto getSumDto(List<WebOrderDataDto> dtoList) {
        WebOrderDataDto dto = new WebOrderDataDto();
        dto.setStoCode(String.valueOf(dtoList.stream().map((r) -> {
            return r.getStoCode();
        }).distinct().count()));
        dto.setPlatform(String.valueOf(dtoList.stream().map((r) -> {
            return r.getPlatform();
        }).distinct().count()));
        dto.setPlatformOrderId(String.valueOf(dtoList.stream().map((r) -> {
            return r.getPlatformOrderId();
        }).distinct().count()));
        dto.setOrderId(String.valueOf(dtoList.stream().map((r) -> {
            return r.getOrderId();
        }).distinct().count()));
        BigDecimal platformOriginalPrice = (BigDecimal)dtoList.stream().filter((r) -> {
            return StrUtil.isNotEmpty(r.getPlatformOriginalPrice());
        }).map((r) -> {
            return BigDecimal.valueOf(Double.valueOf(r.getPlatformOriginalPrice()));
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        platformOriginalPrice = platformOriginalPrice.setScale(2, RoundingMode.HALF_UP);
        dto.setPlatformOriginalPrice(String.valueOf(platformOriginalPrice));
        BigDecimal platformZkPrice = (BigDecimal)dtoList.stream().filter((r) -> {
            return !ObjectUtils.isEmpty(r.getPlatformZkPrice());
        }).map((r) -> {
            return r.getPlatformZkPrice();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        platformZkPrice = platformZkPrice.setScale(2, RoundingMode.HALF_UP);
        dto.setPlatformZkPrice(platformZkPrice);
        BigDecimal platformShippingFee = (BigDecimal)dtoList.stream().filter((r) -> {
            return StrUtil.isNotEmpty(r.getPlatformShippingFee());
        }).map((r) -> {
            return BigDecimal.valueOf(Double.valueOf(r.getPlatformShippingFee()));
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        platformShippingFee = platformShippingFee.setScale(2, RoundingMode.HALF_UP);
        dto.setPlatformShippingFee(String.valueOf(platformShippingFee));
        BigDecimal platformUseFee = (BigDecimal)dtoList.stream().filter((r) -> {
            return !ObjectUtils.isEmpty(r.getPlatformUseFee());
        }).map((r) -> {
            return r.getPlatformUseFee();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        platformUseFee = platformUseFee.setScale(2, RoundingMode.HALF_UP);
        dto.setPlatformUseFee(platformUseFee);
        BigDecimal platformActualPrice = (BigDecimal)dtoList.stream().filter((r) -> {
            return !ObjectUtils.isEmpty(r.getPlatformActualPrice());
        }).map((r) -> {
            return r.getPlatformActualPrice();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        platformActualPrice = platformActualPrice.setScale(2, RoundingMode.HALF_UP);
        dto.setPlatformActualPrice(platformActualPrice);
        BigDecimal customerPay = (BigDecimal)dtoList.stream().filter((r) -> {
            return !ObjectUtils.isEmpty(r.getCustomerPay());
        }).map((r) -> {
            return r.getCustomerPay();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        customerPay = customerPay.setScale(2, RoundingMode.HALF_UP);
        dto.setCustomerPay(customerPay);
        dto.setSaleOrderId(String.valueOf(dtoList.stream().filter((r) -> {
            return StrUtil.isNotEmpty(r.getSaleOrderId());
        }).map((r) -> {
            return r.getSaleOrderId();
        }).distinct().count()));
        BigDecimal saleTotalAmt = (BigDecimal)dtoList.stream().filter((r) -> {
            return !ObjectUtils.isEmpty(r.getSaleTotalAmt());
        }).map((r) -> {
            return r.getSaleTotalAmt();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        saleTotalAmt = saleTotalAmt.setScale(2, RoundingMode.HALF_UP);
        dto.setSaleTotalAmt(saleTotalAmt);
        BigDecimal saleActualPrice = (BigDecimal)dtoList.stream().filter((r) -> {
            return !ObjectUtils.isEmpty(r.getSaleActualPrice());
        }).map((r) -> {
            return r.getSaleActualPrice();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        saleActualPrice = saleActualPrice.setScale(2, RoundingMode.HALF_UP);
        dto.setSaleActualPrice(saleActualPrice);
        BigDecimal saleZkPrice = (BigDecimal)dtoList.stream().filter((r) -> {
            return !ObjectUtils.isEmpty(r.getSaleZkPrice());
        }).map((r) -> {
            return r.getSaleZkPrice();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        saleZkPrice = saleZkPrice.setScale(2, RoundingMode.HALF_UP);
        dto.setSaleZkPrice(saleZkPrice);
        return dto;
    }

    private List<WebOrderDetailDataDto> getOrderDetailQuery(WebOrderQueryBean bean) {
        if (-1 != bean.getPageSize()) {
            PageHelper.startPage(bean.getPageNum(), bean.getPageSize());
        }

        String proId = bean.getProId();
        if (StrUtil.isNotEmpty(proId)) {
            String[] split = proId.split(",");
            List<String> proIdList = new ArrayList(split.length);
            String[] var5 = split;
            int var6 = split.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String s = var5[var7];
                proIdList.add(s);
            }

            bean.setProIdList(proIdList);
        }

        List<WebOrderDetailDataDto> dtoList = this.gaiaTOrderInfoMapper.orderDetailQuery(bean);

        for(int i = 0; i < dtoList.size(); ++i) {
            WebOrderDetailDataDto dto = (WebOrderDetailDataDto)dtoList.get(i);
            dto.setIndex(String.valueOf(i + 1));
        }

        return dtoList;
    }

    private WebOrderDetailDataDto getDetailSumDto(List<WebOrderDetailDataDto> dtoList) {
        WebOrderDetailDataDto dto = new WebOrderDetailDataDto();
        dto.setPlatformOrderId(String.valueOf(dtoList.stream().map((r) -> {
            return r.getPlatformOrderId();
        }).distinct().count()));
        dto.setOrderId(String.valueOf(dtoList.stream().map((r) -> {
            return r.getOrderId();
        }).distinct().count()));
        BigDecimal num = (BigDecimal)dtoList.stream().filter((r) -> {
            return StrUtil.isNotEmpty(r.getNum());
        }).map((r) -> {
            return BigDecimal.valueOf(Double.valueOf(r.getNum()));
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        num = num.setScale(2, RoundingMode.HALF_UP);
        dto.setNum(String.valueOf(num));
        BigDecimal saleActualPrice = (BigDecimal)dtoList.stream().filter((r) -> {
            return!ObjectUtils.isEmpty(r.getSaleActualPrice());
        }).map((r) -> {
            return r.getSaleActualPrice();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        saleActualPrice = saleActualPrice.setScale(2, RoundingMode.HALF_UP);
        dto.setSaleActualPrice(saleActualPrice);
        BigDecimal saleZkPrice = (BigDecimal)dtoList.stream().filter((r) -> {
            return !ObjectUtils.isEmpty(r.getSaleZkPrice());
        }).map((r) -> {
            return r.getSaleZkPrice();
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        saleZkPrice = saleZkPrice.setScale(2, RoundingMode.HALF_UP);
        dto.setSaleZkPrice(saleZkPrice);
        return dto;
    }
}
