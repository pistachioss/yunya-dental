package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2023/6/25 10:41
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单费用数据模型")
public class BillChargeVO implements Serializable {
    /** 支付记录ID */
    @ApiModelProperty("支付记录ID")
    private Integer billPayId;
    /** 收费组织ID */
    @ApiModelProperty("收费组织ID")
    private Integer payeeOrgId;
    /** 收费时间 */
    @ApiModelProperty("收费时间")
    private Date payeeDate;
    /** 订单记录ID */
    @ApiModelProperty("订单记录ID")
    private Integer billId;
    /** 实收分摊总额（含免单） */
    @ApiModelProperty("实收分摊总额（含免单）")
    private BigDecimal receivedAmount;
    /** 划扣卡核销工作量 */
    @ApiModelProperty("划扣卡核销工作量")
    private BigDecimal swipeWorkload;
    /** 免单分摊总额 */
    @ApiModelProperty("免单分摊总额")
    private BigDecimal freeAmount;
    /** 组织ID */
    @ApiModelProperty("账单组织ID")
    private Integer billOrgId;
    /** 账单时间 */
    @ApiModelProperty("账单时间")
    private Date billDate;
}
