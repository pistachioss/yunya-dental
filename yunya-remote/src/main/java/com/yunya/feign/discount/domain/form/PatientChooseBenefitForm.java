package com.yunya.feign.discount.domain.form;

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
@ApiModel(value = "计算患者开单项目的优惠明细")
public class PatientChooseBenefitForm implements Serializable {
    /** 订单明细id */
    @ApiModelProperty("订单明细id")
    private List<Integer> orderDetailIds;

    @ApiModelProperty(value = "患者id", required = true)
    @NotNull
    private Integer patientId;
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
    @ApiModelProperty(value = "组织id", required = true)
    @NotNull
    private Integer orgId;
    @ApiModelProperty(value = "会员卡id")
    private Integer memberCardId;
    @ApiModelProperty(value = "折扣券id")
    private Integer discountId;
    @ApiModelProperty(value = "兑换券id集合")
    private List<Integer> exchangeIds;
    @ApiModelProperty(value = "套餐券id集合")
    private List<Integer> packageIds;
    @ApiModelProperty(value = "代金券id集合")
    private List<Integer> voucherIds;
    @ApiModelProperty(value = "划扣券id集合")
    private List<Integer> deductionIds;
}
