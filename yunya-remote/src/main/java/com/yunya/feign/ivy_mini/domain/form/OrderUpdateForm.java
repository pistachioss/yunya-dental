package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/17
 * @description:
 */
@Data
public class OrderUpdateForm {
    private Integer id;
    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    @ApiModelProperty(value = "单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款")
    private Byte status;

    /**
     * 物流公司(配送方式)
     */
    @ApiModelProperty(value = "物流公司(配送方式)")
    private String deliveryCompany;

    /**
     * 物流单号
     */
    @ApiModelProperty(value = "物流单号")
    private String deliverySn;
}
