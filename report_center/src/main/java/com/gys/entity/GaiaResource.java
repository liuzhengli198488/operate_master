package com.gys.entity;

import lombok.Data;

import javax.persistence.*;

@Table(
        name = "GAIA_RESOURCE"
)
@Data
public class GaiaResource {
    @Id
    @Column(
            name = "ID"
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private String id;
    @Column(
            name = "NAME"
    )
    private String name;
    @Column(
            name = "TITLE"
    )
    private String title;
    @Column(
            name = "PATH"
    )
    private String path;
    @Column(
            name = "PAGE_PATH"
    )
    private String pagePath;
    @Column(
            name = "REQUEST_PATH"
    )
    private String requestPath;
    @Column(
            name = "ICON"
    )
    private String icon;
    @Column(
            name = "TYPE"
    )
    private String type;
    @Column(
            name = "BUTTON_TYPE"
    )
    private String buttonType;
    @Column(
            name = "PARENT_ID"
    )
    private String parentId;
    @Column(
            name = "STATUS"
    )
    private String status;
    @Column(
            name = "SEQ"
    )
    private Integer seq;
    @Column(
            name = "MODULE"
    )
    private String module;
}
