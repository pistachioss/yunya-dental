package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：门诊业绩VO
 *
 * @author: chenlin
 * @Description: 门诊业绩VO
 * @Date: 2021/12/16 16:45
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊业绩VO")
public class ClinicAchievementVO implements Serializable {

    @ApiModelProperty("门诊id")
    private Integer orgId;

    @Excel(name = "院区")
    @ApiModelProperty("院区")
    private String campus;

    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;

    @Excel(name = "目标值", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("目标值")
    private BigDecimal businessGoal = new BigDecimal("0.00");

    @Excel(name = "实际值", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("实际值")
    private BigDecimal actualWorkload = new BigDecimal("0.00");

    @Excel(name = "完成度")
    @ApiModelProperty("完成度")
    private String completedPer = "0.00%";

    @Excel(name = "今日完成", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("今日完成")
    private BigDecimal todayCompleted = new BigDecimal("0.00");

    @Excel(name = "同比", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("同比")
    private BigDecimal cmpPreYear = new BigDecimal("0.00");

}
