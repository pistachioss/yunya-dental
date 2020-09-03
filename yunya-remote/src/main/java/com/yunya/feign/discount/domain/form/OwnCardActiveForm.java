package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/8/29
 */
@Getter
@Setter
@ApiModel(value = "自有平台卡券激活对象")
public class OwnCardActiveForm {
    @ApiModelProperty(value = "卡券id", required = true)
    @NotNull
    private Integer cardId;
    @ApiModelProperty(value = "入账方式")
    private Integer payId;
}
