package com.yunya.feign.discount.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("划扣余额结存表")
public class DeductionBalanceInfoVO {

    /** 预付款类型 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String type;

    /** 会员卡号 */
    @ApiModelProperty(value = "卡号")
    private String cardNumber;

    /** 患者姓名 */
    @ApiModelProperty(value = "购买人")
    private String buyer;

    /** 手机号 */
    @ApiModelProperty(value = "激活人")
    private String activator;

    /** 期初本金余额 */
    @ApiModelProperty(value = "期初划扣余额")
    private BigDecimal beginDeduction;

    /** 期初赠金余额 */
    @ApiModelProperty(value = "本期划扣金额")
    private BigDecimal thisDeduction;

    /** 本期充值本金 */
    @ApiModelProperty(value = "本期未划扣项目退费金额")
    private BigDecimal thisDeductionRefund;

    /** 本期充值赠金 */
    @ApiModelProperty(value = "期末划扣余额")
    private BigDecimal endDeduction;

}