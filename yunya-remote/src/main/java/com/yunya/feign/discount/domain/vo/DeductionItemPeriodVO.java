package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "划扣项目详情")
public class DeductionItemPeriodVO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "记录明细id")
    private Integer id;

    /**
     * 卡券ID
     */
    @ApiModelProperty(value = "礼包id")
    private Integer couponId;

    /**
     * 类型 0:基础价目表,1:基础商品表 2:选项目明细
     */
    @ApiModelProperty(value = "项目类型 0:基础价目表,1:基础商品表")
    private Integer type;

    /**
     * 明细ID
     */
    @ApiModelProperty(value = "明细ID")
    private Integer itemId;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer count;

    /**
     * 套餐单价
     */
    @ApiModelProperty(value = "套餐单价")
    private BigDecimal packageUnitPrice;

    /**
     * 套餐价
     */
    @ApiModelProperty(value = "套餐价")
    private BigDecimal saleAmount;

    /**
     * 单个明细工作量
     */
    @ApiModelProperty(value = "单个数量补入工作量")
    private BigDecimal workloadLoad;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

//    @ApiModelProperty(value = "原价")
//    private BigDecimal price;

}