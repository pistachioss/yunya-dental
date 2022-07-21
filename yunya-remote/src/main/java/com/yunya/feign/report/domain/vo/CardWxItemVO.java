package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/20
 * @description:
 */
@Data
@ApiModel(value = "小程序权益详情")
@AllArgsConstructor
public class CardWxItemVO {
    @ApiModelProperty("项目名称")
    private String itemName;
    @ApiModelProperty("数量（数量为-1，代表不限次数，固定写死的项目）")
    private Integer originalQuantity;
    @ApiModelProperty("剩余可使用数量（数量为-1，代表不限次数，固定写死的项目）")
    private Integer remainingQuantity;
}
