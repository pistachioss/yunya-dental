package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/3/21 13:44
 * @description: 账单项目类型分类统计数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单项目类型分类统计数据模型")
public class BillItemReceivableAmountVO implements Serializable {
    /** 账单id */
    @ApiModelProperty("账单id")
    private Integer billId;
    /** 价目总应收 */
    @ApiModelProperty("价目总应收")
    private BigDecimal tariffReceivableAmount;
    /** 商品总应收 */
    @ApiModelProperty("商品总应收")
    private BigDecimal oralReceivableAmount;
}
