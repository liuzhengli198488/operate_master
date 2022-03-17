package com.gys.service.Impl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gys.entity.data.approval.dto.ApprovalDto;
import com.gys.entity.data.approval.dto.ImageDto;
import com.gys.entity.data.approval.dto.UrlDto;
import com.gys.entity.data.approval.vo.ApprovalVo;
import com.gys.entity.data.approval.vo.GaiaSoItemVo;
import com.gys.mapper.GaiaProductBusinessMapper;
import com.gys.mapper.GaiaSoItemMapper;
import com.gys.service.ApprovalService;
import com.gys.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.gys.util.DownloadImage.getImageStream;

@Service
@Slf4j
public class ApprovalServiceImpl implements ApprovalService {
       @Resource
       private GaiaSoItemMapper gaiaSoItemMapper;
       @Resource
       private GaiaProductBusinessMapper gaiaProductBusinessMapper;
       @Value(value ="${cos.url}")
       private String url;
    @Override
    public PageInfo<ApprovalVo> getAllList(ApprovalDto dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<ApprovalVo> approvalVoList = gaiaProductBusinessMapper.getApprovalVoList(dto);
        if(CollectionUtils.isEmpty(approvalVoList)){
            return  new PageInfo<>();
        }
        PageInfo<ApprovalVo> pageInfo = new PageInfo<>(approvalVoList);
        List<ApprovalVo> voList=new ArrayList<>();
        if(!CollectionUtils.isEmpty(approvalVoList)){
            for (ApprovalVo vo:pageInfo.getList()){
                if(StringUtils.isBlank(vo.getProPicturePj())){
                    vo.setProPicturePj(null);
                }else {
                    vo.setProPicturePj(url+vo.getProPicturePj());
                }
                voList.add(vo);
            }
        }
        pageInfo.setList(voList);
        return pageInfo;
    }

    @Override
    public PageInfo<ApprovalVo> getListBydeliveryNumber(ApprovalDto dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<ApprovalVo> list = gaiaProductBusinessMapper.getApprovalVoListByProCodes(dto);
        //空直接返回
        if(CollectionUtils.isEmpty(list)){
            return  new PageInfo<>();
        }
        PageInfo<ApprovalVo> pageInfo = new PageInfo<>(list);
        List<ApprovalVo> approvalVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (ApprovalVo vo : list) {
                if(StringUtils.isNotBlank(vo.getProPicturePj())){
                    vo.setProPicturePj(url + vo.getProPicturePj());
                    vo.setDeliveryNumber(dto.getDeliveryNumber());
                }else {
                    vo.setDeliveryNumber(dto.getDeliveryNumber());
                }
                approvalVos.add(vo);

            }
        }
        pageInfo.setList(approvalVos);
        return pageInfo;
    }

    @Override
    public void downloadZip(HttpServletResponse response, ImageDto dtos) {
        StringBuilder stringBuilder=new StringBuilder();
        String zipName = stringBuilder.append("pj").append(DateUtil.getCurrent()).append(".zip").toString();
        response.reset();
        response.setContentType("application/x-msdownload;charset=utf-8");
        try {
            response.setHeader("content-disposition", "attachment;filename="+ URLEncoder.encode(zipName, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        download(response,dtos);
    }

    public static void download(HttpServletResponse response, ImageDto dto) {
        List<UrlDto> dtos = dto.getDtos();
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(response.getOutputStream());
            for (UrlDto urlDto : dtos){
                String url = urlDto.getUrl();
                HttpURLConnection connection = getImageStream(url);
                InputStream inputStream = connection.getInputStream();
                //String type = connection.getContentType(); 后缀
                String suffix=url.substring(url.lastIndexOf('.'));
                StringBuilder stringBuilder=new StringBuilder();
                String  filename  = stringBuilder.append(dto.getClient()).append(urlDto.getProSite()).append(urlDto.getProSelfCode()).append(urlDto.getProName()).append(suffix).toString();
                out.putNextEntry(new ZipEntry(filename));
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

}
