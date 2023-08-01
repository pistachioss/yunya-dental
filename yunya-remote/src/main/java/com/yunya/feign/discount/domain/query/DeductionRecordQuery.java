package com.yunya.feign.discount.domain.query;

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
@ApiModel(value = "划扣卡券查询对象")
public class DeductionRecordQuery {
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull
    private Integer patientId;
}
