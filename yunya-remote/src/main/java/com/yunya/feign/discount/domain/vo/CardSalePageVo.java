package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.io.*;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/8/25
 */
@Getter
@Setter
@ApiModel(value = "产品售卖-查看配给分页模型")
public class CardSalePageVo implements Serializable {
    @ApiModelProperty(value = "卡券id")
    private Integer id;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "卡密")
    private String cardPassword;
    @ApiModelProperty(value = "售出对象")
    private String soldTarget;
    @ApiModelProperty(value = "售出对象手机号")
    private String soldPhoneNumber;
    @ApiModelProperty(value = "售出类型")
    private String soldTypeName;
    @ApiModelProperty(value = "链接（卡券二维码数据）")
    private String link;
    @ApiModelProperty(value = "售卖状态")
    private String soldStatusName;
    @ApiModelProperty(value = "是否已收费")
    private String payStatus;
    @ApiModelProperty(value = "线上/线下")
    private String soldWayName;

    @ApiModelProperty("卡券名称")
    private String cardName;
    @ApiModelProperty("售出金额")
    private BigDecimal soldAmount;
    @ApiModelProperty("支付方式")
    private String soldType;
    @ApiModelProperty("支付方式Id")
    private Integer payId;
    @ApiModelProperty("门诊名称")
    private String orgName;
    @ApiModelProperty("门诊地址")
    private String orgAddress;
    @ApiModelProperty("门诊电话")
    private String orgIphone;

}
