package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介：员工奖金系数VO
 *
 * @author: chenlin
 * @Description: 员工奖金系数VO
 * @Date: 2021/10/27 9:58
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工奖金系数VO")
public class ClinicEmployeBonusCoefficientVO extends ClinicEmployeeReportVO implements Serializable {
    
    /** 奖金系数 */
    @ApiModelProperty("奖金系数")
    private BigDecimal bonusCoefficient;
}
