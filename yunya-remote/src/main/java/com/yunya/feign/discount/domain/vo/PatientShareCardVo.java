package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
@Getter
@Setter
@ApiModel(value = "患者共享产品分页模型")
public class PatientShareCardVo extends PatientCardBaseVo{
    @ApiModelProperty(value = "卡主")
    private String cardOwner;
}
