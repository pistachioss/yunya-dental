package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2022/10/20 17:33
 **/
@Data
@ApiModel(value = "pc小程序订单导出")
public class PcOrderExportForm {
    @ApiModelProperty(value = "订单查询条件")
    private OrderForm form;
    @ApiModelProperty(value = "复选框选已选订单")
    private List<Integer> orderIds;
}
