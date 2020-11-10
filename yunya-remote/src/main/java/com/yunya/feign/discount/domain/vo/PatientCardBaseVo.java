package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.io.*;
import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
@Getter
@Setter
public class PatientCardBaseVo implements Serializable {
    @ApiModelProperty(value = "卡券id")
    private Integer cardId;
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "产品类型")
    private String couponTypeName;
    @ApiModelProperty(value = "产品分类")
    private String productTypeName;
    @ApiModelProperty(value = "使用方式")
    private String useWayName;
    @ApiModelProperty(value = "使用截止时间")
    private String useDeadline;
    @ApiModelProperty(value = "激活时间")
    private LocalDate activeDate;
}
