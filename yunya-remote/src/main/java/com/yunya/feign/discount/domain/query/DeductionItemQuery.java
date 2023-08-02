package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2023/8/29
 */
@Getter
@Setter
@ApiModel(value = "划扣卡券项目查询")
public class DeductionItemQuery {
    @ApiModelProperty(value = "卡券id", required = true)
    @NotNull
    private Integer cardId;
}
