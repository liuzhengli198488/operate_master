package com.gys.service;

import com.gys.entity.priceProposal.condition.*;
import com.gys.entity.priceProposal.dto.RetailPriceInfoSaveRequestDto;
import com.gys.entity.priceProposal.vo.AllStosVO;
import com.gys.entity.priceProposal.vo.PriceProposalDetailListVO;
import com.gys.entity.priceProposal.vo.PriceProposalListVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author Jinwencheng
 * @Version 1.0
 * @Description 商品价格建议业务接口
 * @CreateTime 2022-01-11 14:45:00
 */
public interface ProductPriceProposalService {

    void makeData();

    void clearData();

    List<PriceProposalListVO> selectPriceProposalList(SelectPriceProposalListCondition condition);

    List<PriceProposalDetailListVO> selectPriceProposalDetailList(SelectPriceProposalDetailListCondition condition);

    void export(HttpServletResponse response, SelectPriceProposalDetailListCondition condition) throws IOException;

    void savePriceProposaInfo(List<SavePriceProposalCondition> conditions, String clientId);

    List<AllStosVO> selectAllStosByClientId(String clientId);

    List<AllStosVO> stoQuickSearch(StoQuickSearchCondition condition);

    RetailPriceInfoSaveRequestDto saveRetailPriceInfo(SaveRetailPriceInfoCondition condition, String clientId);

}
