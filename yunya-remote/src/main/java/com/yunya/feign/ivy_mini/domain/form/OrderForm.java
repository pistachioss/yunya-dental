package com.yunya.feign.ivy_mini.domain.form;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/17
 * @description:
 */
@Data
@ApiModel(value = "订单管理")
public class OrderForm extends PageQuery implements Serializable {
    @ApiModelProperty(value = "后端使用")
    private List<Integer> nameList;
    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款
     */
    @ApiModelProperty(value = "单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款")
    private Byte status;

    /**
     * 订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     */
    @ApiModelProperty(value = "订单类型：0->正常订单；1->秒杀订单；2-拼团订单")
    private Byte orderType;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String crtTime;

    @ApiModelProperty(value = "收货人信息")
    private String name;
}
