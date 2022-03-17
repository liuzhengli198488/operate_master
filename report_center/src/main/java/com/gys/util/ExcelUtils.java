package com.gys.util;

import com.gys.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;


/**
 * Excel操作工具类
 *
 * @author ChaiXY
 */

@Component
public class ExcelUtils {
    @Resource
    public CosUtils cosUtils;
    //实际需要上的静态属性

    public static CosUtils sCosUtils;

    @PostConstruct
    public void init() {
        sCosUtils = this.cosUtils;
    }
    public static final String OFFICE_EXCEL_XLS = "xls";
    public static final String OFFICE_EXCEL_XLSX = "xlsx";
    /**
     * 读取指定Sheet也的内容
     *
     * @param filepath filepath 文件全路径
     * @param sheetNo  sheet序号,从0开始,如果读取全文sheetNo设置null
     */
    public static String readExcel(String filepath, Integer sheetNo)
            throws EncryptedDocumentException, InvalidFormatException, IOException {
        StringBuilder sb = new StringBuilder();
        Workbook workbook = getWorkbook(filepath);
        if (workbook != null) {
            if (sheetNo == null) {
                int numberOfSheets = workbook.getNumberOfSheets();
                for (int i = 0; i < numberOfSheets; i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    if (sheet == null) {
                        continue;
                    }
                    sb.append(readExcelSheet(sheet));
                }
            } else {
                Sheet sheet = workbook.getSheetAt(sheetNo);
                if (sheet != null) {
                    sb.append(readExcelSheet(sheet));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 根据文件路径获取Workbook对象
     *
     * @param filepath 文件全路径
     */
    public static Workbook getWorkbook(String filepath)
            throws EncryptedDocumentException, InvalidFormatException, IOException {
        InputStream is = null;
        Workbook wb = null;
        if (StringUtils.isBlank(filepath)) {
            throw new IllegalArgumentException("文件路径不能为空");
        } else {
            String suffiex = getSuffiex(filepath);
            if (StringUtils.isBlank(suffiex)) {
                throw new IllegalArgumentException("文件后缀不能为空");
            }
            if (OFFICE_EXCEL_XLS.equals(suffiex) || OFFICE_EXCEL_XLSX.equals(suffiex)) {
                try {
                    is = new FileInputStream(filepath);
                    wb = WorkbookFactory.create(is);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (wb != null) {
                        wb.close();
                    }
                }
            } else {
                throw new IllegalArgumentException("该文件非Excel文件");
            }
        }
        return wb;
    }

    /**
     * 获取后缀
     *
     * @param filepath filepath 文件全路径
     */
    private static String getSuffiex(String filepath) {
        if (StringUtils.isBlank(filepath)) {
            return "";
        }
        int index = filepath.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filepath.substring(index + 1, filepath.length());
    }

    private static String readExcelSheet(Sheet sheet) {
        StringBuilder sb = new StringBuilder();

        if (sheet != null) {
            int rowNos = sheet.getLastRowNum();// 得到excel的总记录条数
            for (int i = 0; i <= rowNos; i++) {// 遍历行
                Row row = sheet.getRow(i);
                if (row != null) {
                    int columNos = row.getLastCellNum();// 表头总共的列数
                    for (int j = 0; j < columNos; j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);
                            CellAddress cellAddress = new CellAddress(3, 0);
                            if (cell.getAddress().compareTo(cellAddress) >= 0) {
                                sb.append(cell.getStringCellValue() + " ");
                            }
                            // System.out.print(cell.getStringCellValue() + " ");
                        }
                    }
                    if (i > 2) {
                        sb.append("\r\n");
                    }
                }
            }
        }

        return sb.toString();
    }

    /**
     * 读取指定Sheet页的表头
     *
     * @param filepath filepath 文件全路径
     * @param sheetNo  sheet序号,从0开始,必填
     */
    public static Row readTitle(String filepath, int sheetNo)
            throws IOException, EncryptedDocumentException, InvalidFormatException {
        Row returnRow = null;
        Workbook workbook = getWorkbook(filepath);
        if (workbook != null) {
            Sheet sheet = workbook.getSheetAt(sheetNo);
            returnRow = readTitle(sheet);
        }
        return returnRow;
    }

    /**
     * 读取指定Sheet页的表头
     */
    public static Row readTitle(Sheet sheet) throws IOException {
        Row returnRow = null;
        int totalRow = sheet.getLastRowNum();// 得到excel的总记录条数
        for (int i = 0; i < totalRow; i++) {// 遍历行
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            returnRow = sheet.getRow(0);
            break;
        }
        return returnRow;
    }

    /**
     * Java文件操作 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }


    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出数据
     * @param heads
     * @param data
     * @return
     */
    public static HSSFWorkbook exportExcel(List<String[]> heads, List<List<List<String>>> data, List<String> sheetName) {
        if (CollectionUtils.isEmpty(heads) || CollectionUtils.isEmpty(data) || CollectionUtils.isEmpty(sheetName)) {
            return null;
        }
        if (heads.size() != data.size() || heads.size() != sheetName.size()) {
            return null;
        }
        // excel 对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int i = 0; i < sheetName.size(); i++) {
            HSSFSheet sheet = workbook.createSheet(sheetName.get(i));
            // 头部写入
            setHead(workbook, sheet, heads.get(i));
            // 内容写入
            setBody(sheet, data.get(i));
        }
        return workbook;
    }

    /**
     * 数据写入
     * @param sheet
     * @param datas
     */
    private static void setBody(HSSFSheet sheet, List<List<String>> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        // 数据行写入
        for (int i = 0; i < datas.size(); i++) {
            // 行数据
            List<String> data = datas.get(i);
            HSSFRow row = sheet.getRow(i + 1);
            if (row == null) {
                row = sheet.createRow(i + 1);
            }
            // 循环列数据
            for (int c = 0; c < data.size(); c++) {
                HSSFCell cell = row.getCell(c);
                if (cell == null) {
                    cell = row.createCell(c);
                }
                if (StringUtils.isNotBlank(data.get(c))) {
                    cell.setCellValue(data.get(c));
                }
            }
        }
    }

    /**
     * 导出数据
     * @param heads
     * @param data
     * @return
     */
    public static HSSFWorkbook exportExcel2(List<String[]> heads, List<List<List<Object>>> data, List<String> sheetName) {
        if (CollectionUtils.isEmpty(heads) || CollectionUtils.isEmpty(data) || CollectionUtils.isEmpty(sheetName)) {
            return null;
        }
        if (heads.size() != data.size() || heads.size() != sheetName.size()) {
            return null;
        }
        // excel 对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int i = 0; i < sheetName.size(); i++) {
            HSSFSheet sheet = workbook.createSheet(sheetName.get(i));
            // 头部写入
            setHead(workbook, sheet, heads.get(i));
            // 内容写入
            setBody2(sheet, data.get(i));
        }
        return workbook;
    }

