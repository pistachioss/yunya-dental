package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：外勤信息响应模型
 *
 * @author: chenlin
 * @Description: 外勤信息响应模型
 * @Date: 2020/11/11 11:22
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("外勤信息响应模型")
public class FieldInfoVO implements Serializable {

    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 申请人ID */
    @ApiModelProperty(value = "申请人ID")
    private Integer userId;

    /** 组织ID */
    @ApiModelProperty(value = "组织ID")
    private Integer companyId;

    /** 外勤地址 */
    @ApiModelProperty(value = "外勤地址")
    private String fieldAddress;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /** 外勤原因 */
    @ApiModelProperty(value = "外勤原因")
    private String fieldReason;

    /** 审批人id（直接存员工id 与审批人员表没有关系） */
    @ApiModelProperty(value = "审批人id（直接存员工id 与审批人员表没有关系）")
    private Integer approvalPeopleId;

    /** 审批状态 0 审批中 1通过 2拒绝  3撤回 4过期*/
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝  3撤回 4过期")
    private Integer apprpvalStatus;
}
