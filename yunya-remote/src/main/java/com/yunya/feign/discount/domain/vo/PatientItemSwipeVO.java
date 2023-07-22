package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author chenlin
 * @descrption 患者开单项目明细划扣数据模型
 * @date 2023/7/20
 */
@Data
@ToString
@ApiModel(value = "患者开单项目明细划扣数据模型")
public class PatientItemSwipeVO {
    @ApiModelProperty(value = "订单明细id")
    private Integer orderDetailId;

    @ApiModelProperty(value = "项目类型（0-价目表；1-商品；）")
    private Integer type;

    @ApiModelProperty(value = "项目id")
    private Integer itemId;

    /** 所用划扣卡名称 */
    @ApiModelProperty("所用划扣卡名称")
    private String couponName;

    /** 卡券id */
    @ApiModelProperty("卡券id")
    private Integer cardId;

    /** 套餐单价 */
    @ApiModelProperty("套餐单价")
    private BigDecimal packageUnitPrice;

    /** 数量 */
    @ApiModelProperty("划扣数量")
    private Integer count;

    /** 划扣金额 */
    @ApiModelProperty("划扣金额")
    private BigDecimal saleAmount;

    /** 原价 */
    @ApiModelProperty("划扣原价")
    private BigDecimal price;

    @ApiModelProperty(value = "补入工作量")
    private BigDecimal supplyWorkload;
}
