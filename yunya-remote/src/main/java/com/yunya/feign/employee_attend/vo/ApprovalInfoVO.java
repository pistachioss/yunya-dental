package com.yunya.feign.employee_attend.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
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
@ApiModel
public class ApprovalInfoVO {
    /**
     * 请假信息表id
     */
    @ApiModelProperty("请假信息表id")
    private Integer leaveId;
    /**
     * 审批级别人员员工Id
     */
    @ApiModelProperty("审批级别人员Id")
    private Integer userId;
    /**
     * 审批级别人员名称
     */
    @ApiModelProperty("审批级别人员名称")
    private String approvalPeopleName;

    /**
     * 审批优先级
     */
    @ApiModelProperty("审批优先级")
    private Integer approvalPriority;
    /**
     * 此审批人给予当前请假的审批状态
     */
    @ApiModelProperty("此审批人给予当前请假的审批状态")
    private Integer approvalStatus;
    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date crtTime;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updTime;

}
