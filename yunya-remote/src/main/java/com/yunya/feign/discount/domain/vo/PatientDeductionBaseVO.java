package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author xiangyang
 * @date 2023/07/31
 */
@Getter
@Setter
public class PatientDeductionBaseVO implements Serializable {
    @ApiModelProperty(value = "卡券id")
    private Integer cardId;
    @ApiModelProperty(value = "订单id")
    private Integer orderId;
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "产品类型")
    private String couponTypeName;
    @ApiModelProperty(value = "购买人")
    private String buyerName;
    @ApiModelProperty(value = "持有人")
    private String ownName;
    @ApiModelProperty(value = "激活状态(0-未激活、1-已激活)")
    private Integer activeStatus;
    @ApiModelProperty(value = "激活患者id")
    private Integer activePatientId;
    @ApiModelProperty(value = "激活患者")
    private String activePatient;
    @ApiModelProperty(value = "激活患者手机号")
    private String activePatientMobile;
    @ApiModelProperty(value = "使用状态(0-未使用、1-已使用)")
    private Integer useStatus;
    @ApiModelProperty(value = "是否可以转赠(0-否、1-是)")
    private Integer changeStatus;
    @ApiModelProperty(value = "产品分类")
    private String productTypeName;
    @ApiModelProperty(value = "销售渠道")
    private String saleChannelName;
    @ApiModelProperty(value = "使用方式")
    private String useWayName;
    @ApiModelProperty(value = "使用截止时间")
    private String useDeadline;
    @ApiModelProperty(value = "激活时间")
    private LocalDate activeDate;
    @ApiModelProperty(value = "产品是否停用 0-否 1-是")
    private Integer couponEnable;
    @ApiModelProperty("售出渠道（0-艾维小程序；1-pc购买 2-门诊划扣卡购买）")
    private Integer payChannel;
    @ApiModelProperty("产品设计图")
    private String couponLogo;
}
