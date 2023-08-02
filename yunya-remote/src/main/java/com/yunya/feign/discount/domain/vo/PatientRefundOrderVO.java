package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xiangyang
 * @date 2023/07/31
 */
@Data
@ApiModel(value = "划扣退费信息模型")
public class PatientRefundOrderVO implements Serializable {
    @ApiModelProperty(value = "购买患者")
    private Integer patientId;
    @ApiModelProperty("订单id")
    private Integer orderDetailId;
    @ApiModelProperty("礼包名称")
    private String couponName;
    @ApiModelProperty("原价")
    private BigDecimal price;
    @ApiModelProperty("套餐价")
    private BigDecimal packagePrice;
    @ApiModelProperty("售卖单价")
    private BigDecimal saleAmount;
    @ApiModelProperty("卡券实收金额")
    private BigDecimal receivedAmount;
    @ApiModelProperty("订单实收金额")
    private BigDecimal totalReceivedAmount;
    @ApiModelProperty(value = "数量")
    private Integer quantity;
    @ApiModelProperty(value = "操作人")
    private String executorName;
    @ApiModelProperty(value = "是否全额退费（0-否，1-是）")
    private Boolean wholeRefund;
    @ApiModelProperty(value = "会员卡收费")
    private DeductionRefundPayVO memberPay;
    @ApiModelProperty(value = "预付款收费")
    private List<DeductionRefundPayVO> prePayment;
    @ApiModelProperty(value = "其他入账方式收费")
    private List<DeductionRefundPayVO> payment;
}
