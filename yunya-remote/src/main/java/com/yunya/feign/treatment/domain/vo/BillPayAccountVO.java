package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/7/5 14:17
 * @description: 账单收费入账方式数据模型
 * @since: 1.0.0
 */
@Builder
@Data
@ToString
@ApiModel("账单收费入账方式数据模型")
public class BillPayAccountVO implements Serializable {

    /** 入账方式名称 */
    @ApiModelProperty("入账方式名称")
    private String accountItemName;

    /** 会员卡或预付款卡号 */
    @ApiModelProperty("会员卡或预付款卡号")
    private String cardNumber;
    
    /** 入账方式id */
    @ApiModelProperty("入账方式id")
    private Integer accountItemId;
    
    /** 预付款或会员卡账户归属人 */
    @ApiModelProperty("预付款或会员卡账户归属人")
    private String belonger;

    /** 账户余额 */
    @ApiModelProperty("账户余额")
    private BigDecimal balance;
    
    /** 本金占比 */
    @ApiModelProperty("本金占比")
    private BigDecimal principalRatio;

    /** 可退本金 */
    @ApiModelProperty("可退本金")
    private BigDecimal principal;

    /** 可退赠金 */
    @ApiModelProperty("可退赠金")
    private BigDecimal bonus;

    /** 实收合计 */
    @ApiModelProperty("实收合计")
    private BigDecimal receivedAmount;
}
