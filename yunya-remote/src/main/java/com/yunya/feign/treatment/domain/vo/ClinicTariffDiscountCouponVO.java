package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：门诊项目金额补入VO
 *
 * @author: chenlin
 * @Description: 门诊项目金额补入VO
 * @Date: 2021/11/3 17:39
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊项目金额补入VO")
public class ClinicTariffDiscountCouponVO implements Serializable {
    /** 门诊ID */
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    /** 项目id */
    @ApiModelProperty("项目id")
    private Integer itemId;

    /** 类型（0-价目表；1-商品；） */
    @ApiModelProperty("类型（0-价目表；1-商品；）")
    private Integer itemType;

    @ApiModelProperty("金额")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @ApiModelProperty("补入工作量")
    private BigDecimal supplyWorkload = BigDecimal.ZERO;
}
