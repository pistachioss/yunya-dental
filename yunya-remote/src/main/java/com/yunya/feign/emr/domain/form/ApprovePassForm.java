package com.yunya.feign.emr.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2020/8/3
 */
@Getter
@Setter
@ApiModel(value = "审批通过对象模型")
public class ApprovePassForm {
    @ApiModelProperty(value = "电子病例Id", required = true)
    @NotNull
    private Integer eventId;
}
