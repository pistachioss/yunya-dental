package com.yunya.feign.discount.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description: 优惠和补入查询模型
 * @Date: 2021/11/4 15:32
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("优惠和补入查询模型")
public class DiscountCouponQuery implements Serializable {

    @ApiModelProperty("开单ID列表")
    private Collection<Integer> orderRecordIds;

    @ApiModelProperty("日期类型：0-日，1-月，2-年")
    private Byte dateType;

    @ApiModelProperty("开始日期")
    private String startDate;

    @ApiModelProperty("结束日期")
    private String endDate;
}
