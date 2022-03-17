package com.gys.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gys.common.exception.BusinessException;
import com.gys.entity.data.DownLoadInData;
import com.gys.entity.data.ViewPictureInData;
import com.gys.entity.data.YaoJianInData;
import com.gys.entity.data.YaoJianOutData;
import com.gys.mapper.GaiaWmsViewYaojianMapper;
import com.gys.mapper.GaiaWmsYaojianMapper;
import com.gys.service.GaiaWmsViewYaojianService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/5 17:55
 */
@Service
@Slf4j
public class GaiaWmsViewYaojianServiceImpl implements GaiaWmsViewYaojianService {
    @Value("${cos.url}")
    private String urls;
    @Resource
    private GaiaWmsViewYaojianMapper yaojianMapper;
    @Resource
    private GaiaWmsYaojianMapper gaiaWmsYaojianMapper;

    //查询
    @Override
    public PageInfo<YaoJianOutData> getList(YaoJianInData inData) {
        if (StringUtils.isBlank(inData.getWhereWmJhdh())
                && StringUtils.isBlank(inData.getBatchNo())
                && StringUtils.isBlank(inData.getProCode())
                && StringUtils.isBlank(inData.getProName())
                && StringUtils.isBlank(inData.getProPYM())
        ){
            throw new BusinessException("提示：请至少输入一个条件！");
        }
        if (inData.getPageNum() == null){
            inData.setPageNum(1);
        }
        if (inData.getPageSize() == null){
            inData.setPageSize(50);
        }
        PageHelper.startPage(inData.getPageNum(),inData.getPageSize());
        List<YaoJianOutData> outData = new ArrayList<>();
        //拣货单号是否为空
        if (StringUtils.isBlank(inData.getWhereWmJhdh())){
            outData = yaojianMapper.getListByProductInfo(inData);
        }else {
            outData = yaojianMapper.getListByBillNo(inData);
        }
        PageInfo<YaoJianOutData> info = new PageInfo<>(outData);
        return info;
    }

    //查看图片
    @Override
    public List<String> viewPicture(ViewPictureInData inData) {
        if (StringUtils.isBlank(inData.getBatchNo())){
            throw new BusinessException("提示：批号为空！");
        }
        if (StringUtils.isBlank(inData.getProCode())){
            throw new BusinessException("提示：商品编码为空！");
        }
        if (StringUtils.isBlank(inData.getSupplierCoder())){
            throw new BusinessException("提示：供应商编码为空！");
        }
        String url = gaiaWmsYaojianMapper.getPictureUrl(inData.getClient(),inData.getSupplierCoder(),inData.getProCode(),inData.getBatchNo());
        if (StringUtils.isNotBlank(url)){
            List<String> result = Arrays.asList(url.split(","));
            List<String> list = new ArrayList<>();
            for (String str : result){
                //补全图片路径
                String u = urls + str;
                list.add(u);
            }
            return list;
        }
        return null;
    }

    //打印
    @Override
    public List<String> print(List<ViewPictureInData> inDataList,String client) {
        List<String> list = new ArrayList<>();
        for (ViewPictureInData inData : inDataList){
            inData.setClient(client);
            String url = gaiaWmsYaojianMapper.getPictureUrl(inData.getClient(),inData.getSupplierCoder(),inData.getProCode(),inData.getBatchNo());
            if (StringUtils.isNotBlank(url)){
                List<String> result = Arrays.asList(url.split(","));
                for (String str : result){
                    //补全图片路径
                    String u = urls + str;
                    list.add(u);
                }
            }
        }
        return list;
    }

    @Override
    public void downLoad(HttpServletResponse response,List<DownLoadInData> inDataList, String client) {
        if (CollectionUtils.isEmpty(inDataList)){
            throw new BusinessException("提示：请选择图片！");
        }
        String date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map<String,String> map = new HashMap();
        //新建文件名称
        String filePath = "rep_"+date.replaceAll("-","").replaceAll(" ","").replaceAll(":","") + ".zip";
        for (DownLoadInData inData  : inDataList){
            inData.setClient(client);
            String url = gaiaWmsYaojianMapper.getPictureUrl(inData.getClient(),inData.getSupplierCoder(),inData.getProCode(),inData.getBatchNo());
            if (StringUtils.isNotBlank(url)){
                List<String> result = Arrays.asList(url.split(","));
                for (int i = 0; i < result.size(); i++){
                    //补全图片路径
                    String u = urls + result.get(i);
                    String pictureName = "";
                    if (result.size() > 1){
                        pictureName =  inData.getProName() + "_" + inData.getBatchNo() + "(" + (i+1) +")" + u.substring(u.lastIndexOf('.')) ;
                    }else {
                       pictureName =  inData.getProName() + "_" + inData.getBatchNo() + u.substring(u.lastIndexOf('.'));
                    }
                    map.put(u,pictureName);
                }
            }
        }
        try {
            response.reset();
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("content-disposition", "attachment;filename="+URLEncoder.encode(filePath, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        downloadZip(response,map);
    }

    //下载Zip
    public static void downloadZip(HttpServletResponse response, Map<String,String> map){
        Set<String> urlList = map.keySet();
        ZipOutputStream out = null;

        try {
            out = new ZipOutputStream(response.getOutputStream());
            for (String url : urlList){
                HttpURLConnection connection = getImageStream(url);
                InputStream inputStream = connection.getInputStream();
                out.putNextEntry(new ZipEntry(map.get(url)));
                if (null == inputStream){
                    continue;
                }
                int temp;
                while ((temp = inputStream.read()) != -1) {
                   out.write(temp);
                }
                inputStream.close();
            }
            out.closeEntry();
            log.info("压缩完成");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(out != null)
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static HttpURLConnection getImageStream(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return connection;
            }
        } catch (IOException e) {
            System.out.println("获取网络图片出现异常，图片路径为：" + url);
            e.printStackTrace();
        }
        return null;
    }


}
