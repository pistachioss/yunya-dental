package com.yunya.feign.employee_attend.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介：外勤信息查询参数模型
 *
 * @author: chenlin
 * @Description: 外勤信息查询参数模型
 * @Date: 2020/11/13 09:42
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("外勤信息查询参数模型")
public class FieldInfoQueryForm implements Serializable {

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

    /** 外勤地址 */
    @ApiModelProperty(value = "外勤地址")
    private Integer fieldAddress;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /** 开始日期 */
    @ApiModelProperty(value = "开始日期")
    private Date betweenDate;

    /** 结束日期 */
    @ApiModelProperty(value = "结束日期")
    private Date andDate;

    /** 外勤原因 */
    @ApiModelProperty(value = "外勤原因")
    private String fieldReason;

    /** 审批人id （直接存员工id 与审批人员表无关） */
    @ApiModelProperty(value = "审批人id （直接存员工id 与审批人员表无关）")
    private Integer approvalPeopleId;

    /** 审批状态 0 审批中 1通过 2拒绝 3撤回 4异常 */
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝 3撤回 4异常")
    private Integer apprpvalStatus;
}
