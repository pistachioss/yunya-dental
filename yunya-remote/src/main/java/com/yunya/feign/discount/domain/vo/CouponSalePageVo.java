package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.io.*;
import java.math.*;

/**
 * @author xiangyang
 * @date 2020/8/25
 */
@Getter
@Setter
@ApiModel(value = "产品售卖查询分页模型")
public class CouponSalePageVo implements Serializable {
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;
    @ApiModelProperty(value = "产品类型")
    private Integer couponType;
    @ApiModelProperty(value = "产品类型")
    private String couponTypeName;
    @ApiModelProperty(value = "售出时间段")
    private String saleSegment;
    @ApiModelProperty(value = "售出金额")
    private BigDecimal soldAmount;
    @ApiModelProperty(value = "分配数量")
    private Integer allocateNum;
    @ApiModelProperty(value = "已售数量")
    private Integer soldNum;
    @ApiModelProperty(value = "组织id")
    private Integer orgId;
}
