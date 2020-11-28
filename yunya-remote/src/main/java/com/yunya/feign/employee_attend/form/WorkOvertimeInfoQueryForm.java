package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介：加班信息查询参数模型
 *
 * @author: chenlin
 * @Description: 加班信息查询参数模型
 * @Date: 2020/11/28 11:09
 * @since: 1.0.0
 */
@ApiModel("加班信息查询参数模型")
@Data
@ToString
public class WorkOvertimeInfoQueryForm implements Serializable {
    private static final long serialVersionUID = -9144700613607161531L;

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 员工id */
    @ApiModelProperty(value = "员工id")
    private Integer userId;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer companyId;

    /** 加班日期 */
    @ApiModelProperty(value = "加班日期")
    private Date workDate;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /** 要加班的休息班排班Id */
    @ApiModelProperty(value = "要加班的休息班排班Id")
    private Integer rest_schedule_id;

    /** 审批条件表id */
    @ApiModelProperty(value = "审批条件表id")
    private Integer approvalCriteriaId;

    /** 审批状态 0 审批中 1通过 2拒绝 3撤回 4异常 */
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝 3撤回 4异常")
    private Integer apprpvalStatus;
}
