package com.yunya.feign.report.domain.vo;


import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2021/11/18
 * @description: 公司端-市场报表-产品使用报表
 */
@ApiModel(value = "公司端/门诊端-产品使用报表")
@Data
public class CouponUseVo {
    @ApiModelProperty(value = "门诊")
    @ExcelProperty(value = "门诊")
    private String orgName;
    @ApiModelProperty(value = "产品")
    @ExcelProperty(value = "产品")
    private String couponName;
    @ApiModelProperty(value = "患者")
    @ExcelProperty(value = "患者")
    private String patName;
    @ApiModelProperty(value = "账单编号")
    @ExcelProperty(value = "账单编号")
    private String billNum;
    @ApiModelProperty(value = "账单日期")
    @ExcelProperty(value = "账单日期")
    private String billDate;
    @ApiModelProperty(value = "应收金额")
    @ExcelProperty(value = "应收金额")
    private BigDecimal orderAmount;
    @ApiModelProperty(value = "实收金额")
    @ExcelProperty(value = "实收金额")
    private BigDecimal actualAmount;

}
