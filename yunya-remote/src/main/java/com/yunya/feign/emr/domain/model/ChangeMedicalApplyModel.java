package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author xiangyang
 * @date 2020/8/4
 */
@Getter
@Setter
@ApiModel(value = "病例变更申请对象模型")
public class ChangeMedicalApplyModel {
    @ApiModelProperty(value = "事件ID", required = true)
    @NotNull
    private Integer eventId;

    @ApiModelProperty(value = "申请原因", required = true)
    @Size(max = 150)
    @NotBlank
    private String applyReason;
}
