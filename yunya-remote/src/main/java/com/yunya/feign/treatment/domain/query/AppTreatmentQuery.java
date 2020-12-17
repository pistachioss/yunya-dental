package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: App端参数封装
 * @author: LHB
 * @create: 2020-12-17 17:21
 **/
@Data
@ApiModel(value = "AppTreatmentQuery",description = "App端参数封装")
public class AppTreatmentQuery implements Serializable {
    @ApiModelProperty("预约ID")
    private Integer appointId;
    @ApiModelProperty("挂号ID")
    private Integer registeredId;
    @ApiModelProperty("就诊ID")
    private Integer treatmentId;
}
