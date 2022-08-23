package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/7/1 10:04
 **/
@Data
@ApiModel(description = "订单详情")
public class OrderDetailVO {
    @ApiModelProperty(value = "订单")
    private PayOrderVO orderVO;
    @ApiModelProperty(value = "订单项目明细")
    private List<PayOrderItemVO> itemVO;
    @ApiModelProperty(value = "收货地址")
    private PayReceiveAddressVO addressVO;

}
