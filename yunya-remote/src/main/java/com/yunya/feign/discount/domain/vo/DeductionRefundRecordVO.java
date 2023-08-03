package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2023/07/31
 */
@Data
@ApiModel(value = "划扣退费记录模型")
public class DeductionRefundRecordVO implements Serializable {
    @ApiModelProperty("产品名称")
    private String couponName;
    @ApiModelProperty("售卖单价")
    private BigDecimal saleAmount;
    @ApiModelProperty("退款金额")
    private BigDecimal refundAmount;
    @ApiModelProperty("退费方式")
    private String refundType;
    @ApiModelProperty("诊所")
    private String orgName;
    @ApiModelProperty(value = "操作人")
    private String executorName;
    @ApiModelProperty(value = "备注")
    private String remark;
}
