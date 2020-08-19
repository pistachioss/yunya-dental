package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
@Setter
@Getter
@ApiModel(value = "门诊分配详情模型")
public class ClinicAllocateModel {

    @ApiModelProperty(value = "配给门诊")
    private Integer orgId;
    @ApiModelProperty(value = "数量")
    private Integer allocateNum;
}
