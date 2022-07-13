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
@ApiModel(description = "虚拟服务订单详情")
public class OrderVirtualDetailVO {
    @ApiModelProperty(value = "订单")
    private PayOrderVO orderVO;
    @ApiModelProperty(value = "订单项目明细")
    private List<PayOrderItemVO> itemVO;
    @ApiModelProperty(value = "虚拟服务激活")
    private VirtualActiveVO activeVO;
    @ApiModelProperty(value = "二维码数据")
    private List<QrCodeVO> qrList;

}
