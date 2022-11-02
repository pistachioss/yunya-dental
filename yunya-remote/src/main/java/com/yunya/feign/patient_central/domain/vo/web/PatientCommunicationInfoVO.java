package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/11/1 10:01
 * @description: 沟通详情数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("沟通详情数据模型")
public class PatientCommunicationInfoVO implements Serializable {

    /** 沟通记录id */
    @ApiModelProperty(value = "沟通记录id")
    private Integer id;

    /** 患者id */
    @ApiModelProperty(value = "患者id")
    private Integer patientId;

    /** 沟通内容 */
    @ApiModelProperty("沟通内容")
    private String content;
}
