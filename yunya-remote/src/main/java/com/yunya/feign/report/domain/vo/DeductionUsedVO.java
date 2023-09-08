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
@ApiModel(value = "报表划扣使用记录")
public class DeductionUsedVO implements Serializable {
    @ApiModelProperty("产品名称")
    @ExcelProperty("产品名称")
    private String couponName;
    @ApiModelProperty("卡号")
    @ExcelProperty("卡号")
    private String cardNumber;
    @ApiModelProperty("销售渠道")
    @ExcelProperty("销售渠道")
    private String saleChannel;
    @ApiModelProperty("售卖单价")
    @ExcelProperty("售卖单价")
    private BigDecimal saleAmount;
    @ApiModelProperty("消耗项目")
    @ExcelProperty("消耗项目")
    private String itemName;
    @ApiModelProperty("项目类型")
    @ExcelProperty("项目类型")
    private Integer itemType;
    @ApiModelProperty("消耗数量")
    @ExcelProperty("消耗数量")
    private Integer quantity;
    @ApiModelProperty("消耗患者")
    @ExcelProperty("消耗患者")
    private String patientName;
    @ApiModelProperty("账单编号")
    @ExcelProperty("账单编号")
    private String billNumber;
    @ApiModelProperty("划扣时间")
    @ExcelProperty("划扣时间")
    private String billDate;
    @ApiModelProperty("诊所")
    @ExcelProperty("诊所")
    private String orgName;
    @ApiModelProperty(value = "操作人")
    @ExcelProperty("操作人")
    private String executorName;
    @ApiModelProperty(value = "备注")
    @ExcelProperty("备注")
    private String remark;
}
