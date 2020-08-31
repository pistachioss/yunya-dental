package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
@Getter
@Setter
@ApiModel(value = "患者卡券查询已配置共享人模型")
public class PatientCardSharerVo {
    @ApiModelProperty(value = "共享人患者id")
    private Integer sharerId;
    @ApiModelProperty(value = "共享人患者姓名")
    private String sharerName;
    @ApiModelProperty(value = "共享人患者手机号")
    private String sharerPhoneNumber;
}
