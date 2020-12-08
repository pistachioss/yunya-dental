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
@ApiModel(value = "申请记录信息")
public class ApprovalAllListVO {

    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("申请人Id")
    private Integer userId;
    @ApiModelProperty("申请人姓名")
    private String userName;
    @ApiModelProperty("申请时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date crtTime;
    @ApiModelProperty("开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty("结束日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
    @ApiModelProperty("审批人ID集合，用逗号隔开")
    private String approvalPeopleId;
    @ApiModelProperty("审批人姓名集合，用逗号隔开")
    private String approvalPeopleName;
    @ApiModelProperty("申请类型 加班 外勤 请假")
    private String status;
    @ApiModelProperty("审批状态")
    private Integer apprpvalStatus;

}