    /**
     * 数据写入
     * @param sheet
     * @param datas
     */
    private static void setBody2(HSSFSheet sheet, List<List<Object>> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        // 数据行写入
        for (int i = 0; i < datas.size(); i++) {
            // 行数据
            List<Object> data = datas.get(i);
            HSSFRow row = sheet.getRow(i + 1);
            if (row == null) {
                row = sheet.createRow(i + 1);
            }
            // 循环列数据
            for (int c = 0; c < data.size(); c++) {
                HSSFCell cell = row.getCell(c);
                if (cell == null) {
                    cell = row.createCell(c);
                }
                if (data.get(c) != null) {
                    Object param = data.get(c);
                    if (param instanceof Integer) {
                        int value = ((Integer) param).intValue();
                        cell.setCellValue(value);
                    } else if (param instanceof String) {
                        String value = (String) param;
                        cell.setCellValue(value);
                    } else if (param instanceof Double) {
                        double value = ((Double) param).doubleValue();
                        cell.setCellValue(value);
                    } else if (param instanceof Float) {
                        float value = ((Float) param).floatValue();
                        cell.setCellValue(value);
                    } else if (param instanceof Long) {
                        long value = ((Long) param).longValue();
                        cell.setCellValue(value);
                    } else if (param instanceof Boolean) {
                        boolean value = ((Boolean) param).booleanValue();
                        cell.setCellValue(value);
                    } else if (param instanceof Date) {
                        Date value = (Date) param;
                        cell.setCellValue(value);
                    } else if (param instanceof BigDecimal) {
                        double doubleVal = (new BigDecimal(param.toString())).doubleValue();
                        cell.setCellValue(doubleVal);
                    } else {
                        cell.setCellValue(param.toString());
                    }
                }
            }
        }
    }

    /**
     * 导出数据
     * @param heads
     * @param data
     * @return
     */
    public static XSSFWorkbook exportExcel3(List<String[]> heads, List<List<List<Object>>> data, List<String> sheetName) {
        if (CollectionUtils.isEmpty(heads) || CollectionUtils.isEmpty(data) || CollectionUtils.isEmpty(sheetName)) {
            return null;
        }
        if (heads.size() != data.size() || heads.size() != sheetName.size()) {
            return null;
        }
        // excel 对象
        XSSFWorkbook workbook = new XSSFWorkbook();
        for (int i = 0; i < sheetName.size(); i++) {
            XSSFSheet sheet = workbook.createSheet(sheetName.get(i));
            // 头部写入
            setHead3(workbook, sheet, heads.get(i));
            // 内容写入
            setBody3(sheet, data.get(i));
        }
        return workbook;
    }

