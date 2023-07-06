package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/7/5 15:11
 * @description: 患者储蓄账户（会员卡or预付款）详情数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者储蓄账户（会员卡or预付款）详情数据模型")
public class PatientDepositAccountVO implements Serializable {
    
    /** 账户编号 */
    @ApiModelProperty("账户编号")
    private String cardNumber;
    
    /** 账户类型: 0-会员卡，1-预付款 */
    @ApiModelProperty("账户类型: 0-会员卡，1-预付款")
    private Integer type;

    /** 归属患者id */
    @ApiModelProperty("归属患者id")
    private Integer patientId;
    
    /** 归属患者姓名 */
    @ApiModelProperty("归属患者姓名")
    private String patientName;
    
    /** 账户余额 */
    @ApiModelProperty("账户余额")
    private BigDecimal balance;
    
    /** 消费本金 */
    @ApiModelProperty("消费本金")
    private BigDecimal principal;
    
    /** 消费赠金 */
    @ApiModelProperty("消费赠金")
    private BigDecimal bonus;
}
