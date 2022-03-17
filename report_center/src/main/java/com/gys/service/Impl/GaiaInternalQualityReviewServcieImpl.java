package com.gys.service.Impl;

import com.github.pagehelper.PageHelper;
import com.gys.common.constant.CommonConstant;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.PageInfo;
import com.gys.common.enums.SerialCodeTypeEnum;
import com.gys.common.exception.BusinessException;
import com.gys.common.response.Result;
import com.gys.common.vo.GaiaInternalQualityReviewVo;
import com.gys.entity.GaiaInternalQualityReview;
import com.gys.entity.data.GaiaInternalQualityReviewExcelData;
import com.gys.entity.data.GaiaInternalQualityReviewInData;
import com.gys.mapper.GaiaInternalQualityReviewMapper;
import com.gys.service.CommonService;
import com.gys.service.GaiaInternalQualityReviewServcie;
import com.gys.util.CosUtils;
import com.gys.util.DateUtil;
import com.gys.util.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author csm
 * @date 12/31/2021 - 5:55 PM
 */
@Slf4j
@Service
public class GaiaInternalQualityReviewServcieImpl implements GaiaInternalQualityReviewServcie {
    @Resource
    private GaiaInternalQualityReviewMapper gaiaInternalQualityReviewMapper;
    @Resource
    private CommonService commonService;
    @Resource
    public CosUtils cosUtils;
    @Override
    public void addItem(GetLoginOutData loginUser, GaiaInternalQualityReviewInData inData) {
        GaiaInternalQualityReview gaiaInternalQualityReview = new GaiaInternalQualityReview();
        //生成GSP编号
        String serialCode = commonService.getSerialCode(loginUser.getClient(), SerialCodeTypeEnum.QUALITY_REVIEW_CODE);
        BeanUtils.copyProperties(inData,gaiaInternalQualityReview);
        gaiaInternalQualityReview.setVoucherId(serialCode);
        gaiaInternalQualityReview.setClient(loginUser.getClient());
        gaiaInternalQualityReview.setIsDelete(0);
        gaiaInternalQualityReview.setCreateTime(new Date());
        gaiaInternalQualityReview.setCreateUser(loginUser.getLoginName()+"-"+loginUser.getUserId());
        gaiaInternalQualityReviewMapper.insertSelective(gaiaInternalQualityReview);

    }

