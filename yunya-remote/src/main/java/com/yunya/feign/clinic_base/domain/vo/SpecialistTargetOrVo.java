package com.yunya.feign.clinic_base.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@ApiModel(value = "返回模型")
public class SpecialistTargetOrVo {

    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
}
