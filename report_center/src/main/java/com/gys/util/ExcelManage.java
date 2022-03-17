package com.gys.util;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gys.common.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从excel读取数据/往excel中写入 excel有表头，表头每列的内容对应实体类的属性
 *
 * @author haoyongqiang
 *
 */

public class ExcelManage {
//    private HSSFWorkbook workbook;

//    public ExcelManage(String fileDir) {
//        File file = new File(fileDir);
//        try {
//            workbook = new HSSFWorkbook(new FileInputStream(file));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 读取excel表中的数据.
     *
     * @param sheet
     *            表格索引(EXCEL 是多表文档,所以需要输入表索引号，如sheet1)
     */
    public static <T> List readFromExcel(Sheet sheet, T object) {
        List result = new ArrayList();
        // 获取该对象的class对象
        Class class_ = object.getClass();
        // 获得该类的所有属性
        Field[] fields = class_.getDeclaredFields();

//        // 读取excel数据
//        // 获得指定的excel表
//        HSSFSheet sheet = workbook.getSheet(sheetName);
//        // 获取表格的总行数
        int rowCount = sheet.getLastRowNum() + 1; // 需要加一
        if (rowCount < 1) {
            throw new BusinessException("EXCEL导入信息为空");
        }
        // 获取表头的列数
        int columnCount = sheet.getRow(0).getLastCellNum();
        // 读取表头信息,确定需要用的方法名---set方法
        // 用于存储方法名
        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数
        // 用于存储属性类型
        String[] fieldTypes = new String[columnCount];
        // 获得表头行对象
        Row titleRow = sheet.getRow(0);
        // 遍历
//        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头列
            String data = titleRow.getCell(columnIndex).toString().trim(); // 某一列的内容
            //中文表头转换英文(下次优化可用Map封装)
            String titleName = changeTitle().get(data);
            if (StringUtils.isNotEmpty(titleName)) {
                String Udata = Character.toUpperCase(titleName.charAt(0))
                        + titleName.substring(1, titleName.length()); // 使其首字母大写
                methodNames[columnIndex] = "set" + Udata;
                for (int i = 0; i < fields.length; i++) { // 遍历属性数组
                    if (titleName.equals(fields[i].getName())) { // 属性与表头相等
                        fieldTypes[columnIndex] = fields[i].getType().getName(); // 将属性类型放到数组中
                    }
                }
            }
        }
        System.out.println(fieldTypes);
        // 逐行读取数据 从1开始 忽略表头
        for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
            // 获得行对象
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                Object obj = null;
                // 实例化该泛型类的对象一个对象
                try {
                    obj = class_.newInstance();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                // 获得本行中各单元格中的数据
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    String data = "";
                    if (ObjectUtil.isNotEmpty(row.getCell(columnIndex))) {
                        data = row.getCell(columnIndex).toString().trim();
                    }
                    // 获取要调用方法的方法名
                    String methodName = methodNames[columnIndex];
                    Method method = null;
                    if(ObjectUtil.isNotEmpty(fieldTypes[columnIndex])) {
                        try {
                            // 这部分可自己扩展
                            if (fieldTypes[columnIndex].equals("java.lang.String")) {
                                method = class_.getDeclaredMethod(methodName,
                                        String.class); // 设置要执行的方法--set方法参数为String
                                method.invoke(obj, data); // 执行该方法
                            } else if (fieldTypes[columnIndex].equals("int")) {
                                method = class_.getDeclaredMethod(methodName,
                                        int.class); // 设置要执行的方法--set方法参数为int
                                double data_double = Double.parseDouble(data);
                                int data_int = (int) data_double;
                                method.invoke(obj, data_int); // 执行该方法
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                result.add(obj);
            }
        }
        return result;
    }


    public static <T> List readFromExcelExtends(Sheet sheet, T object,Map<String,String> headerMap) {
        List result = new ArrayList();
        // 获取该对象的class对象
        Class class_ = object.getClass();
        // 获得该类的所有属性
        Field[] fields = class_.getDeclaredFields();

//        // 读取excel数据
//        // 获得指定的excel表
//        HSSFSheet sheet = workbook.getSheet(sheetName);
//        // 获取表格的总行数
        int rowCount = sheet.getLastRowNum() + 1; // 需要加一
        if (rowCount < 1) {
            throw new BusinessException("EXCEL导入信息为空");
        }
        // 获取表头的列数
        int columnCount = sheet.getRow(0).getLastCellNum();
        // 读取表头信息,确定需要用的方法名---set方法
        // 用于存储方法名
        String[] methodNames = new String[columnCount]; // 表头列数即为需要的set方法个数
        // 用于存储属性类型
        String[] fieldTypes = new String[columnCount];
        // 获得表头行对象
        Row titleRow = sheet.getRow(0);
        // 遍历
//        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头列
            String data = titleRow.getCell(columnIndex).toString().trim(); // 某一列的内容
            //中文表头转换英文(下次优化可用Map封装)
            String titleName = headerMap.get(data);
            if (StringUtils.isNotEmpty(titleName)) {
                String Udata = Character.toUpperCase(titleName.charAt(0))
                        + titleName.substring(1, titleName.length()); // 使其首字母大写
                methodNames[columnIndex] = "set" + Udata;
                for (int i = 0; i < fields.length; i++) { // 遍历属性数组
                    if (titleName.equals(fields[i].getName())) { // 属性与表头相等
                        fieldTypes[columnIndex] = fields[i].getType().getName(); // 将属性类型放到数组中
                    }
                }
            }
        }
        System.out.println(fieldTypes);
        // 逐行读取数据 从1开始 忽略表头
        for (int rowIndex = 1; rowIndex <= rowCount; rowIndex++) {
            // 获得行对象
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                Object obj = null;
                // 实例化该泛型类的对象一个对象
                try {
                    obj = class_.newInstance();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                // 获得本行中各单元格中的数据
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    String data = "";
                    if (ObjectUtil.isNotEmpty(row.getCell(columnIndex))) {
                        if(row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getCellTypeEnum().equals(CellType.NUMERIC)){
                            long longVal = Math.round(row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                            data = longVal + "";
                        }else{
                            data =  row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
                        }
                    }
                    // 获取要调用方法的方法名
                    String methodName = methodNames[columnIndex];
                    Method method = null;
                    if(ObjectUtil.isNotEmpty(fieldTypes[columnIndex])) {
                        try {
                            // 这部分可自己扩展
                            if (fieldTypes[columnIndex].equals("java.lang.String")) {
                                method = class_.getDeclaredMethod(methodName,
                                        String.class); // 设置要执行的方法--set方法参数为String
                                method.invoke(obj, data); // 执行该方法
                            } else if (fieldTypes[columnIndex].equals("int")) {
                                method = class_.getDeclaredMethod(methodName,
                                        int.class); // 设置要执行的方法--set方法参数为int
                                double data_double = Double.parseDouble(data);
                                int data_int = (int) data_double;
                                method.invoke(obj, data_int); // 执行该方法
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                result.add(obj);
            }
        }
        return result;
    }

    //表头中文对应的Map
    public static Map<String,String> changeTitle(){
        Map<String,String> result = new HashMap<>();
        result.put("商品编码","proCode");
        result.put("通用名称","proCommonname");
        result.put("商品名称","proDepict");
        result.put("规格","proSpecs");
        result.put("计量单位","proUnit");
        result.put("批准文号","proRegisterNo");
        result.put("生产企业","proFactoryName");
        result.put("剂型","proForm");
        result.put("国际条形码","proBarcode");
        result.put("处方类别","proPresClass");
        result.put("进项税率","proInputTax");
        result.put("销项税率","proOutputTax");
        result.put("商标","proMark");
        result.put("保质期","proLife");
        result.put("助记码","proPym");
        result.put("商品名","proName");
        result.put("批准文号批准日期","proRegisterDate");
        result.put("批准文号失效日期","proRegisterExdate");
        result.put("商品分类","proClass");
        result.put("商品分类描述","proClassName");
        result.put("成分分类","proCompclass");
        result.put("成分分类描述","proCompclassName");
        result.put("生产企业代码","proFactoryCode");
        result.put("上市许可持有人","proHolder");
        result.put("药品本位码","proBasicCode");
        result.put("税务分类编码","proTaxClass");
        result.put("管制特殊分类","proControlClass");
        result.put("生产类别","proOutputTax");
        result.put("贮存条件","proStorageCondition");
        result.put("商品仓储分区","proStorageArea");
        result.put("长（以MM计算）","proLong");
        result.put("宽（以MM计算）","proWide");
        result.put("高（以MM计算）","proHigh");
        result.put("中包装量","proMidPackage");
        result.put("大包装量","proBigPackage");
        result.put("启用电子监管码","proElectronicCode");
        result.put("生产经营许可证号","proQsCode");
        result.put("最大销售量","proMaxSales");
        result.put("说明书代码","proInstructionCode");
        result.put("说明书内容","proInstruction");
        result.put("国家医保品种编码","proMedProdctCode");
        result.put("生产国家","proCountry");
        result.put("产地","proPlace");
        result.put("状态","proStatus");
        result.put("可服用天数","proTakeDays");
        result.put("用法用量","proUsage");
        result.put("禁忌说明","proInstructionCode");
        result.put("国家医保目录编号","proMedListNum");
        result.put("国家医保目录名称","proMedListName");
        result.put("国家医保医保目录剂型","proMedListForm");
        return result;
    }
}
