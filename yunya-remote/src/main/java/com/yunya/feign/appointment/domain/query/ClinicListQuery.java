package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/10
 * @description:
 */
@ApiModel(value = "ClinicListQuery",description = "门诊列表参数")
@Data
public class ClinicListQuery {

    /** 经度 */
    @ApiModelProperty(value = "经度", required = true)
    private Double longitude;

    /** 纬度 */
    @ApiModelProperty(value = "纬度", required = true)
    private Double latitude;

}
