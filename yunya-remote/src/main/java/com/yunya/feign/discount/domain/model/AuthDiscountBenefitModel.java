package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/10/9
 */
@Getter
@Setter
@ApiModel(value = "授权折扣优惠提交")
public class AuthDiscountBenefitModel {
    @ApiModelProperty(value = "订单id", required = true)
    @NotNull
    private Integer orderId;
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull
    private Integer patientId;
    @ApiModelProperty(value = "组织id", required = true)
    @NotNull
    private Integer orgId;
    @ApiModelProperty(value = "授权人id", required = true)
    @NotNull
    private Integer authorizedId;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "授权项目优惠明细")
    private List<AuthItemBenefitModel> itemBenefits;
}
