package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/6/15 17:21
 **/
@Data
public class DeliveryOrderVO {
    @ApiModelProperty("自提")
    private PayReceiveAddressVO pickUp;
    @ApiModelProperty("配送")
    private PayReceiveAddressVO delivery;
}
