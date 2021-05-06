package com.yunya.feign.wechat.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: xy
 * @date 2021/4/27 10:20
 **/
@Data
public class WxAppointConfirmModel {
    @ApiModelProperty(value = "预约id", required = true)
    @NotNull
    private Integer appointId;
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull
    private Integer patientId;
    @ApiModelProperty(value = "患者姓名", required = true)
    @NotBlank
    private String  patientName;
    @ApiModelProperty(value = "确认状态,true-已确认；false-未确认", required = true)
    @NotNull
    private Boolean flag;
}
