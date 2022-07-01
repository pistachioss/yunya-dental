package com.yunya.feign.ivy_mini.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: xy
 * @date 2022/6/30 14:15
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "我的订单查询")
public class MyOrderQuery extends PageQuery {
    @ApiModelProperty(value = "订单状态（0->待付款；1->待发货；2->待收货；3->已完成；4->已关闭；5->退款中）")
    private Integer status;
}
