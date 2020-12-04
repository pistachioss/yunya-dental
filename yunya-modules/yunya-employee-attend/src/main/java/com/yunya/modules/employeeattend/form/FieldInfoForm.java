package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

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
public class FieldInfoForm {

    private Integer id;

    @ApiModelProperty("申请人ID")
    private Integer userId;

    @ApiModelProperty("抄送人ID")
    private Integer copyId;
    /**
     * 外勤地址
     */
    @ApiModelProperty("外勤地址")
    private String fieldAddress;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id")
    private Integer companyId;

    /**
     * 外勤原因
     */
    @ApiModelProperty("外勤原因")
    private String fieldReason;

    /**
     * 审批人id （直接存员工id 与审批人员表无关）
     */
    @ApiModelProperty("审批人id （直接存员工id 与审批人员表无关）")
    private Integer approvalPeopleId;

    @ApiModelProperty("抄送人Id集合")
    private List<Integer> copyList;
    /**
     * 审批状态 0 审批中 1通过 2拒绝  3撤回 4过期
     */
    @ApiModelProperty("审批状态 0 审批中 1通过 2拒绝  3撤回 4过期")
    private Integer apprpvalStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String refuseReason;

    /**
     * 创建人
     */
    @ApiModelProperty("crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @ApiModelProperty("crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @ApiModelProperty("upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @ApiModelProperty("upd_time")
    private Date updTime;
}
