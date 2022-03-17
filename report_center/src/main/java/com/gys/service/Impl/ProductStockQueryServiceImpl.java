package com.gys.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gys.common.data.JsonResult;
import com.gys.common.data.ProductStockQueryInData;
import com.gys.common.data.ProductStockQueryOutData;
import com.gys.common.exception.BusinessException;
import com.gys.mapper.ProductStockQueryMapper;
import com.gys.service.ProductStockQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ProductStockQueryServiceImpl implements ProductStockQueryService {
    @Resource
    private ProductStockQueryMapper ProductStockQueryMapper;

    /**
     * @Author jiht
     * @Description 搜索商品信息，需要分页
     * @Date 2022/2/10 16:40
     * @Param [inData]
     * @Return com.gys.common.data.JsonResult
     **/
    @Override
    public JsonResult searchProductInfo(ProductStockQueryInData inData) {
        log.info("[商品库存查询]报表查询开始,入参：{}", JSONObject.toJSONString(inData));
        // 数据校验
        if (StringUtils.isEmpty(inData.getProSearchInfo())) {
            throw new BusinessException("提示：商品查询信息不能为空!");
        }

        // 查询数据
        PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        List<ProductStockQueryOutData> resultList = ProductStockQueryMapper.searchProBasicInfo(inData);
        // 分页处理
        PageInfo pageInfo;
        if (CollectionUtils.isEmpty(resultList)) {
            pageInfo = new PageInfo(new ArrayList());
        } else {
            pageInfo = new PageInfo(resultList);
        }
        log.info("[销售分析]报表查询结束");

        return JsonResult.success(pageInfo, "success");
    }

    /**
     * @Author jiht
     * @Description 查询商品信息
     * @Date 2022/2/10 16:40
     * @Param [inData]
     * @Return com.gys.common.data.JsonResult
     **/
    @Override
    public JsonResult queryProductInfo(ProductStockQueryInData inData) {
        log.info("[商品库存查询]报表查询开始,入参：{}", JSONObject.toJSONString(inData));
        // 数据校验
        if (StringUtils.isEmpty(inData.getProSelfCode())) {
            throw new BusinessException("提示：商品编码不能为空!");
        }
        // 查询数据
        ProductStockQueryOutData result = ProductStockQueryMapper.queryProBasicInfo(inData);
//        result.setTotalStockQty(String.valueOf(Math.round((Double.valueOf(result.getStoreQty()) + Double.valueOf(result.getWmsQty())) * 100) / 100));
        return JsonResult.success(result, "success");
    }

    /**
     * @Author jiht
     * @Description 查询门店合计数据
     * @Date 2022/2/10 16:39
     * @Param [inData]
     * @Return com.gys.common.data.JsonResult
     **/
    @Override
    public JsonResult queryStoreInfo(ProductStockQueryInData inData) {
        log.info("[商品库存查询]门店信息查询开始,入参：{}", JSONObject.toJSONString(inData));
        // 数据校验
        if (StringUtils.isEmpty(inData.getProSelfCode())) {
            throw new BusinessException("提示：商品编码不能为空!");
        }
        List<ProductStockQueryOutData> resultList = new ArrayList<>();
        // 合计数据
        ProductStockQueryOutData sumData = new ProductStockQueryOutData();
        resultList.add(sumData);
        // 查询数据
        List<ProductStockQueryOutData> storeInfoList = ProductStockQueryMapper.queryStoreInfo(inData);
        resultList.addAll(storeInfoList);
        // 门店合计
        calSumData(sumData, storeInfoList);

        return JsonResult.success(resultList, "success");
    }

    /**
     * @Author jiht
     * @Description 计算门店合计
     * @Date 2022/2/11 9:57
     * @Param [sumData, storeInfoList]
     * @Return void
     **/
    private void calSumData (ProductStockQueryOutData sumData,List<ProductStockQueryOutData> storeInfoList) {
        sumData.setStoName("门店合计");
        sumData.setStoreQty("0");
        sumData.setMovingQty("0");
        storeInfoList.stream().forEach(o -> {
            sumData.setStoreQty(String.valueOf(Double.valueOf(sumData.getStoreQty()) + Double.valueOf(o.getStoreQty())));
            sumData.setMovingQty(String.valueOf(Double.valueOf(sumData.getMovingQty()) + Double.valueOf(o.getMovingQty())));
        });
        sumData.setStoreQty(String.valueOf(Math.round(Double.valueOf(sumData.getStoreQty()) * 100) / 100));
        sumData.setMovingQty(String.valueOf(Math.round(Double.valueOf(sumData.getMovingQty()) * 100) / 100));
    }

}
