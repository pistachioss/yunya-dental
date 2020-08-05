package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2020/8/4
 */
@Getter
@Setter
@ApiModel(value = "审批申请基础对象")
public class ApplyBaseModel {

    @ApiModelProperty(value = "事件ID", required = true)
    @NotNull
    private Integer eventId;

    @ApiModelProperty(value = "审批事件类型 0：草稿病历审批，1：申请病历变更审批，2：删除审批", required = true)
    @NotNull
    private Integer eventType;

    @ApiModelProperty(value = "申请人Id", required = true)
    @NotNull
    private Integer proposerId;

    @ApiModelProperty(value = "审批人Id", required = true)
    @NotNull
    private Integer approverId;

    @ApiModelProperty(value = "申请类型 0：新增，1：修改", required = true)
    private Integer applyType;
}
