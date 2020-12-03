package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ApprovalAllListForm {
    @ApiModelProperty("发起时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date crtTime;

    @ApiModelProperty("申请开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("申请人姓名")
    private String userName;

    @ApiModelProperty("申请人姓名")
    private Integer userId;

    @ApiModelProperty("申请类型 加班 外勤 请假")
    private String status;

    @ApiModelProperty("审批状态 0 审批中 1通过 2拒绝  3撤回")
    private Integer apprpvalStatus;
}
