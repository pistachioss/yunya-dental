package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/7/26 14:14
 * @description: 患者的推荐人信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者的推荐人信息数据模型")
public class PatientReferrerInfoVO implements Serializable {

    /** 转介绍类型：1-员工转介绍，2-患者转介绍 */
    @ApiModelProperty("转介绍类型：1-员工转介绍，2-患者转介绍")
    private Integer originType;

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

    /** 推荐人手机号 */
    @ApiModelProperty("推荐人手机号")
    private String mobile;
}
