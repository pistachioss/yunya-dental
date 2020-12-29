package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: yunya-dental
 * @description: 查看优惠明细视图VO
 * @author: LHB
 * @create: 2020-12-29 10:51
 **/
@Data
@ApiModel(value = "BillDiscountDetailInifoVO",description = "查看优惠明细视图VO")
public class BillDiscountDetailInifoVO implements Serializable {
    @ApiModelProperty("项目ID")
    private Integer itemId;
    @ApiModelProperty("订单记录ID(order_record表ID)")
    private Integer billId;
    @ApiModelProperty("开单项目名称")
    private String itemName;
    @ApiModelProperty("单位")
    private String unit;
    @ApiModelProperty("价格")
    private BigDecimal price;
    @ApiModelProperty("数量")
    private Integer quantity;
    @ApiModelProperty("原价")
    private BigDecimal originPrice;
    @ApiModelProperty("产品名称")
    private String couponName;
    @ApiModelProperty("卡号")
    private String cardNumber;
    @ApiModelProperty("销售渠道")
    private String saleChannelName;
    @ApiModelProperty("优惠金额")
    private BigDecimal benefitAmount;
    @ApiModelProperty("执行人")
    private String employeeName;


}
