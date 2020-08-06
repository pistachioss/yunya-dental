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

    @ApiModelProperty(value = "申请人Id", required = true)
    @NotNull
    private Integer proposerId;

    @ApiModelProperty(value = "审批人Id", required = true)
    @NotNull
    private Integer approverId;
}
