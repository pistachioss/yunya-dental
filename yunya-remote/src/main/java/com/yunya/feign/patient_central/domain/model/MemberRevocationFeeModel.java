package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 会员卡撤销收费Model
 *
 * @author: WY
 * @date 2020/8/15 15:41
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员卡撤销收费Model")
public class MemberRevocationFeeModel implements Serializable {


    /**
     * 会员卡号
     */
    @ApiModelProperty(value = "会员卡号",required = true)
    private String memberId;

    /**
     * 账单付款记录id
     */
    @ApiModelProperty(value = "账单付款记录id",required = true)
    private Integer billPayRecordId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注",required = true)
    private String remarks;

}