    /**
     * 数据写入
     * @param sheet
     * @param datas
     */
    private static void setBody3(XSSFSheet sheet, List<List<Object>> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        // 数据行写入
        for (int i = 0; i < datas.size(); i++) {
            // 行数据
            List<Object> data = datas.get(i);
            XSSFRow row = sheet.getRow(i + 1);
            if (row == null) {
                row = sheet.createRow(i + 1);
            }
            // 循环列数据
            for (int c = 0; c < data.size(); c++) {
                XSSFCell cell = row.getCell(c);
                if (cell == null) {
                    cell = row.createCell(c);
                }
                if (data.get(c) != null) {
                    Object param = data.get(c);
                    if (param instanceof Integer) {
                        int value = ((Integer) param).intValue();
                        cell.setCellValue(value);
                    } else if (param instanceof String) {
                        String value = (String) param;
                        cell.setCellValue(value);
                    } else if (param instanceof Double) {
                        double value = ((Double) param).doubleValue();
                        cell.setCellValue(value);
                    } else if (param instanceof Float) {
                        float value = ((Float) param).floatValue();
                        cell.setCellValue(value);
                    } else if (param instanceof Long) {
                        long value = ((Long) param).longValue();
                        cell.setCellValue(value);
                    } else if (param instanceof Boolean) {
                        boolean value = ((Boolean) param).booleanValue();
                        cell.setCellValue(value);
                    } else if (param instanceof Date) {
                        Date value = (Date) param;
                        cell.setCellValue(value);
                    } else if (param instanceof BigDecimal) {
                        double doubleVal = (new BigDecimal(param.toString())).doubleValue();
                        cell.setCellValue(doubleVal);
                    } else {
                        cell.setCellValue(param.toString());
                    }
                }
            }
        }
    }

    /**
     * 表头写入
     * @param sheet
     * @param heads
     */
    private static void setHead(HSSFWorkbook workbook, HSSFSheet sheet, String[] heads) {
        HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
        hssfCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        HSSFRow row = sheet.getRow(0);
        if (row == null) {
            row = sheet.createRow(0);
        }
        for (int i = 0; i < heads.length; i++) {
            String head = heads[i];
            HSSFCell cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }
            cell.setCellValue(head);
            cell.setCellStyle(hssfCellStyle);
        }
    }
    /**
     * 表头写入
     * @param sheet
     * @param heads
     */
    private static void setHead3(XSSFWorkbook workbook, XSSFSheet sheet, String[] heads) {
        XSSFCellStyle hssfCellStyle = workbook.createCellStyle();
        hssfCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        XSSFRow row = sheet.getRow(0);
        if (row == null) {
            row = sheet.createRow(0);
        }
        for (int i = 0; i < heads.length; i++) {
            String head = heads[i];
            XSSFCell cell = row.getCell(i);
            if (cell == null) {
                cell = row.createCell(i);
            }
            cell.setCellValue(head);
            cell.setCellStyle(hssfCellStyle);
        }
    }


