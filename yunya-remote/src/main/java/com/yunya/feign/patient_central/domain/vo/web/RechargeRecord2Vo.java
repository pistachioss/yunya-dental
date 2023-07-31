package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 返回会员卡充值记录模型
 *
 * @author: WY
 * @date 2020/8/15 17:00
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回赠金转账记录模型")
public class RechargeRecord2Vo implements Serializable {

    /**
     * 会员充值记录id
     */
    @ApiModelProperty("会员充值记录id")
    private Integer id;

    /**
     * 转出患者
     */
    @Excel(name = "转出患者")
    @ApiModelProperty("转出患者")
    private String patientName1;

    /**
     * 卡主
     */
    @Excel(name = "卡主")
    @ApiModelProperty("卡主")
    private String patientName2;

    /**
     * 操作时间
     */
    @Excel(name = "转账时间")
    @ApiModelProperty("转账时间")
    private String operatingTime;

    /**
     * 充值金额
     */
    @ApiModelProperty("充值金额")
    private BigDecimal rechargePrincipal;

    /**
     * 赠送金额
     */
    @Excel(name = "转账赠金")
    @ApiModelProperty("转账赠金")
    private BigDecimal rechargeBonus;

    /**
     * 转入会员卡卡号
     */
    @Excel(name = "转入会员卡卡号")
    @ApiModelProperty("转入会员卡卡号")
    private String remarks;

    /**
     * 入账方式Id
     */
//    @Excel(name = "入账方式Id")
    @ApiModelProperty("入账方式Id")
    private Integer paymentId;

    /**
     * 入账方式
     */
    @ApiModelProperty("入账方式")
    private String payment;

    /**
     * 门诊id
     */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /**
     * 诊所
     */
    @Excel(name = "诊所")
    @ApiModelProperty("诊所")
    private String orgName;

    /**
     * 操作人id
     */
    @ApiModelProperty("操作人id")
    private Integer operatorId;

    /**
     * 操作人员
     */
    @Excel(name = "操作人员")
    @ApiModelProperty("操作人员")
    private String operatorName;


}
