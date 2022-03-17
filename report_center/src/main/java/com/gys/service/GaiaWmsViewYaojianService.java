package com.gys.service;

import com.github.pagehelper.PageInfo;
import com.gys.entity.data.DownLoadInData;
import com.gys.entity.data.ViewPictureInData;
import com.gys.entity.data.YaoJianInData;
import com.gys.entity.data.YaoJianOutData;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @desc:
 * @author: ZhangChi
 * @createTime: 2021/12/5 17:55
 */
public interface GaiaWmsViewYaojianService {
    /**
     * 查询
     * @param inData
     * @return
     */
    PageInfo<YaoJianOutData> getList(YaoJianInData inData);

    /**
     * 查看图片
     * @param inData
     * @return
     */
    List<String> viewPicture(ViewPictureInData inData);

    /**
     * 打印
     * @param inDataList
     * @param client
     * @return
     */
    List<String> print(List<ViewPictureInData> inDataList,String client);

    /**
     * 批量下载图片
     */
    void downLoad(HttpServletResponse response, List<DownLoadInData> inDataList, String client);

}
