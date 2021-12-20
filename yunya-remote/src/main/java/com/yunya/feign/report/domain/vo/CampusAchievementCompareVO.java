package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：院区业绩同比VO
 *
 * @author: chenlin
 * @Description: 院区业绩同比VO
 * @Date: 2021/12/18 14:34
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("院区业绩同比VO")
public class CampusAchievementCompareVO implements Serializable {
    @Excel(name = "院区")
    @ApiModelProperty("院区")
    private String campusName;

    @Excel(name = "工作量", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("工作量")
    private BigDecimal workload = new BigDecimal("0.00");

    @Excel(name = "占比")
    @ApiModelProperty("占比")
    private String workloadRatio = "0.00%";

    @Excel(name = "初诊人数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("初诊人数")
    private Integer firstVisitCount = 0;

    @Excel(name = "占比")
    @ApiModelProperty("占比")
    private String firstVisitCountRatio = "0.00%";

    @Excel(name = "就诊人数", cellType = Excel.ColumnType.NUMERIC)
    @ApiModelProperty("就诊人数")
    private Integer treatVisitCount = 0;

    @Excel(name = "占比")
    @ApiModelProperty("占比")
    private String treatVisitCountRatio = "0.00%";

}
