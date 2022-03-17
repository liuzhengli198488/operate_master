//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.common.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gys.common.data.GetLoginOutData;
import com.gys.common.data.JsonResult;
import com.gys.common.exception.BusinessException;
import com.gys.common.redis.RedisManager;
import com.gys.feign.AuthService;
import com.gys.feign.vo.UserRestrictInfo;
import com.gys.util.UtilMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BaseController {
    @Autowired
    private RedisManager redisManager;

    @Autowired
    private AuthService authService;

    public BaseController() {
    }

    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
    }

    public GetLoginOutData getLoginUser(HttpServletRequest request) {
        String token = getRequestHeaderParameter("X-Token");
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException(UtilMessage.Token);
        }
        String userInfo = (String)this.redisManager.get(request.getHeader("X-Token"));
        if(StringUtils.isEmpty(userInfo)){
            throw new BusinessException(UtilMessage.TOKEN_ILEGAL);
        }
        JSONObject jsonObject = JSON.parseObject(userInfo);
        GetLoginOutData data = JSONObject.toJavaObject(jsonObject, GetLoginOutData.class);
        //获取数据权限
        data.setUserRestrictInfo(this.getRestrictStoCodes(data));
        return data;
    }

    private UserRestrictInfo getRestrictStoCodes(GetLoginOutData loginOutData){
        UserRestrictInfo res = new UserRestrictInfo();
        try {
            UserRestrictInfo userRestrictInfo = authService.getRestrictStoreList(loginOutData);;
            String restrictType = userRestrictInfo.getRestrictType();
            res.setRestrictType(restrictType);
            res.setRestrictStoCodes(userRestrictInfo.getRestrictStoCodes());
        }catch (Exception e){
            throw new BusinessException(UtilMessage.RESTRICT_ERROR);
        }
        return res;
    }

    public static String getRequestHeaderParameter(String key) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            return request.getHeader(key);
        }
        return null;
    }

    public GetLoginOutData getLoginUser(String token) {
        String userInfo = (String)this.redisManager.get(token);
        JSONObject jsonObject = JSON.parseObject(userInfo);
        GetLoginOutData data = JSONObject.toJavaObject(jsonObject, GetLoginOutData.class);
        return data;
    }


//    private String getCellValue(Cell cell) {
//        String firstCell = "";
//        if (cell == null) {
//            firstCell = "";
//        } else if (cell.getCellType() == 0) {
//            if (HSSFDateUtil.isCellDateFormatted(cell)) {
//                SimpleDateFormat sdf = null;
//                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
//                    sdf = new SimpleDateFormat("HH:mm");
//                } else {
//                    sdf = new SimpleDateFormat("yyyy-MM-dd");
//                }
//
//                Date date = cell.getDateCellValue();
//                firstCell = sdf.format(date);
//            } else {
//                DecimalFormat df = new DecimalFormat("0");
//                firstCell = df.format(cell.getNumericCellValue());
//            }
//        } else if (cell.getCellType() == 1) {
//            firstCell = cell.getStringCellValue();
//        }
//
//        return firstCell;
//    }

//    public <T> List<T> importExcel(MultipartFile uploadExcel, Class<T> t, Integer sheetNo, Integer firstRowNum) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
//        Workbook workbook = null;
//        String fileName = uploadExcel.getOriginalFilename();
//        String fileEnd = fileName.substring(fileName.indexOf(".") + 1);
//        Sheet sheet = null;
//        if ("xls".equals(fileEnd)) {
//            workbook = new HSSFWorkbook(uploadExcel.getInputStream());
//            sheet = (HSSFSheet)workbook.getSheetAt(sheetNo);
//        } else {
//            workbook = new XSSFWorkbook(uploadExcel.getInputStream());
//            sheet = (XSSFSheet)workbook.getSheetAt(sheetNo);
//        }
//
//        List<T> list = new ArrayList();
//
//        for(int i = firstRowNum; i <= ((Sheet)sheet).getLastRowNum(); ++i) {
//            Row row = ((Sheet)sheet).getRow(i);
//            if (row != null) {
//                T instance = t.newInstance();
//                Field[] fields = instance.getClass().getDeclaredFields();
//                int count = 0;
//                int length = fields.length;
//
//                for(int k = 0; k < length; ++k) {
//                    Method setMethod = instance.getClass().getMethod("set" + StringUtils.capitalize(fields[k].getName()), String.class);
//                    if (k == length - 1) {
//                        String index = i + 1 + "";
//                        setMethod.invoke(instance, index);
//                    } else {
//                        Cell cell = row.getCell(k);
//                        if (cell == null) {
//                            setMethod.invoke(instance, "");
//                            ++count;
//                        } else {
//                            cell.setCellType(CellType.STRING);
//                            if (StrUtil.isBlank(cell.getStringCellValue())) {
//                                ++count;
//                            }
//
//                            setMethod.invoke(instance, cell.getStringCellValue());
//                        }
//                    }
//                }
//
//                if (count != length - 1) {
//                    list.add(instance);
//                }
//            }
//        }
//
//        return list;
//    }
}
