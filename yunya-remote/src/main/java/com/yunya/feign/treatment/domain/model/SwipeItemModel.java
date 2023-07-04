package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/7/3 12:59
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("划扣卡核销项目添加模型")
public class SwipeItemModel implements Serializable {

    /** 划扣卡订单明细id */
    @ApiModelProperty(value = "划扣卡订单明细id", required = true)
    @NotNull(message = "划扣卡订单明细id")
    private Integer couponOrderDetailId;

    /** 项目类型：0-价目，1-商品 */
    @ApiModelProperty(value = "项目类型：0-价目，1-商品", required = true)
    @NotNull(message = "项目类型不能为空")
    private Byte itemType;

    /** 项目id */
    @ApiModelProperty(value = "项目id", required = true)
    @NotNull(message = "项目id不能为空")
    private Integer itemId;

    /** 套餐价 */
    @ApiModelProperty(value = "套餐价", required = true)
    @NotNull(message = "套餐价不能为空")
    private BigDecimal packagePrice;

    /** 数量 */
    @ApiModelProperty(value = "数量", required = true)
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /** 划扣卡补入工作量 */
    @ApiModelProperty(value = "划扣卡补入工作量", required = true)
    @NotNull(message = "补入工作量不能为空")
    private BigDecimal supplyWorkload;
    
    /** 订单明细id */
    @ApiModelProperty(value = "订单明细id", hidden = true)
    private Integer orderDetailId;
}
