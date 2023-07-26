package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2023/07/31
 */
@Getter
@Setter
public class PatientDeductionOrderVO implements Serializable {
    @ApiModelProperty(value = "卡券id")
    private Integer orderId;
    @ApiModelProperty(value = "门诊id")
    private Integer orgId;
    @ApiModelProperty(value = "门诊名称")
    private String orgName;
    @ApiModelProperty(value = "操作人")
    private String operateId;
    @ApiModelProperty(value = "订单号")
    private String orderNumber;
    @ApiModelProperty(value = "入账方式")
    private String accountTypeName;
    @ApiModelProperty(value = "应收金额")
    private BigDecimal receivableAmount;
    @ApiModelProperty(value = "实收金额")
    private BigDecimal receivedAmount;
    @ApiModelProperty(value = "订单状态(0-已下单；1-已收费；2-已退款)")
    private Integer orderStatus;
}
