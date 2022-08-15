package com.yunya.feign.ivy_mini.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "虚拟服务激活")
public class VirtualActiveVO {
    @ApiModelProperty(value = "激活患者")
    private String patientName;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "激活时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime activeDate;
}