package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 返回预付款基本信息模型
 *
 * @author: WY
 * @date 2020/7/31 9:46
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回预付款基本信息模型")
public class PatientPrepaymentsInfoVo implements Serializable {
    /**
     * 主键id
     */
    @ApiModelProperty("预付款ID")
    private Integer id;

    /**
     * 诊所id
     */
    @ApiModelProperty("组织ID")
    private Integer orgId;

    /**
     * 患者id
     */
    @ApiModelProperty("患者ID")
    private Integer patientId;

    /**
     * 患者名称
     */
    @ApiModelProperty("患者姓名")
    private String name;

    /**
     * 预付款账号
     */
    @ApiModelProperty("预付款号")
    private String prepaymentNumber;

    /**
     * 预付款本金+预付款赠金
     */
    @ApiModelProperty("预付款本金+预付款赠金")
    private BigDecimal prepaymentMoneySum;

    /**
     * 赠金
     */
    @ApiModelProperty("赠金")
    private BigDecimal prepaymentBonus;

    /**
     * 本金
     */
    @ApiModelProperty("本金")
    private BigDecimal prepaymentPrincipal;

    /**
     * 开户日期
     */
    @ApiModelProperty("开户日期")
    private Date crtTime;

    /** 预付款类型：1-预付款，2-正畸预付款，3-美白预付款 */
    @ApiModelProperty("预付款类型：1-预付款，2-正畸预付款，3-美白预付款")
    private Integer type;

    /**
     * 备注（账户余额 or 未开通账户）
     */
    @ApiModelProperty("备注（账户余额 or 未开通账户）")
    private String remark;
}
