package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/7/1 10:04
 **/
@Data
@ApiModel(description = "取消订单")
public class CancelVO {
    @ApiModelProperty(value = "订单")
    private OrderDetailVO orderVO;
}
