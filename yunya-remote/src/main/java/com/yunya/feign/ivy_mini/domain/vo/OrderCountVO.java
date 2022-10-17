package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/10/14
 * @description:
 */
@Data
@ApiModel(description = "各个状态订单数量")
public class OrderCountVO {
    @ApiModelProperty("待发货")
    private Integer delivery;
    @ApiModelProperty("待收货")
    private Integer received;
    @ApiModelProperty("待使用")
    private Integer used;

}
