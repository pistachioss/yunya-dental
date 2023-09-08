package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
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
@ApiModel(value = "划扣购买记录")
public class DeductionBuyVO implements Serializable {
    @ApiModelProperty("购卡人姓名")
    @ExcelProperty("购卡人姓名")
    private String buyer;
    @ApiModelProperty("手机号")
    @ExcelProperty("手机号")
    private String mobile;
    @ApiModelProperty("产品名称")
    @ExcelProperty("产品名称")
    private String couponName;
    @ApiModelProperty("卡号")
    @ExcelProperty("卡号")
    private String cardNumber;
    @ApiModelProperty("购买日期")
    @ExcelProperty("购买日期")
    private String buyDate;
    @ApiModelProperty("订单号")
    @ExcelProperty("订单号")
    private String orderNumber;
    @ApiModelProperty("入账方式")
    @ExcelProperty("入账方式")
    private String accountTypeName;
    @ApiModelProperty("购买门诊")
    @ExcelProperty("购买门诊")
    private String orgName;
    @ApiModelProperty("原价")
    @ExcelProperty("原价")
    private BigDecimal price;
    @ApiModelProperty("售卖单价")
    @ExcelProperty("售卖单价")
    private BigDecimal saleAmount;
    @ApiModelProperty("操作人")
    @ExcelProperty("操作人")
    private Integer operateName;
}
