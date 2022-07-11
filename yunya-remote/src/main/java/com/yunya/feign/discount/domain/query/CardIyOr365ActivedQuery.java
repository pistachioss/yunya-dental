package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

/**
 * 简介：艾芽卡or365系列卡券激活查询
 *
 * @author: chenlin
 * @Description: 艾芽卡or365系列卡券激活查询
 * @Date: 2022/7/11 13:38
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("艾芽卡or365系列卡券激活查询")
public class CardIyOr365ActivedQuery implements Serializable {

    /** 产品类型名称列表：默认是[ 365系列礼包、套餐有效期至12.31  ] */
    @ApiModelProperty("产品类型名称")
    private Collection<String> productTypeNames = Arrays.asList("365系列礼包","套餐有效期至12.31");

    /** 卡券类型*/
    @ApiModelProperty("卡券类型")
    private Integer couponType;

    /** 执行日期，默认是明天 */
    @ApiModelProperty("执行日期，默认是明天")
    private LocalDateTime execDate = LocalDateTime.now().plusDays(1);
}
