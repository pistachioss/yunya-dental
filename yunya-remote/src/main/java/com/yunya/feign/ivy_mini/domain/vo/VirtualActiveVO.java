package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "虚拟服务激活")
public class VirtualActiveVO {
    @ApiModelProperty(value = "激活患者")
    private Integer patientName;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "激活时间")
    private String activeDate;
}