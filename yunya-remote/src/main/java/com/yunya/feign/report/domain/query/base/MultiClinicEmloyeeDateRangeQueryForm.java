package com.yunya.feign.report.domain.query.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/5/10 9:06
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("多员工+多门诊+日期范围查询")
public class MultiClinicEmloyeeDateRangeQueryForm extends MultiClinicDateRangeQueryForm {
    
    
    /** 员工id列表 */
    @ApiModelProperty("员工id列表")
    private List<Integer> employeeIds;
}
