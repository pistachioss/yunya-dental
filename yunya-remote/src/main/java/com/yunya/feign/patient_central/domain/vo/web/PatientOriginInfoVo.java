package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 返回患者来源信息模型
 *
 * @author: WY
 * @date 2020/8/5 14:49
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回患者来源信息模型")
public class PatientOriginInfoVo implements Serializable {
    /**
     * 患者来源ID
     */
    private Integer id;

    /**
     * 患者来源父ID（顶级为0）
     */
    private Integer parentId;

    /**
     * 患者来源名称
     */
    private String name;

    /**
     * 患者来源类型 1.员工 2.老患者 3.活动 4.合作商
     */
    private Integer originType;

    /**
     * 是否允许操作（编辑、删除）
     */
    private Boolean allowOperate;

    /**
     * 是否有时间限制（0-否；1-是））
     */
    private Integer timeLimit;

    /**
     * 限制开始时间
     */
    private Date limitStartDate;

    /**
     * 限制介绍时间
     */
    private Date limitEndDate;

    /**
     * 是否启用
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
    private Integer updId;

    /**
     * 更新人姓名
     */
    private String updName;

    /**
     * 更新时间
     */
    private Date updTime;

    /**
     * 二维码url
     */
    private String codeUrl;
}
