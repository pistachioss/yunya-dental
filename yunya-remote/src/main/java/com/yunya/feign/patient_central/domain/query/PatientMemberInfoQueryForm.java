package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 查询患者会员信息form
 *
 * @author: WY
 * @date 2020/9/4 14:42
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientMemberInfoQueryForm implements Serializable {

    /**
     *  患者id（主卡人）
     */
    @ApiModelProperty(value = "患者id（主卡人）",required = true)
    private Integer patientId;

    /**
     * 会员卡类型
     */
    @ApiModelProperty(value = "会员卡绑定类型",required = true)
    private Integer bindType;
}
