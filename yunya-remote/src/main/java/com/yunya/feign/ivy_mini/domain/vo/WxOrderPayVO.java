package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/7/1 9:53
 **/
@Data
@ApiModel(description = "微信支付订单状态查询")
public class WxOrderPayVO {
    @ApiModelProperty(value = "支付状态（0-支付成功，1-支付中，2-已关闭，3-退款中）")
    private Integer payStatus;
}
