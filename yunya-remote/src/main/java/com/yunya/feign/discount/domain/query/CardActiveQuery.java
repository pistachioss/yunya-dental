package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/8/28
 */
@Getter
@Setter
@ApiModel(value = "卡券激活详情查询")
public class CardActiveQuery {
    @ApiModelProperty(value = "卡号", required = true)
    @NotBlank
    private String cardNumber;
    @ApiModelProperty(value = "卡密", required = true)
    @NotBlank
    private String cardPassword;
}
