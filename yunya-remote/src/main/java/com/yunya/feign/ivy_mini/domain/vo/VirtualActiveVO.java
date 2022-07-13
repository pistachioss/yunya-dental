package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "虚拟服务激活")
public class VirtualActiveVO {
    @ApiModelProperty(value = "id")
    private Integer patientName;
    @ApiModelProperty(value = "收货人")
    private String mobile;
    @ApiModelProperty(value = "收货人电话")
    private String activeDate;
}