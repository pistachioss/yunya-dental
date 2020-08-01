package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2020/7/30
 */
@Getter
@Setter
@ApiModel(value = "草稿病例申请")
public class DraftMedicalApplyModel {

    @ApiModelProperty(value = "审批事件ID")
    @NotNull
    private Integer eventId;

    @ApiModelProperty(value = "审批事件类型 0：草稿病历审批，1：申请新增病历审批，2：申请修改病历审批，3：删除审批")
    @NotNull
    private Integer eventType;

    @ApiModelProperty(value = "申请人Id")
    @NotNull
    private Integer proposerId;

    @ApiModelProperty(value = "审批人Id")
    @NotNull
    private Integer approverId;


}
