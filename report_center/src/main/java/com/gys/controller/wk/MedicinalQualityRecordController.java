package com.gys.controller.wk;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.gys.common.base.BaseController;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.enums.SerialCodeTypeEnum;
import com.gys.common.exception.BusinessException;
import com.gys.entity.wk.dto.*;
import com.gys.entity.wk.entity.GaiaMedicinalQualityRecord;
import com.gys.entity.wk.entity.GaiaQualityManagementSystemReport;
import com.gys.entity.wk.vo.GaiaMedicinalQualityRecordVo;
import com.gys.entity.wk.vo.GetProductThirdlyOutData;
import com.gys.entity.wk.vo.MedicinalQualityRecordVo;
import com.gys.service.CommonService;
import com.gys.service.MedicinalQualityRecordService;
import com.gys.util.BeanCopyUtils;
import com.gys.util.DateUtil;
import com.gys.util.ExcelStyleUtil;
import com.gys.util.easyExcel.EasyExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther: tzh
 * @Date: 2021/12/30 11:03
 * @Description: 卫康报表
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/medicinal/quality/")
public class MedicinalQualityRecordController extends BaseController {
    @Resource
    private MedicinalQualityRecordService medicinalQualityRecordService;
    @Resource
    private CommonService commonService;

    @PostMapping({"products"})
    public JsonResult products(HttpServletRequest request, @RequestBody GetProductThirdlyInData inData) {
        GetLoginOutData userInfo = this.getLoginUser(request);
        inData.setClient(userInfo.getClient());
        PageInfo<GetProductThirdlyOutData> pageInfo = medicinalQualityRecordService.queryProFourthly(inData);
        return JsonResult.success(pageInfo, "提示：获取数据成功！");
    }

