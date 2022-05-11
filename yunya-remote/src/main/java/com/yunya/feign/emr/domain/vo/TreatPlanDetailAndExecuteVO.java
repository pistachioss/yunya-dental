package com.yunya.feign.emr.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介：治疗计划详情执行情况
 *
 * @author: chenlin
 * @Description: 治疗计划详情执行情况
 * @Date: 2022/5/7 15:51
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划详情执行情况VO")
public class TreatPlanDetailAndExecuteVO implements Serializable {
    /** 订单详情id*/
    @ApiModelProperty("订单详情id")
    private Integer orderDetailId;

    /** 项目名称*/
    @Excel(name = "项目名称")
    @ApiModelProperty("项目名称")
    private String billItemName;

    /** 单位*/
    @Excel(name = "单位")
    @ApiModelProperty("单位")
    private String unit;

    /** 单价*/
    @Excel(name = "单价", type = EXPORT, cellType = NUMERIC)
    @ApiModelProperty("单价")
    private BigDecimal price;

    /** 数量*/
    @Excel(name = "数量", type = EXPORT, cellType = NUMERIC)
    @ApiModelProperty("数量")
    private Integer quantity;

    /** 原价*/
    @Excel(name = "原价", type = EXPORT, cellType = NUMERIC)
    @ApiModelProperty("原价")
    private BigDecimal originPrice;

    /** 备注*/
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /** 执行人*/
    @Excel(name = "执行人", defaultValue = "--")
    @ApiModelProperty("执行人")
    private String executeName;

    /** 执行时间*/
    @Excel(name = "执行时间", dateFormat = "yyyy-MM-dd", defaultValue = "--")
    @ApiModelProperty("执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date executeDate;
}
