package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/3/7 10:43
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("混搭优惠")
@ToString
public class MixMatchBenefitModel extends PatientOrderBenefitModel {

    /************************** 授权折扣信息 **************************/
    @ApiModelProperty(value = "授权人id", required = true)
    @NotNull
    private Integer authorizedId;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "授权项目优惠明细")
    private List<AuthItemBenefitModel> itemBenefits;
}
