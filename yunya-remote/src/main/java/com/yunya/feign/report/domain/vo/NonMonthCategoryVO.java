package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：非本月免单金额VO
 *
 * @author: chenlin
 * @Description: 非本月免单金额明细
 * @Date: 2021/4/9 16:37
 * @since: 1.0.0
 */
@ToString
@Data
@ApiModel("非本月免单金额VO")
public class NonMonthCategoryVO implements Serializable {
    private Integer categoryId;
    private Integer itemId;
    private Integer itemType;
    private Integer billId;
    /** 门诊*/
    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;
    /** 开单日期*/
    @Excel(name = "开单日期")
    @ApiModelProperty("开单日期")
    private String billDate;
    /** 患者姓名*/
    @Excel(name = "患者姓名")
    @ApiModelProperty("患者姓名")
    private String patientName;
    /** 开单金额*/
    @Excel(name = "开单金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("开单金额")
    private BigDecimal billAmount = BigDecimal.ZERO;
    /** 项目大类*/
    @Excel(name = "项目大类")
    @ApiModelProperty("项目大类")
    private String categoryName;
    /** 项目大类金额*/
    @Excel(name = "项目大类金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("项目大类金额")
    private BigDecimal actualAmount = BigDecimal.ZERO;
    /** 非当月免单金额*/
    @Excel(name = "非当月免单金额", scale = 2, cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("非当月免单金额")
    private BigDecimal freeBillAmount = BigDecimal.ZERO;
    /** 免单时间*/
    @Excel(name = "免单时间")
    @ApiModelProperty("免单时间")
    private String freeDate;
    /** 非当月补入工作量*/
    @Excel(name = "非当月补入工作量", scale = 2, cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("非当月补入工作量")
    private BigDecimal couponWorkload = BigDecimal.ZERO;
    /** 非当月免单金额分摊*/
    @Excel(name = "非当月免单金额分摊", scale = 2, cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("非当月免单金额分摊")
    private BigDecimal freeAmount = BigDecimal.ZERO;
}
