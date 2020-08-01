package com.yunya.models.appointment;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * appoint_item
 * @author 
 */
@Data
public class AppointItem implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 预约项目分类ID
     */
    private Integer appointItemId;

    /**
     * 预约项目详情名称
     */
    private String name;

    /**
     * 预约时长
     */
    private Integer duration;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    private Integer crtId;

    /**
     * 创建人姓名
     */
    private String crtName;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 更新人ID
     */
    private Integer uptId;

    /**
     * 更新人姓名
     */
    private String updName;

    /**
     * 更新时间
     */
    private Date updTime;

    private static final long serialVersionUID = 1L;
}