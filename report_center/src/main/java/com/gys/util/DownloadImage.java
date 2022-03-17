package com.gys.util;

import com.gys.entity.data.approval.dto.UrlDto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Auther: tzh
 * @Date: 2021/12/6 15:33
 * @Description: DownloadImage
 * @Version 1.0.0
 */
public class DownloadImage {
    public static void dowloaImageGraceFully(String imageUrl, String formatName, String storePath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(imageUrl);
            URLConnection con = url.openConnection();
            String type = con.getContentType();
            if (type.matches("image/.+") || type.matches("png/.+")||type.matches("gif/.+")) {
                String filename = formatName + "." + type.substring(type.lastIndexOf("/") + 1);
                File storeDir = new File(storePath);
                if (!storeDir.exists()) {
                    storeDir.mkdirs();
                }
                is = con.getInputStream();
                fos = new FileOutputStream(new File(storeDir, filename));
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = is.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                }

            } else {
                System.out.println("不是图片类型，无法下载！" + imageUrl);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
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


    public static void main(String[] args) {
        String a="https://purchase-dev-1301851641.cos.ap-shanghai.myqcloud.com/product/1638771542253.png";
        dowloaImageGraceFully(a, UUID.randomUUID().toString(), "/home/xiaoxin/图片/图片下载测试2");
        List<UrlDto> dtos =new ArrayList<>();
        UrlDto urlDto=new UrlDto();
        urlDto.setProSelfCode("111111");
        urlDto.setUrl("https://purchase-dev-1301851641.cos.ap-shanghai.myqcloud.com/product/1638771542253.png");
        dtos.add(urlDto);
       // generateZip(HttpServletResponse  ew ,dtos);
    }
}
