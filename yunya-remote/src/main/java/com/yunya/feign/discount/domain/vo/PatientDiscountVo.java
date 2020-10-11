package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.io.*;
import java.time.*;

/**
 * @author xiangyang
 * @date 2020/9/9
 */
@Getter
@Setter
public class PatientDiscountVo implements Serializable {
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    @ApiModelProperty(value = "卡券id")
    private Integer cardId;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;
    @ApiModelProperty(value = "使用有效截止时间")
    private LocalDate useDeadline;
    @ApiModelProperty(value = "产品共用（0：不共用 1：共用）")
    private Integer mixable;
    @ApiModelProperty(value = "拥有者")
    private String owner;
    @ApiModelProperty(value = "图片路径")
    private String path;
    @ApiModelProperty(value = "是否可以在项目使用优惠（0：否 1：是）")
    private Integer itemUsable;
}
