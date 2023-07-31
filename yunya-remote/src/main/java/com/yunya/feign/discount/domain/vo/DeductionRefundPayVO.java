package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author xiangyang
 * @date 2023/07/31
 */
@Data
@ApiModel(value = "划扣退费方式模型")
public class DeductionRefundPayVO implements Serializable {
    @ApiModelProperty("卡号（会员或预付款）")
    private String number;
}
