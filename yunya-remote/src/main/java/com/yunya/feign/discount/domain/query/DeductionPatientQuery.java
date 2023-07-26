package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2023/7/17
 */
@Getter
@Setter
@ApiModel(value = "患者档案划扣卡产品列表")
public class DeductionPatientQuery {
    @ApiModelProperty(value = "产品名称")
    private String couponName;
    @ApiModelProperty(value = "患者id",required = true)
    @NotNull
    private Integer patientId;
}