    @Override
    public void updateItem(GetLoginOutData loginUser, List<GaiaInternalQualityReviewInData> inDatas) {

            for (GaiaInternalQualityReviewInData s : inDatas) {
                GaiaInternalQualityReview gaiaInternalQualityReview = new GaiaInternalQualityReview();
                BeanUtils.copyProperties(s,gaiaInternalQualityReview);
                gaiaInternalQualityReview.setUpdateTime(new Date());
                gaiaInternalQualityReview.setUpdateUser(loginUser.getLoginName() + "-" + loginUser.getUserId());
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getReviewQuality())){
                    throw new BusinessException("评审质量不能为空");
                }
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getReviewOrg())){
                    throw new BusinessException("审核组织不能为空");
                }
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getTrialDep())){
                    throw new BusinessException("受审部门不能为空");
                }
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getReviewTarget())){
                    throw new BusinessException("审核目的不能为空");
                }
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getReviewScope())){
                    throw new BusinessException("评审范围不能为空");
                }
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getReviewBase())){
                    throw new BusinessException("审核依据不能为空");
                }
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getReviewContent())){
                    throw new BusinessException("评审内容不能为空");
                }
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getSuggest())){
                    throw new BusinessException("建议不能为空");
                }
                if(StringUtils.isEmpty(gaiaInternalQualityReview.getEffectContent())){
                    throw new BusinessException("实施情况不能为空");
                }
                gaiaInternalQualityReviewMapper.updateByPrimaryKeySelective(gaiaInternalQualityReview);
            }

    }

    @Override
    public void delItem(GetLoginOutData loginUser, List<GaiaInternalQualityReview> inDatas) {
        for (GaiaInternalQualityReview d : inDatas) {
            d.setUpdateUser(loginUser.getLoginName() + "-" + loginUser.getUserId());
            d.setUpdateTime(new Date());
            d.setIsDelete(1);
            gaiaInternalQualityReviewMapper.updateByPrimaryKeySelective(d);
        }
    }

    @Override
    public PageInfo<GaiaInternalQualityReviewVo> getItem(GaiaInternalQualityReviewInData inData) {
        PageHelper.startPage(inData.getPageNum(), inData.getPageSize());
        List<GaiaInternalQualityReview> items = gaiaInternalQualityReviewMapper.getItem(inData);
        List<GaiaInternalQualityReviewVo> list = new ArrayList<>();
        PageInfo pageInfo;
        if(CollectionUtils.isEmpty(items)){
            pageInfo = new PageInfo(new ArrayList<>());
        }else{
            for (GaiaInternalQualityReview item : items) {
                GaiaInternalQualityReviewVo vo = new GaiaInternalQualityReviewVo();
                BeanUtils.copyProperties(item,vo);
                vo.setReviewTime(DateUtil.formatDate(item.getReviewTime(),"yyyy-MM-dd"));
                list.add(vo);
            }
            pageInfo = new PageInfo(list);
        }
        return  pageInfo;
    }

    @Override
    public List<GaiaInternalQualityReview> getExcelItem(GaiaInternalQualityReviewInData inData) {
        List<GaiaInternalQualityReview> items = gaiaInternalQualityReviewMapper.getItem(inData);

        if(CollectionUtils.isEmpty(items)){
            return new ArrayList<>();
        }
        return items;
    }

    @Override
    public Result exportExcel(GaiaInternalQualityReviewInData inData) {
        List<GaiaInternalQualityReview> items = this.getExcelItem(inData);
        List<GaiaInternalQualityReviewExcelData> giqrelist = new ArrayList<>();
        if(CollectionUtils.isEmpty(items)){
            throw new BusinessException("未查到相关数据！");
        }
        for (int i = 0; i < items.size(); i++) {
            GaiaInternalQualityReviewExcelData ed = new GaiaInternalQualityReviewExcelData();
            BeanUtils.copyProperties(items.get(i),ed);
            ed.setIndex(i + 1);
            giqrelist.add(ed);
        }
        //组装导出内容
        List<List<Object>> dataList = new ArrayList<>(giqrelist.size());
        for (GaiaInternalQualityReviewExcelData giqred : giqrelist) {
            //每行数据
            List<Object> lineList = new ArrayList<>();
            //序号
            lineList.add(giqred.getIndex());
            //gsp编号
            lineList.add(giqred.getVoucherId());
            //评审日期
            lineList.add(DateUtil.formatDate(giqred.getReviewTime(),"yyyy-MM-dd"));
            //评审人员
            lineList.add(giqred.getReviewUser());
            //评审内容
            lineList.add(giqred.getReviewContent());
            //评审质量
            lineList.add(giqred.getReviewQuality());
            //建议
            lineList.add(giqred.getSuggest());
            //实施情况
            lineList.add(giqred.getEffectContent());
            //审核目的
            lineList.add(giqred.getReviewTarget());
            //审核依据
            lineList.add(giqred.getReviewBase());
            //审核范围
            lineList.add(giqred.getReviewScope());
            //审核组织
            lineList.add(giqred.getReviewOrg());
            //受审部门
            lineList.add(giqred.getTrialDep());
            dataList.add(lineList);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HSSFWorkbook workbook = ExcelUtils.exportExcel2(
                new ArrayList<String[]>() {{
                    add(CommonConstant.GAIA_INTERNAL_QUALITY_REVIEW);
                }},
                new ArrayList<List<List<Object>>>() {{
                    add(dataList);
                }},
                new ArrayList<String>() {{
                    add(CommonConstant.GAIA_INTERNAL_QUALITY_REVIEW_NAME);
                }});
        Result uploadResult = null;
        try {
            workbook.write(bos);
            String fileName = CommonConstant.GAIA_INTERNAL_QUALITY_REVIEW_NAME + ".xls";
            uploadResult = cosUtils.uploadFile(bos, fileName);
            bos.flush();
        } catch (IOException e) {
            log.error("导出文件失败:{}", e.getMessage(), e);
            throw new BusinessException("导出文件失败！");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                log.error("关闭流异常:{}", e.getMessage(), e);
                throw new BusinessException("关闭流异常！");
            }
        }
        return uploadResult;
    }

}
