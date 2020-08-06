package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author xiangyang
 * @date 2020/8/5
 */
@Getter
@Setter
@ApiModel(value = "拒绝变更申请模型")
public class ChangeApproveRejectForm {

    @ApiModelProperty(value = "事件Id", required = true)
    @NotNull
    private Integer eventId;

    @ApiModelProperty(value = "拒绝原因", required = true)
    @Size(max = 150)
    @NotBlank
    private String rejectReason;
}
