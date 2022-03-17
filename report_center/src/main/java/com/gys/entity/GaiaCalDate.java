package com.gys.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @desc:
 * @author: Ryan
 * @createTime: 2021/12/29 14:56
 */
@Data
@Table(name = "GAIA_CAL_DT")
public class GaiaCalDate {

    @Id
    @Column( name = "GCD_DATE" )
    private String gcdDate;

    @Column( name = "GCD_YEAR" )
    private String gcdYear;

    @Column( name = "GCD_QUARTER" )
    private String gcdQuarter;

    @Column( name = "GCD_MONTH" )
    private String gcdMonth;

    @Column( name = "GCD_WEEK" )
    private String gcdWeek;

}

