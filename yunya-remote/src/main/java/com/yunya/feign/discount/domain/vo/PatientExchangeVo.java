package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author xiangyang
 * @date 2020/9/9
 */
@Getter
@Setter
public class PatientExchangeVo implements Serializable {
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    @ApiModelProperty(value = "卡券id")
    private Integer cardId;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;
    @ApiModelProperty(value = "使用有效截止时间")
    private String useDeadline;
    @ApiModelProperty(value = "产品共用（0：不共用 1：共用）")
    private Integer mixable;
    @ApiModelProperty(value = "使用方式（0：一次使用 1：多次使用）")
    private Integer useWay;
    @ApiModelProperty(value = "账单单次使用限制数量（页面不用显示）")
    private Integer limitCount;
    @ApiModelProperty(value = "拥有者")
    private String owner;
    @ApiModelProperty(value = "图片路径")
    private String path;
    @ApiModelProperty(value = "是否可以在项目使用优惠（0：否 1：是）")
    private Integer itemUsable;
    @ApiModelProperty(value = "优惠券类型")
    private Integer couponType = 2;
}
