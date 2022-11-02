package com.yunya.feign.patient_central.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/11/1 9:44
 * @description: 沟通记录查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("沟通记录查询模型")
public class PatientCommunicationQueryForm extends PageQuery implements Serializable {

    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;
}
