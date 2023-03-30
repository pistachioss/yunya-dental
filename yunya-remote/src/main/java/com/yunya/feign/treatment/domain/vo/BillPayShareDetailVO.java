package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/3/29 16:03
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单收费分摊明细")
public class BillPayShareDetailVO implements Serializable {
    /** 订单id */
    @ApiModelProperty("订单id")
    private Integer orderRecordId;

    /** 项目类型：0-价目；1-商品 */
    @ApiModelProperty("项目类型：0-价目；1-商品")
    private Byte itemType;

    /** 项目id */
    @ApiModelProperty("项目id")
    private Integer itemId;

    /** 项目应收金额 */
    @ApiModelProperty("项目应收金额")
    private BigDecimal actualReceivable;

    /** 项目已收金额 */
    @ApiModelProperty("项目已收金额")
    private BigDecimal receivedAmount;
}
