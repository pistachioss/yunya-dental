package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;

/**
 * 简介：产品使用明细VO
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/3/29 10:07
 * @since: 1.0.0
 */
@ApiModel("产品使用明细VO")
@Data
@ToString
public class CouponExecutoredDetailVO implements Serializable {

    /** 门诊*/
    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 执行人*/
    @Excel(name = "执行人")
    @ApiModelProperty("执行人")
    private String executorName;

    /** 账单编号*/
    @Excel(name = "账单编号")
    @ApiModelProperty("账单编号")
    private String billNum;

    /** 产品*/
    @Excel(name = "产品")
    @ApiModelProperty("产品")
    private String couponName;

    /** 使用项目*/
    @Excel(name = "使用项目")
    @ApiModelProperty("使用项目")
    private String itemName;

  /** 数量 */
  @Excel(name = "数量", cellType = NUMERIC)
  @ApiModelProperty("数量")
  private String num;

    /** 患者*/
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String patientName;

    /** 患者手机*/
    @Excel(name = "患者手机")
    @ApiModelProperty("患者手机")
    private String mobile;

    /** 使用时间*/
    @Excel(name = "使用时间", dateFormat = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date useDate;
}
