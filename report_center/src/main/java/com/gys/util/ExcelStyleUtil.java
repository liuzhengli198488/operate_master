package com.gys.util;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.*;

/**
 * @Author ：gyx
 * @Date ：Created in 15:43 2021/12/1
 * @Description：easyExcel的style工具类
 * @Modified By：gyx
 * @Version:
 */
public class ExcelStyleUtil {

    /**
     * 默认excel样式
     * @return
     */
    public static HorizontalCellStyleStrategy getStyle() {

        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景白色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontName("宋体");
        headWriteFont.setFontHeightInPoints((short)10);
        headWriteFont.setBold(false);   //不加粗
        headWriteCellStyle.setWriteFont(headWriteFont);
        //边框
        headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
        headWriteCellStyle.setDataFormat((short) 49);//文本格式

        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 背景白色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        contentWriteCellStyle.setDataFormat((short) 49);    //文本格式
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);//水平居中
        //边框
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontHeightInPoints((short)10);
        contentWriteFont.setFontName("宋体");
        contentWriteFont.setBold(false);    //不加粗
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }
}
