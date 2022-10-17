package com.yunya.feign.ivy_mini.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/10/14
 * @description:
 */
@Data
@ApiModel(description = "各个状态订单数量form")
public class OrderCountQuery {
    @ApiModelProperty("微信用户id")
    @NotNull(message = "用户id不能为空")
    private Integer fansId;
}
