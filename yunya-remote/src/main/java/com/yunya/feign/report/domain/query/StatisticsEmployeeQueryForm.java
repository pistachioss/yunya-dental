package com.yunya.feign.report.domain.query;

import com.yunya.feign.report.domain.query.base.ClinicDateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Collection;

/**
 * 简介：统计员工查询VO
 *
 * @author: chenlin
 * @Description: 统计员工查询VO
 * @Date: 2022/1/4 11:11
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("统计员工查询VO")
public class StatisticsEmployeeQueryForm extends ClinicDateRangeQueryForm {
    /** 员工ID列表 */
    @ApiModelProperty(value = "员工ID列表")
    private Collection<Integer> dentistIds;
}