    @PostMapping("add")
    public JsonResult addRecord(HttpServletRequest request, @Valid @RequestBody MedicinalQualityRecordDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        log.info("登陆信息：{}",userInfo);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        GaiaMedicinalQualityRecord record = new GaiaMedicinalQualityRecord();
        BeanCopyUtils.copyPropertiesIgnoreNull(dto, record);
        //组装字段
        String serialCode = commonService.getSerialCode(userInfo.getClient(), SerialCodeTypeEnum.MEDICINAL_QUALITY);
        if (StringUtils.isBlank(serialCode)) {
            throw new BusinessException("流水号生成失败!");
        }
        record.setVoucherId(serialCode);
        record.setCreateTime(new Date());
        String depId = userInfo.getDepId();
        String loginName = userInfo.getLoginName();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(depId)) {
            builder.append("空");
        }else {
            builder.append(depId);
        }
        if (StringUtils.isNotBlank(loginName)) {
            builder.append("-").append(loginName);
        }
        record.setCreateUser(builder.toString());
        record.setUpdateUser(builder.toString());
        record.setUpdateTime(new Date());
        record.setClient(userInfo.getClient());
        record.setIsDelete(1);
        medicinalQualityRecordService.add(record);
        return JsonResult.success(null, "保存成功");
    }

    @PostMapping("/addRecords")
    public JsonResult addRecords(@RequestBody List<MedicinalQualityRecordDto> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            throw new BusinessException("新增对象为空");
        }
        medicinalQualityRecordService.addRecords(dtos);
        return JsonResult.success(null, "新增成功");
    }

    @PostMapping("search")
    public JsonResult searchRecords(HttpServletRequest request, @RequestBody MedicinalQualityRecordQueryDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        dto.setClient(userInfo.getClient());
        if (dto.getPageNum() == null) {
            dto.setPageNum(1);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(50);
        }
        dto.setClient(userInfo.getClient());
        PageInfo<GaiaMedicinalQualityRecordVo> recordList = medicinalQualityRecordService.searchRecords(dto);
        return JsonResult.success(recordList, "查询成功");
    }

    @PostMapping("del")
    public JsonResult delRecords(HttpServletRequest request, @RequestBody MedicinalQualityRecordDelDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessException("未选择删除对象!");
        }
        List<GaiaMedicinalQualityRecord> reportList = medicinalQualityRecordService.selectList(dto.getIds());
        if (CollectionUtils.isEmpty(reportList)) {
            throw new BusinessException("数据异常!");
        }
        List<GaiaMedicinalQualityRecord> reports = reportList.stream().filter(s -> s.getClient().equals(userInfo.getClient())).collect(Collectors.toList());
        if (reports.size() != reportList.size()) {
            throw new BusinessException("存在异常供应商数据，无法删除!");
        }
        String depId = userInfo.getDepId();
        String loginName = userInfo.getLoginName();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(depId)) {
            builder.append(depId);
        }
        if (StringUtils.isNotBlank(loginName)) {
            builder.append("-").append(loginName);
        }
        dto.setUpdateUser(builder.toString());
        medicinalQualityRecordService.del(dto);
        return JsonResult.success(null, "删除成功");
    }

    @PostMapping("record")
    public JsonResult getRecord(HttpServletRequest request, @RequestBody MedicinalQualityRecordDetailDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        if (dto.getId() == null) {
            throw new BusinessException("未选择对象!");
        }
        GaiaMedicinalQualityRecord record = medicinalQualityRecordService.getOneRecord(dto);
        return JsonResult.success(record, "查询成功");
    }

    @PostMapping("exportRecords")
    public void exportRecord(HttpServletRequest request, HttpServletResponse response, @RequestBody MedicinalQualityRecordQueryDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        dto.setClient(userInfo.getClient());
        if (dto.getPageNum() == null) {
            dto.setPageNum(1);
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(50);
        }
        dto.setClient(userInfo.getClient());
        PageInfo<GaiaMedicinalQualityRecordVo> recordList = medicinalQualityRecordService.searchRecords(dto);
        List<GaiaMedicinalQualityRecordVo> recordListList = recordList.getList();
        if (recordListList == null || CollectionUtils.isEmpty(recordListList)) {
            throw new BusinessException("未查询到可导出内容!");
        }
        List<MedicinalQualityRecordVo> dataList = new ArrayList<>();
        if (recordListList.size() > 0) {
            dataList = recordListList.stream().map(gaiaMedicinalQualityRecord -> {
                MedicinalQualityRecordVo exportOutData = new MedicinalQualityRecordVo();
                BeanCopyUtils.copyPropertiesIgnoreNull(gaiaMedicinalQualityRecord, exportOutData);
                exportOutData.setProStorage(getStorage(exportOutData.getProStorage()));
                exportOutData.setProCategory(gaiaMedicinalQualityRecord.getProCategoryName());
                exportOutData.setCreateTime(DateUtil.formatDate(gaiaMedicinalQualityRecord.getCreateTime()));
                return exportOutData;
            }).collect(Collectors.toList());
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "药品质量档案";
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        try {
            EasyExcel.write(response.getOutputStream(), MedicinalQualityRecordVo.class)
                    .registerWriteHandler(ExcelStyleUtil.getStyle())
                    .sheet(0).doWrite(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStorage(String proStorage) {
        //贮存条件 1-常温，2-阴凉，3-冷藏
        if (String.valueOf(1).equals(proStorage)) {
            return "常温";
        }
        if (String.valueOf(2).equals(proStorage)) {
            return "阴凉";
        }
        if (String.valueOf(2).equals(proStorage)) {
            return "冷藏";
        }
        return "其他";
    }

    @PostMapping("/importRecords")
    public JsonResult importRecords(@RequestParam("multipartFile") MultipartFile multipartFile) {
        List<MedicinalQualityRecordUpLoadDto> recordUpLoadDtoList = null;
        try {
            recordUpLoadDtoList = EasyExcelUtil.readExcel(multipartFile.getInputStream(), MedicinalQualityRecordUpLoadDto.class, new EasyExcelUtil.ExcelListener<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // todo 处理数据
        return JsonResult.success(recordUpLoadDtoList, "导入成功");
    }

    @PostMapping("/updateRecords")
    public JsonResult updateRecords(@RequestBody List<MedicinalQualityRecordUpdateDto> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            throw new BusinessException("更新对象为空");
        }
        medicinalQualityRecordService.update(dtos);
        return JsonResult.success(null, "更新成功");
    }

    @PostMapping("/updateRecord")
    public JsonResult updateRecord(HttpServletRequest request, @RequestBody MedicinalQualityRecordUpdateDto dto) {
        GetLoginOutData userInfo = getLoginUser(request);
        if (userInfo == null || StringUtils.isBlank(userInfo.getClient())) {
            throw new BusinessException("用户信息缺失!");
        }
        if (dto.getId() != null) {
            GaiaMedicinalQualityRecord record = medicinalQualityRecordService.selectOne(dto.getId());
            if (record != null) {
                if (!record.getClient().equals(userInfo.getClient())) {
                    throw new BusinessException("供应商不匹配!");
                }
            }
        }
        String depId = userInfo.getDepId();
        String loginName = userInfo.getLoginName();
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(depId)) {
            builder.append(depId);
        }
        if (StringUtils.isNotBlank(loginName)) {
            builder.append("-").append(loginName);
        }
        dto.setUpdateUser(builder.toString());
        dto.setUpdateTime(new Date());
        medicinalQualityRecordService.updateById(dto);
        return JsonResult.success(null, "更新成功");
    }

    @PostMapping("/test")
    public JsonResult test(){
        return JsonResult.success(medicinalQualityRecordService.test(),"提示！");
    }
}
