package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：账单信息响应模型
 *
 * @author: chenlin
 * @Description: 账单信息响应模型
 * @Date: 2021/1/21 15:55
 * @since: 1.0.0
 */
@ApiModel("账单信息响应模型")
@ToString
@Data
public class BaseBillDetailVO implements Serializable {

    @ApiModelProperty("账单单详情id")
    private Integer billDetailId;

    @ApiModelProperty("执行人")
    private String operateUserName;

    @ApiModelProperty("项目类型")
    private Byte itemType;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("项目名称")
    private String itemName;
}
