package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
@Setter
@Getter
@ApiModel(value = "门诊分配详情模型")
public class ClinicAllocateModel {

    @ApiModelProperty(value = "配给门诊")
    @NotNull
    private Integer orgId;

    @ApiModelProperty(value = "优惠券分配id")
    @NotNull
    private Integer couponAllocateId;

    @ApiModelProperty(value = "数量")
    @NotNull
    private Integer allocateNum;
}
