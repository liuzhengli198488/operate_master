package com.gys.mapper;

import com.gys.common.data.MaterialDocumentInData;
import com.gys.common.data.MaterialDocumentOutData;
import org.apache.ibatis.annotations.Mapper;
import org.omg.CORBA.Object;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface MaterialDocumentMapper {

    List<MaterialDocumentOutData> noLogList(MaterialDocumentInData inData);

    List<MaterialDocumentOutData> listSuccessDocument(MaterialDocumentInData inData);

    List<MaterialDocumentOutData> listErrorForCDOrCX(MaterialDocumentInData inData);

    List<MaterialDocumentOutData> listErrorForPDOrPXOrXD(MaterialDocumentInData inData);

    List<MaterialDocumentOutData> listErrorForTD(MaterialDocumentInData inData);

    List<MaterialDocumentOutData> listErrorForGD(MaterialDocumentInData inData);

    List<MaterialDocumentOutData> listErrorForLSOrLX(MaterialDocumentInData inData);

    List<HashMap<String, Object>> listClient();

    List<HashMap<String, Object>> listClientDCAndStorage(MaterialDocumentInData inData);
}
