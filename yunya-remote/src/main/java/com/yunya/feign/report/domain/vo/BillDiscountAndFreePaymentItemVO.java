package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：折扣&免单支付项目VO
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/3/25 16:21
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("折扣&免单支付项目VO")
public class BillDiscountAndFreePaymentItemVO implements Serializable {

    /** 折扣/免单ID*/
    @ApiModelProperty("折扣/免单ID")
    private Integer accountType;

    /** 折扣/免单名称*/
    @ApiModelProperty("折扣/免单名称")
    private String name;
}
