package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/8/3 15:38
 * @description: 患者累计信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者累计信息数据模型")
public class PatientCumulativeInfoVO implements Serializable {

    /** 个人累计消费总额 */
    @ApiModelProperty("个人累计消费总额")
    private BigDecimal cumulativeConsumption = BigDecimal.ZERO;

    /** 个人赠金账户余额 */
    @ApiModelProperty("个人赠金账户余额")
    private BigDecimal personBounsCumulative = BigDecimal.ZERO;

    /** 卡内累计消费总额 */
    @ApiModelProperty("卡内累计消费总额")
    private BigDecimal cardCumulativeConsumption = BigDecimal.ZERO;

    /** 会员卡卡内本金余额 */
    @ApiModelProperty("会员卡卡内本金余额")
    private BigDecimal memberPrincipal = BigDecimal.ZERO;

    /** 会员卡卡内赠金余额 */
    @ApiModelProperty("会员卡卡内赠金余额")
    private BigDecimal memberBouns = BigDecimal.ZERO;
}
