package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 消费记录Model
 *
 * @author: WY
 * @date 2020/8/27 20:48
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class MemberExpendRecordModel implements Serializable {

    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 会员卡号
     */
    @ApiModelProperty(value = "会员卡号",required = true)
    private String memberId;

    /**
     * 消费总额
     */
    @ApiModelProperty(value = "消费总额",required = true)
    private BigDecimal expendTotal;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;


}
