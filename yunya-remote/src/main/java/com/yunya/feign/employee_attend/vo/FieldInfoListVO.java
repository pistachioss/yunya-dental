package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@ApiModel(value = "外勤信息")
public class FieldInfoListVO {
    /** 主键id */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /** 申请人ID */
    @ApiModelProperty(value = "申请人ID")
    private Integer userId;

    /** 申请人名称 */
    @ApiModelProperty(value = "申请人名称")
    private String userName;

    /** 外勤地址 */
    @ApiModelProperty(value = "外勤地址")
    private String fieldAddress;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer companyId;

    /** 门诊名称 */
    @ApiModelProperty(value = "门诊名称")
    private String companyName;

    /** 外勤原因 */
    @ApiModelProperty(value = "外勤原因")
    private String fieldReason;

    /** 审批人id（直接存员工id 与审批人员表没有关系） */
    @ApiModelProperty(value = "审批人id（直接存员工id 与审批人员表没有关系）")
    private Integer approvalPeopleId;

    /** 审批人名称 */
    @ApiModelProperty(value = "审批人名称")
    private String approvalPeopleName;

    /** 审批状态 0 审批中 1通过 2拒绝  3撤回 4过期*/
    @ApiModelProperty(value = "审批状态 0 审批中 1通过 2拒绝  3撤回 4过期")
    private Integer apprpvalStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String refuseReason;

    /** 外勤时长*/
    @ApiModelProperty(value = "外勤时长")
    private Integer fieldTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date crtTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updTime;
}
