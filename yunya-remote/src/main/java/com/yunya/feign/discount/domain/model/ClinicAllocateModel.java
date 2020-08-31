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
    @ApiModelProperty(value = "配给对象", required = true)
    @NotNull
    private Integer orgId;
    @ApiModelProperty(value = "优惠券分配id", required = true)
    @NotNull
    private Integer couponAllocateId;
    @ApiModelProperty(value = "数量", required = true)
    @NotNull
    private Integer allocateNum;
}
