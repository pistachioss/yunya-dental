package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author xiangyang
 * @date 2020/8/4
 */
@Getter
@Setter
@ApiModel(value = "病例变更申请对象模型")
public class ChangeMedicalApplyModel {

    @ApiModelProperty(value = "申请变更基础信息（申请新增变更eventId需传入就诊Id，申请修改变更eventId需传入电子病例Id），变更申请不需要传审批人id")
    private ApplyBaseModel applyBase;

    @ApiModelProperty(value = "申请原因", required = true)
    @Size(max = 150)
    @NotBlank
    private String applyReason;
}
