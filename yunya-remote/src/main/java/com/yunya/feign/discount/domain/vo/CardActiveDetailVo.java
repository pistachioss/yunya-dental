package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.math.*;

/**
 * @author xiangyang
 * @date 2020/8/28
 */
@Getter
@Setter
@ApiModel(value = "卡券激活详情模型")
public class CardActiveDetailVo {
    @ApiModelProperty(value = "卡券id")
    private Integer id;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "售出对象")
    private String soldTarget;
    @ApiModelProperty(value = "售出对象手机号")
    private String soldPhoneNumber;
    @ApiModelProperty(value = "已支付金额（不需要收费时候展示）（充值卡的本金）")
    private BigDecimal paidAmount;
    @ApiModelProperty(value = "卡券售出金额（需要收费时候展示）")
    private BigDecimal soldAmount;
    @ApiModelProperty(value = "是否需要收费（0：否 1：是）")
    private Integer charge;
    @ApiModelProperty(value = "赠金")
    private BigDecimal bonus;
    @ApiModelProperty(value = "售出类型")
    private String soldTypeName;
}
