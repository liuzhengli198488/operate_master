package com.gys.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class EmployeeHealthRecordDelDto implements Serializable {

    private List<Long> ids;

    /**
     * 加盟商
     */
    private String client;
    /**
     * 删除人编号
     */
    private String updateUser;
    private static final long serialVersionUID = 1L;
}