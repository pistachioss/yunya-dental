package com.yunya.modules.discount.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-16 12:57
 */
@Data
@ApiModel("套餐明细")
public class PackageDetailNode implements Serializable {
    @ApiModelProperty("明细ID")
    private Integer tariffId;
    @ApiModelProperty("数量")
    private Integer count;
    @ApiModelProperty("单价")
    private BigDecimal price;
    @ApiModelProperty("工作量比例")
    private Integer workload;
}
