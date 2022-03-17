//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.report.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(
        name = "GAIA_SD_PRODUCTS_GROUP"
)
public class GaiaSdProductsGroup implements Serializable {
    @Id
    @Column(
            name = "CLIENT"
    )
    private String clientId;
    @Id
    @Column(
            name = "GSPG_ID"
    )
    private String gspgId;
    @Id
    @Column(
            name = "GSPG_PRO_ID"
    )
    private String gspgProId;

}
