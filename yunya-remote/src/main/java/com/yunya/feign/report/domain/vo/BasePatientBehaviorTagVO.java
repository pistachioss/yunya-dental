package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/5/15 14:53
 * @description: 患者行为标签数据模型
 * @since: 1.0.0
 */
@Builder
@Data
@ToString
@ApiModel("患者行为标签数据模型")
@AllArgsConstructor
@NoArgsConstructor
public class BasePatientBehaviorTagVO implements Serializable {
    
    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 行为标签名 */
    @ApiModelProperty("行为标签名")
    private String tagName;
}
