package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/11/1 9:28
 * @description: 沟通记录修改模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("沟通记录修改模型")
public class PatientCommunicationForm implements Serializable {

    /** 沟通记录id */
    @ApiModelProperty(value = "沟通记录id", required = true)
    @NotNull(message = "沟通记录id不能为空")
    private Integer id;

    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;
    
    /** 沟通内容 */
    @ApiModelProperty("沟通内容")
    private String content;
}
