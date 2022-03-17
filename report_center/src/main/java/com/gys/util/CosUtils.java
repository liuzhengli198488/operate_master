package com.gys.util;

import com.gys.common.data.JsonResult;
import com.gys.common.response.Result;
import com.gys.common.response.ResultEnum;
import com.gys.common.response.ResultUtil;
import com.gys.entity.Tencent;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author: libb
 * @date: 2020.04.09
 */
@Slf4j
@Component
public class CosUtils {

    @Resource
    Tencent tencent;

    public Result uploadWorkbook(Workbook workbook, String fileName) {
        try {
            // 1 初始化秘钥信息
            COSCredentials cred = new BasicCOSCredentials(tencent.getCosSecretId(), tencent.getCosSecretKey());
            // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
            ClientConfig clientConfig = new ClientConfig(new Region(tencent.getCosRegion()));
            // 3 生成cos客户端
            COSClient cosClient = new COSClient(cred, clientConfig);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            int length = barray.length;

            // 获取文件流
            InputStream byteArrayInputStream = new ByteArrayInputStream(barray);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(length);

            // 保存目录
            String key = tencent.getImportPaht() + fileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(tencent.getCosBucket(), key, byteArrayInputStream, objectMetadata);
            // 设置 Content type, 默认是 application/octet-stream
            putObjectRequest.setMetadata(objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            String eTag = putObjectResult.getETag();
            System.out.println(eTag);
            // 关闭客户端
            cosClient.shutdown();
            return ResultUtil.success();
        } catch (IOException e) {
            return ResultUtil.error(ResultEnum.E0123);
        }
    }

    public Result uploadFile(ByteArrayOutputStream bos, String fileName) {
        try {
            // 1 初始化秘钥信息
            COSCredentials cred = new BasicCOSCredentials(tencent.getCosSecretId(), tencent.getCosSecretKey());
            // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
            ClientConfig clientConfig = new ClientConfig(new Region(tencent.getCosRegion()));
            // 3 生成cos客户端
            COSClient cosClient = new COSClient(cred, clientConfig);

            byte[] barray = bos.toByteArray();
            int length = barray.length;

            // 获取文件流
            InputStream byteArrayInputStream = new ByteArrayInputStream(barray);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(length);

            // 保存目录
            String key = tencent.getExportPath() + fileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(tencent.getCosBucket(), key, byteArrayInputStream, objectMetadata);
            // 设置 Content type, 默认是 application/octet-stream
            putObjectRequest.setMetadata(objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            String eTag = putObjectResult.getETag();
//            System.out.println("eTag = "+eTag);
            // 关闭客户端
            cosClient.shutdown();
            return ResultUtil.success(this.urlAuth(key));
        } catch (Exception e) {
            return ResultUtil.error(ResultEnum.E0155);
        }
    }

    public Result uploadInvoiceFile(ByteArrayOutputStream bos, String fileName) {
        try {
            // 1 初始化秘钥信息
            COSCredentials cred = new BasicCOSCredentials(tencent.getCosSecretId(), tencent.getCosSecretKey());
            // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
            ClientConfig clientConfig = new ClientConfig(new Region(tencent.getCosRegion()));
            // 3 生成cos客户端
            COSClient cosClient = new COSClient(cred, clientConfig);

            byte[] barray = bos.toByteArray();
            int length = barray.length;

            // 获取文件流
            InputStream byteArrayInputStream = new ByteArrayInputStream(barray);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(length);

            // 保存目录
            String key = tencent.getInvoicePath() + fileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(tencent.getCosBucket(), key, byteArrayInputStream, objectMetadata);
            // 设置 Content type, 默认是 application/octet-stream
            putObjectRequest.setMetadata(objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            String eTag = putObjectResult.getETag();
            System.out.println(eTag);
            // 关闭客户端
            cosClient.shutdown();
            return ResultUtil.success(this.urlAuth(key));
        } catch (Exception e) {
            return ResultUtil.error(ResultEnum.E0155);
        }
    }

    /**
     * 文件签名
     *
     * @param path
     * @return
     */
    public String urlAuth(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        String secretId = tencent.getCosSecretId();
        String secretKey = tencent.getCosSecretKey();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region region = new Region(tencent.getCosRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        // 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = tencent.getCosBucket();
        String key = path.startsWith("/") ? path : "/" + path;
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
        // 设置签名过期时间(可选), 若未进行设置, 则默认使用 ClientConfig 中的签名过期时间(1小时)
        // 这里设置签名在半个小时后过期
        Date expirationDate = new Date(System.currentTimeMillis() + 60L * 60L * 1000L * 24L);
        req.setExpiration(expirationDate);
        URL url = cosClient.generatePresignedUrl(req);
        log.info(url.toString());
        if (url.toString().toLowerCase().startsWith("http:")) {
            return url.toString().replace("http:", "https:");
        }
        return url.toString();
    }

    /**
     * 上传签名
     *
     * @param type
     * @return
     */
    public String uploadSign(String type) {
        String secretId = tencent.getCosSecretId();
        String secretKey = tencent.getCosSecretKey();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        COSSigner signer = new COSSigner();
        //设置过期时间为1个小时
        Date expiredTime = new Date(System.currentTimeMillis() + 3600L * 1000L);
        // 要签名的 key, 生成的签名只能用于对应此 key 的上传
        String key = "/" + type;
        String sign = signer.buildAuthorizationStr(HttpMethodName.PUT, key, cred, expiredTime);
        log.info(sign);
        return sign;
    }


    public JsonResult uploadFileNew(ByteArrayOutputStream bos, String fileName) {
        try {
            // 1 初始化秘钥信息
            COSCredentials cred = new BasicCOSCredentials(tencent.getCosSecretId(), tencent.getCosSecretKey());
            // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
            ClientConfig clientConfig = new ClientConfig(new Region(tencent.getCosRegion()));
            // 3 生成cos客户端
            COSClient cosClient = new COSClient(cred, clientConfig);

            byte[] barray = bos.toByteArray();
            int length = barray.length;

            // 获取文件流
            InputStream byteArrayInputStream = new ByteArrayInputStream(barray);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(length);

            // 保存目录
            String key = tencent.getExportPath() + fileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(tencent.getCosBucket(), key, byteArrayInputStream, objectMetadata);
            // 设置 Content type, 默认是 application/octet-stream
            putObjectRequest.setMetadata(objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            String eTag = putObjectResult.getETag();
            System.out.println(eTag);
            // 关闭客户端
            cosClient.shutdown();
            return JsonResult.success(this.urlAuth(key), "success");
        } catch (Exception e) {
            return JsonResult.error("导出失败！");
        }
    }

}
