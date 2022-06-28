package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/6/15 17:21
 **/
@Data
@ApiModel(description = "确认订单返回")
public class ConfirmOrderVO {
    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）")
    private Integer productType;
    @ApiModelProperty("计算的金额")
    private CalcAmountVO calcAmountVO;
    @ApiModelProperty("用户配送方式")
    private DeliveryOrderVO deliveryVO;
    @ApiModelProperty("产品列表信息")
    private List<PayOrderItemVO> productList;
}
