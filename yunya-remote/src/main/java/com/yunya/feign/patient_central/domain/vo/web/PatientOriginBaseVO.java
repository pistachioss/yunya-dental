package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/7/26 14:14
 * @description: 患者来源基础信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者来源基础信息数据模型")
public class PatientOriginBaseVO implements Serializable {
    /**
     * 患者来源类型 患者来源分类ID
     */
    @ApiModelProperty("患者来源分类ID")
    private Integer originType;

    /**
     * 患者来源类型名称
     */
    @ApiModelProperty("患者来源类型名称")
    private String originTypeName;

    /**
     * 患者来源关联ID 患者来源关联ID（活动ID）
     */
    @ApiModelProperty("患者来源关联ID（活动ID）")
    private Integer originId;

    /**
     * 来源名称 或 推荐人名称
     */
    @ApiModelProperty("来源名称 或 推荐人名称")
    private String originName;
}
