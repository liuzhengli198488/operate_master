package com.gys.service;

import com.gys.common.data.MaterialDocumentInData;
import com.gys.common.data.PageInfo;
import org.omg.CORBA.Object;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface MaterialDocumentService {

    PageInfo noLogList(MaterialDocumentInData inData);

    List<HashMap<String, Object>> listClient(MaterialDocumentInData inData);

    List<HashMap<String, Object>> listClientDCAndStorage(MaterialDocumentInData inData);
}
