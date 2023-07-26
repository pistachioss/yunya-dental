package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2023/8/29
 */
@Getter
@Setter
@ApiModel(value = "划扣卡券转赠对象")
public class DeductionChangeForm {
    @ApiModelProperty(value = "卡券id", required = true)
    @NotNull
    private Integer cardId;
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull
    private Integer patientId;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "手机号")
    private String mobile;
}
