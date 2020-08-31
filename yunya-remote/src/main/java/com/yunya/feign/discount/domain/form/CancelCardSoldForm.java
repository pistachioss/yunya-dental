package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;

/**
 * @author xiangyang
 * @date 2020/8/28
 */
@Getter
@Setter
@ApiModel(value = "卡券取消售出模型")
public class CancelCardSoldForm {
    @ApiModelProperty(value = "优惠券id", required = true)
    @NotNull
    private Integer couponId;
    @ApiModelProperty(value = "组织id", required = true)
    @NotNull
    private Integer orgId;
}
