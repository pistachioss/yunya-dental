package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 返回预付款充值记录信息模型
 *
 * @author: WY
 * @date 2020/8/22 15:14
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回预付款充值记录信息模型")
public class PrepaidRechargeRecordVo implements Serializable {
    /**
     * 会员充值记录id
     */
    @ApiModelProperty("会员充值记录id")
    private Integer id;

    /**
     * 操作时间
     */
    @Excel(name = "操作时间")
    @ApiModelProperty("操作时间")
    private String operatingTime;

    /**
     * 充值金额
     */
    @Excel(name = "充值金额")
    @ApiModelProperty("充值金额")
    private BigDecimal rechargePrincipal;

    /**
     * 赠送金额
     */
    @Excel(name = "赠送金额")
    @ApiModelProperty("赠送金额")
    private BigDecimal rechargeBonus;

    /**
     * 入账方式id
     */
    @ApiModelProperty("入账方式id")
    private Integer paymentId;

    /**
     * 入账方式
     */
    @Excel(name = "入账方式")
    @ApiModelProperty("入账方式")
    private String paymentName;

    /**
     * 充值卡号
     */
    @Excel(name = "充值卡号")
    @ApiModelProperty("充值卡号")
    private String rechargeCardNumber;

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

    /**
     * 充值类型
     */
    @Excel(name = "充值类型")
    @ApiModelProperty("充值类型")
    private Integer rechargeType;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remarks;

}
