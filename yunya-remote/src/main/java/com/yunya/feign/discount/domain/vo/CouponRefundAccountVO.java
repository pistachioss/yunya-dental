package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@ApiModel("划扣收费入账方式数据模型")
public class CouponRefundAccountVO implements Serializable {

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

    /** 可退本金 */
    @ApiModelProperty("可退本金")
    private BigDecimal principal;

    /** 可退赠金 */
    @ApiModelProperty("可退赠金")
    private BigDecimal bonus;

//    /** 实收合计 */
//    @ApiModelProperty("实收合计")
//    private BigDecimal receivedAmount;
}
