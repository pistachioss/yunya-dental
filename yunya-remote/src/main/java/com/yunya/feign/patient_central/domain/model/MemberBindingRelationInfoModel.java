package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/30 17:49
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberBindingRelationInfoModel implements Serializable {

    /**   * 患者id
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 诊所Id
     */
    @ApiModelProperty(value = "患者id")
    private Integer orgId;

    /**
     * 关联人名称
     */
    @ApiModelProperty(value = "患者id")
    private String name;

    /**
     * 主卡会员人ID
     */
    @ApiModelProperty(value = "主卡会员人ID",required = true)
    private Integer masterCardId;

    /**
     * 副卡会员人ID
     */
    @ApiModelProperty(value = "副卡会员人ID",required = true)
    private Integer secondaryCardId;

    /**
     * 关联类型
     */
    @ApiModelProperty(value = "关联类型",required = true)
    private Byte bindType;

}
