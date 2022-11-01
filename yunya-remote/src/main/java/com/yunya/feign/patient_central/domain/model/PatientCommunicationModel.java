package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/11/1 9:17
 * @description: 患者沟通添加模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者沟通添加模型")
public class PatientCommunicationModel implements Serializable {
    
    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;
    
    /** 沟通内容 */
    @ApiModelProperty("沟通内容")
    private String content;
}