//    public HSSFWorkbook exportExcelByList(HSSFWorkbook workbook, int sheetNum, String title, String[] headers, String[] columNames,
//                                          List<Map<String,Object>> dataset, String pattern){
//
//        // 生成一个表格
//        HSSFSheet sheet = workbook.createSheet();
//        workbook.setSheetName(sheetNum,title);
//        // 设置表格默认列宽度为15个字节
//        sheet.setDefaultColumnWidth((short) 15);
//        // 产生表格标题行
//        HSSFRow row = sheet.createRow(0);
//        HSSFCell cell = row.createCell(0);
//        for (short i = 0; i < headers.length; i++){
//            cell = row.createCell(i);
//
//            HSSFFont font = (HSSFFont) workbook.createFont();
//            font.setFontHeightInPoints((short) 12); // 字体高度
//            font.setFontName("宋体"); // 字体
////            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 宽度加粗
//            HSSFCellStyle style = (HSSFCellStyle) workbook.createCellStyle();
//
////            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
//
//            style.setFont(font);
//            cell.setCellStyle(style);
//
//            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
//            cell.setCellValue(text);
//        }
//        // 遍历集合数据，产生数据行
//        int index = 0;
//        for(int ii = 0;ii<dataset.size();ii++){
//            index++;
//            row = sheet.createRow(index);
//            Map<String,Object> map = dataset.get(ii);
//            for (short i = 0; i < columNames.length; i++){
//                cell = row.createCell(i);
//                try{
//                    Object value = map.get(columNames[i]);
//                    String textValue = null;
//                    // 判断值的类型后进行强制类型转换
//                    if(null != value){
//                        if (value instanceof Date){
//                            Date date = (Date) value;
//                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//                            textValue = sdf.format(date);
//                        }else{
//                            // 其它数据类型都当作字符串简单处理
//                            textValue = value.toString();
//                        }
//                    }
//                    // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
//                    if (textValue != null){
//                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
//                        Matcher matcher = p.matcher(textValue);
//                        if (matcher.matches()){
//                            // 是数字当作double处理
//                            cell.setCellValue(Double.parseDouble(textValue));
//                        }else{
//                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
//                            cell.setCellValue(richString);
//                        }
//                    }else{
//                        textValue="";
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        return workbook;
//    }

    public static Result exportExcel(String excelName, String title,ArrayList<String> headers,ArrayList<String> columNames , List<LinkedHashMap<String,Object>> dataset, String pattern, HttpServletResponse response) throws IOException {


        Long milliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        String fileName = excelName + "-" + milliSecond + ".xlsx";
//        ServletOutputStream outputStream = response.getOutputStream();
//        response.setContentType("application/x-xls;charset=UTF-8");
//        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        boolean flag = false;
        Workbook workbook = null;
        if (fileName.endsWith("xlsx"))
        {
            workbook = new XSSFWorkbook();
        } else if (fileName.endsWith("xls"))
        {
            workbook = new HSSFWorkbook();
        } else
        {
            try
            {
                throw new Exception("invalid file name, should be xls or xlsx");
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        Sheet sheet = workbook.createSheet(title);
        CellStyle styleLeft = workbook.createCellStyle();
        styleLeft.setBorderBottom(BorderStyle.THIN);
        CellStyle styleRight = workbook.createCellStyle();
        styleRight.setBorderBottom(BorderStyle.THIN);
        // 列名
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        for (int i = 0; i < headers.size(); i++)
        {
            sheet.setColumnWidth(i, 5000);
            cell = row.createCell(i);
            XSSFRichTextString text = new XSSFRichTextString(headers.get(i));
            cell.setCellValue(text);

        }

        // 遍历集合数据，产生数据行
        int index = 0;
        for(int ii = 0;ii<dataset.size();ii++){
            index++;
            row = sheet.createRow(index);
            Map<String,Object> map = dataset.get(ii);
            for (short i = 0; i < columNames.size(); i++){
                cell = row.createCell(i);
                try{
                    Object obj = map.get(columNames.get(i));
                if (obj instanceof Date)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                    cell.setCellValue(sdf.format(obj));
                } else if (obj instanceof Integer)
                {
                    cell.setCellValue((Integer) obj);
                } else if (obj instanceof Double)
                {
                    cell.setCellValue((Double) obj);
                } else if (obj instanceof BigDecimal)
                {
                    //保留两位小数
                    Double d=0.00;
                    BigDecimal b = new BigDecimal(String.valueOf(obj));
                    d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    cell.setCellValue( d);
                } else
                {
                    cell.setCellValue((String) obj);
                }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

//        Iterator<Map<String,Object>> it = dataset.iterator();
//        int index = 0;
//        while (it.hasNext())
//        {
//            index++;
//            row = sheet.createRow(index);
//
//            Map map = it.next();
////            logger.info(map.toString());
//            Set<String> mapKey = (Set<String>)map.keySet();
////            logger.info(mapKey.toString());
//            Iterator<String> iterator = mapKey.iterator();
////            logger.info(iterator.toString());
//            int num  = 0;
//            while(iterator.hasNext()){
//                Cell cell = row.createCell(num);
//                num++;
//                String key = iterator.next();
////                logger.info(key);
//                Object obj = map.get(key);
//                if (obj instanceof Date)
//                {
//                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//                    cell.setCellValue(sdf.format(obj));
//                } else if (obj instanceof Integer)
//                {
//                    cell.setCellValue((Integer) obj);
//                } else if (obj instanceof Double)
//                {
//                    cell.setCellValue((Double) obj);
//                } else if (obj instanceof BigDecimal)
//                {
//                    //保留两位小数
//                    Double d=0.00;
//                    BigDecimal b = new BigDecimal(String.valueOf(obj));
//                    d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//                    cell.setCellValue( d);
//                } else
//                {
//                    cell.setCellValue((String) obj);
//                }
//            }
//        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Result result = new Result();
        try
        {
            workbook.write(bos);
            result = sCosUtils.uploadFile(bos, fileName);
            bos.flush();
            bos.close();
            flag = true;
        } catch (FileNotFoundException e)
        {
//            logger.info("文件不存在");
            flag = false;
            e.printStackTrace();
        } catch (IOException e)
        {
//            logger.info("文件写入错误");
            flag = false;
            e.printStackTrace();

        }
//        return flag;
        return result;

    }

}
