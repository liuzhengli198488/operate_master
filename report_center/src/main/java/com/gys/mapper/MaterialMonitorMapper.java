package com.gys.mapper;

import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import java.util.List;
import java.util.Map;

/**
 * @Author jiht
 * @Description
 * @Date 2022/2/21 13:58
 * @Param
 * @Return
 **/
@Mapper
public interface MaterialMonitorMapper {
    /**
     * @Author jiht
     * @Description 数据删除
     * @Date 2022/2/21 17:03
     * @Param []
     * @Return void
     **/
    void clearMonitorMaterialDoc();

    void clearMonitorMaterialLog();

    void clearMonitorProductXnp();

    void clearMonitorServiceData();

    void clearMonitorMaterialDiff(@Param("batchDate") String batchDate);

    /**
     * @Author jiht
     * @Description 获取物料凭证数据
     * @Date 2022/2/21 14:04
     * @Param [client, startDate, endDate]
     * @Return void
     **/
    void insertMonitorMaterialDoc(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * @Author jiht
     * @Description 获取物料凭证LOG表数据
     * @Date 2022/2/21 14:04
     * @Param [client, startDate, endDate]
     * @Return void
     **/
    void insertMonitorMaterialLog(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * @Author jiht
     * @Description 获取虚拟品商品数据
     * @Date 2022/2/21 14:04
     * @Param [client, startDate, endDate]
     * @Return void
     **/
    void insertMonitorProductXnp(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * @Author jiht
     * @Description 获取虚拟品商品数据
     * @Date 2022/2/21 14:04
     * @Param [client, startDate, endDate]
     * @Return void
     **/
    void insertMonitorMaterialDiff(@Param("client") String client, @Param("batchDate") String batchDate);

    /**
     * @Author jiht
     * @Description 获取业务数据数据
     * @Date 2022/2/21 14:04
     * @Param [client, startDate, endDate]
     * @Return void
     **/
    // GD-退供应商
    void insertMonitorServiceData_GD(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // XD-销售 PD-配送 ND-互调配送
    void insertMonitorServiceData_XDPDND(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // CD-仓库验收
    void insertMonitorServiceData_CDdc(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // CD-门店验收
    void insertMonitorServiceData_CDsd(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // ED-销退 TD-退库 MD-互调退库
    void insertMonitorServiceData_EDTDMD(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // ZD-仓库自用
    void insertMonitorServiceData_ZD(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // ZD-门店自用
    void insertMonitorServiceData_ZDsd(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // BD-报损出库
    void insertMonitorServiceData_BDdc(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // BD-门店报损
    void insertMonitorServiceData_BDsd(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // LS-零售
    void insertMonitorServiceData_LS(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // SY-仓库损溢
    void insertMonitorServiceData_SYdc(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // SY-门店损溢
    void insertMonitorServiceData_SYsd(@Param("client") String client, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * @Author jiht
     * @Description 统计差异
     * @Date 2022/2/22 15:05
     * @Param [batchDate]
     * @Return java.util.List<java.util.Map<java.lang.String,java.lang.String>>
     **/
    List<Map<String,String>> queryMailInfo(@Param("batchDate") String batchDate);

    /**
     * @Author jiht
     * @Description 差异数据量计数
     * @Date 2022/2/22 15:05
     * @Param [batchDate]
     * @Return int
     **/
    int countMaterailDiff(@Param("batchDate") String batchDate);

    /**
     * @Author jiht
     * @Description 查询所有加盟商
     * @Date 2022/2/13 23:14
     * @Param []
     * @Return java.util.List<java.lang.String>
     **/
    List<String> queryAllClients();

    /**
     * @Author jiht
     * @Description 查询收件人
     * @Date 2022/2/13 23:14
     * @Param []
     * @Return java.util.List<java.lang.String>
     **/
    List<String> queryMails();
}
