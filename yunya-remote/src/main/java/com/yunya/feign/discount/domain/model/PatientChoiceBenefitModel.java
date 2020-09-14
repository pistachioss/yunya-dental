package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.*;
import lombok.*;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/9/4
 */
@Getter
@Setter
@ApiModel(value = "患者选择优惠模型")
public class PatientChoiceBenefitModel {
    @ApiModelProperty(value = "患者id")
    private Integer patientId;
    @ApiModelProperty(value = "兑换券id集合")
    private List<Integer> packageCouponIds;
    @ApiModelProperty(value = "特殊套餐券id集合")
    private List<Integer> specialPackageCouponIds;
    @ApiModelProperty(value = "折扣券id集合")
    private List<Integer> discountCouponIds;
    @ApiModelProperty(value = "会员卡id集合")
    private List<Integer> memberCardIds;
    @ApiModelProperty(value = "代金券id集合")
    private List<Integer> voucherCouponIds;
}
