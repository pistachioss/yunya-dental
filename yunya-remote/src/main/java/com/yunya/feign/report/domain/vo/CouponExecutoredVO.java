package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：产品使用VO
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/3/29 10:07
 * @since: 1.0.0
 */
@ApiModel("产品使用VO")
@Data
@ToString
public class CouponExecutoredVO implements Serializable {

    /** 门诊ID*/
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    /** 门诊*/
    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 执行人ID*/
    @ApiModelProperty("执行人ID")
    private Integer executorId;

    /** 执行人*/
    @Excel(name = "执行人")
    @ApiModelProperty("执行人")
    private String executorName;

    /** 产品ID*/
    @ApiModelProperty("产品ID")
    private Integer couponId;

    /** 产品*/
    @Excel(name = "产品")
    @ApiModelProperty("产品")
    private String couponName;

    /** 项目ID*/
    @ApiModelProperty("项目ID")
    private Integer itemId;

    /** 项目类型*/
    @ApiModelProperty("项目类型")
    private Integer itemType;

    /** 使用项目*/
    @Excel(name = "使用项目")
    @ApiModelProperty("使用项目")
    private String itemName;

    /** 数量*/
    @Excel(name = "数量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("数量")
    private String num;
}
