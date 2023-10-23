package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 返回会员信息模型
 *
 * @author: WY
 * @date 2020/8/28 20:48
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("返回会员信息模型")
public class PatientMemberInfoVo implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 诊所id
     */
    @ApiModelProperty("诊所id")
    private Integer orgId;

    /**
     * 患者id
     */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /**
     * 患者id
     */
    @ApiModelProperty("患者")
    private String name;

    /** 会员类型id */
    @ApiModelProperty("会员类型id")
    private Integer memberTypeId;

    /**
     * 会员账号
     */
    @ApiModelProperty("会员账号")
    private String cardNumber;

    /**
     * 本金+赠金
     */
    @ApiModelProperty("本金+赠金")
    private BigDecimal memberMoneySum;

    /**
     * 赠金
     */
    @ApiModelProperty("赠金")
    private BigDecimal principalAmount;

    /**
     * 本金
     */
    @ApiModelProperty("本金")
    private BigDecimal bonusAmount;

    /** 次一级会员折扣 */
    @ApiModelProperty("次一级会员折扣")
    private BigDecimal secondaryMemberDiscount;

    /** 是否激活 */
    @ApiModelProperty("是否激活")
    private Boolean inservice;

    /** 积分 */
    @ApiModelProperty("积分")
    private Integer point;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    private Date crtTime;
}
