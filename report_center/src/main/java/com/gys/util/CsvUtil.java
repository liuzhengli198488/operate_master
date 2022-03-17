package com.gys.util;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author huxinxin
 * @Date 2021/4/28 13:29
 * @Version 1.0.0
 **/
public class CsvUtil {
    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);
    /** CSV文件列分隔符 */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /** CSV文件列分隔符 */
    private static final String CSV_RN = "\r\n";

    /**
     *
     * @param dataList 集合数据
     * @param colNames 表头部数据
     * @param mapKey 查找的对应数据
     * @param1response 返回结果
     */
    public static boolean doExport1(List<Map<String, Object>> dataList, ArrayList<String> colNames, ArrayList<String> mapKey, OutputStream os) {
        try {
            StringBuffer buf = new StringBuffer();
            // 完成数据 csv 文件的封装 // 输出列头
            for ( int i = 0; i < colNames. size(); i++) {
                buf.append(colNames.get(i)).append( CSV_COLUMN_SEPARATOR);
            }
            buf.append( CSV_RN);
            if ( null != dataList) { // 输出数据
                for ( int i = 0; i < dataList.size(); i++) {
                    for ( int j = 0; j < mapKey. size(); j++) {
                        buf.append(dataList.get(i).get(mapKey.get(i))).append( CSV_COLUMN_SEPARATOR);
                    }
                    buf.append( CSV_RN);
                }
            } // 写出响应
            os.write(buf.toString().getBytes( "GBK"));
            os.flush();
            os.close();
            return true;
        } catch (Exception e) {
            logger.info( "doExport 错误 :"+ e);
        }
        return false;
    }


    public static void responseSetProperties(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        // 设置文件后缀
         SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd");
         String fn = fileName + sdf.format( new Date()).toString() + ".csv";
         // 读取字符编码 \
        String utf = "UTF-8";
         // 设置响应
        response.setContentType( "application/ms-txt.numberformat:@");
        response.setCharacterEncoding(utf); response.setHeader( "Pragma", "public");
        response.setHeader( "Cache-Control", "max-age=30");
        response.setHeader( "Content-Disposition", "attachment; filename=" + URLEncoder. encode(fn, utf));
    }
}
