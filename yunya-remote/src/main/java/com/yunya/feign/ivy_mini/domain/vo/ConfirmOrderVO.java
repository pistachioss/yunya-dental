package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/6/15 17:21
 **/
@Data
@ApiModel(description = "确认订单返回")
public class ConfirmOrderVO {
    @ApiModelProperty("计算的金额")
    private CalcAmountVO calcAmountVO;
    @ApiModelProperty("用户收货地址")
    private PayReceiveAddressVO addressVO;
    @ApiModelProperty("产品列表信息")
    private List<PayOrderItemVO> productList;
}
