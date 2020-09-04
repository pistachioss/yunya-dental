package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/9/3
 */
@Setter
@Getter
@ApiModel(value = "患者选择优惠")
public class PatientBenefitQuery {
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull
    private Integer patientId;
}
