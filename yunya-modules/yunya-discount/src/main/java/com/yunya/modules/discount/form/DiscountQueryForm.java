package com.yunya.modules.discount.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-13 12:40
 */
@Data
@ApiModel("查询表单")
public class DiscountQueryForm implements Serializable {
    @ApiModelProperty("产品ID")
    private Integer marketProductTypeId;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("开始时间")
    private Date startDate;
    @ApiModelProperty("结束时间")
    private Date endDate;
}
