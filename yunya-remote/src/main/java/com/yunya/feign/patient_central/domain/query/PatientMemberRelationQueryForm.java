package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/30 15:31
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者会员卡关联关系")
public class PatientMemberRelationQueryForm implements Serializable {
    /**
     * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 会员卡关联类型 （0：主副卡关联（可用会员卡优惠权益、礼包），1：充值关联（可用会员卡余额））
     */
    @ApiModelProperty(value = "会员卡关联类型 （0：主副卡关联（可用会员卡优惠权益、礼包），1：充值关联（可用会员卡余额））",required = true)
    private Integer bindType;

}
