package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简单介绍:</br> 会员卡关联关系
 *
 * @author: WY
 * @date 2020/7/30 17:49
 * @description: 会员卡关联关系
 * @since: 1.0.0
 */
@Data
@ToString
public class MemberBindingRelationInfoModel implements Serializable {

    /**   * 患者id
     */
    @NotNull(message = "患者id不能为空")
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 诊所Id
     */
    @NotNull(message = "诊所Id不能为空")
    @ApiModelProperty(value = "诊所Id")
    private Integer orgId;

    /**
     * 关联人名称
     */
    @NotNull(message = "关联人名称不能为空")
    @ApiModelProperty(value = "关联人名称")
    private String name;

    /**
     * 主卡人患者id
     */
    @NotNull(message = "主卡人患者id不能为空")
    @ApiModelProperty(value = "主卡人患者id",required = true)
    private Integer masterCardId;

    /**
     * 副卡人患者ID
     */
    @NotNull(message = "副卡会员人ID不能为空")
    @ApiModelProperty(value = "副卡会员人ID",required = true)
    private Integer secondaryCardId;

    /**
     * 关联类型
     */
    @NotNull(message = "关联类型不能为空")
    @ApiModelProperty(value = "关联类型",required = true)
    private Byte bindType;

}
