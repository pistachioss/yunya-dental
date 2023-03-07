package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/9/14
 */
@Data
@ApiModel(value = "卡券优惠提交")
public class PatientOrderBenefitModel implements Serializable {
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull
    private Integer patientId;
    @ApiModelProperty(value = "组织id", required = true)
    @NotNull
    private Integer orgId;
    @ApiModelProperty(value = "会员卡id")
    private Integer memberCardId;
    @ApiModelProperty(value = "折扣券id")
    private Integer discountId;
    @ApiModelProperty(value = "兑换券id集合")
    private List<Integer> exchangeIds;
    @ApiModelProperty(value = "特殊套餐券id集合")
    private List<Integer> packageIds;
    @ApiModelProperty(value = "代金券id集合")
    private List<Integer> voucherIds;
    @ApiModelProperty(value = "优惠方式（0-卡券优惠  1-授权折扣 2-混搭优惠（价目使用卡券+商品使用折扣））")
    private Integer benefitType;
}
