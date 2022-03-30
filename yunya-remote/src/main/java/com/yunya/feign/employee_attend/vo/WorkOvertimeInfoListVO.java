package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
@ApiModel(value = "加班信息")
public class WorkOvertimeInfoListVO extends HadReadVO implements Serializable {
    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 申请人ID */
    @ApiModelProperty(value = "申请人ID")
    private Integer userId;

    /** 加班日期 */
    @ApiModelProperty(value = "加班日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date workDate;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer companyId;

    /** 门诊名称 */
    @ApiModelProperty(value = "门诊名称")
    private String companyName;

    /** 班次id */
    @ApiModelProperty(value = "班次id")
    private Integer scheduleId;

    /** 加班事由 */
    @ApiModelProperty(value = "加班事由")
    private String overtimeReason;

    /** 审批人id（直接存员工id 与审批人员表没有关系） */
    @ApiModelProperty(value = "审批人id（直接存员工id 与审批人员表没有关系）")
    private Integer approvalPeopleId;

    /** 审批状态 0 审批中 1通过 2拒绝  3撤回 4过期*/
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝  3撤回 4过期")
    private Integer approvalStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String refuseReason;

    /** 加班时长*/
    @ApiModelProperty(value = "加班时长")
    private Integer workOverTime;

    /** 开始时间 */
    @ApiModelProperty(value = "加班开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "加班结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    /** 班次名称*/
    @ApiModelProperty(value = "班次名称")
    private String name;

    /** 审批人名称 */
    @ApiModelProperty(value = "审批人名称")
    private String approvalPeopleName;

    /** 申请人名称 */
    @ApiModelProperty(value = "申请人名称")
    private String userName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date crtTime;

    /**
     * 审批（修改）时间
     */
    @ApiModelProperty(value = "审批（修改）时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updTime;
}
