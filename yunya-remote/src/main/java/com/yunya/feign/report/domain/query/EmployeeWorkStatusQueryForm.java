package com.yunya.feign.report.domain.query;

import com.yunya.feign.report.domain.query.base.NumDateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：员工状态查询VO
 *
 * @author: chenlin
 * @Description: 员工状态查询VO
 * @Date: 2021/12/16 13:49
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工状态查询VO")
public class EmployeeWorkStatusQueryForm extends NumDateRangeQueryForm implements Serializable {
    private Integer[] employeeIds;

    /** 就职状态 */
    @ApiModelProperty("就职状态（试用: 0, 正式: 1，实习: 2, 离职:3）")
    private Integer[] workStatus;
}
