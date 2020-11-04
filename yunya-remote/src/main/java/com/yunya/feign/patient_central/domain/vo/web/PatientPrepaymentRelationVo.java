package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br>返回预付款绑定关系信息模型
 *
 * @author: WY
 * @date 2020/8/27 12:50
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回预付款绑定关系信息模型")
public class PatientPrepaymentRelationVo implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 诊所ID
     */
    private Integer orgId;

    /**
     * 主卡人ID
     */
    private Integer masterCardId;

    /**
     * 副卡人ID
     */
    private Integer secondaryCardId;

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
     * 副卡人名称
     */
    private String SecondaryCardName;
}



